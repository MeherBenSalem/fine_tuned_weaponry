import json
import os
import unittest
from pathlib import Path

from resources import jar_tags, resolve_packs


class AnvilResources(unittest.TestCase):
    artifacts = Path(os.environ['FTW_TEST_ARTIFACTS'])
    root = Path(__file__).resolve().parents[2]

    def packs(self, workspace, loader):
        version = '1.20.1' if workspace == '1.20.1' else '1.21.1'
        vanilla = json.loads((self.artifacts / f'vanilla-tags-{version}.json').read_text())
        if loader == 'fabric':
            api = '0.92.1+1.20.1' if version == '1.20.1' else '0.109.0+1.21.1'
            loader_pack = json.loads((self.artifacts / f'fabric-tags-{api}.json').read_text())
        else:
            jar = 'forge-1.20.1-47.3.0-universal.jar' if loader == 'forge' else 'neoforge-21.1.80-universal.jar'
            loader_pack = jar_tags(self.artifacts / jar)
        resource_root = self.root / workspace / 'common/src/main/resources'
        mod = {p.relative_to(resource_root).as_posix(): json.loads(p.read_text())
               for p in (resource_root / 'data').rglob('*.json') if '/tags/' in p.as_posix()}
        return version, vanilla, loader_pack, mod

    def test_current_release_mismatch(self):
        for loader in ['fabric', 'neoforge']:
            version, vanilla, pack, _ = self.packs('26.2', loader)
            released = jar_tags(self.artifacts / f'ftw-{loader}-{version}-2.3.0.jar')
            tags = resolve_packs([vanilla, pack, released], version, 'c:tools')
            self.assertNotIn('forge:tools', tags)
            self.assertIn('minecraft:diamond_sword', tags['c:tools'])
            self.assertIn('fine_tuned_weaponry:classic_katana', tags['c:tools'])
            self.assertNotIn('fine_tuned_weaponry:gem', tags)
            self.assertNotIn('fine_tuned_weaponry:amp', tags)

    def test_all_supported_workspaces_and_loaders(self):
        for workspace, loaders in [('26.2', ['fabric', 'neoforge']), ('1.21.1', ['fabric', 'neoforge']), ('1.20.1', ['fabric', 'forge'])]:
            for loader in loaders:
                with self.subTest(workspace=workspace, loader=loader):
                    version, vanilla, pack, mod = self.packs(workspace, loader)
                    convention = 'c:tools' if version == '1.21.1' else 'c:swords'
                    tags = resolve_packs([vanilla, pack, mod], version, convention)
                    accepted = tags['fine_tuned_weaponry:anvil_tools']
                    for item in ['minecraft:diamond_sword', 'minecraft:diamond_axe', 'minecraft:diamond_pickaxe',
                                 'minecraft:diamond_shovel', 'minecraft:diamond_hoe', 'minecraft:bow',
                                 'minecraft:crossbow', 'minecraft:shield', 'minecraft:trident', 'minecraft:fishing_rod',
                                 'anvil_test:third_party_weapon']:
                        self.assertIn(item, accepted)
                    for item in ['minecraft:dirt', 'minecraft:stick', 'minecraft:diamond_chestplate',
                                 'fine_tuned_weaponry:inferno_core', 'fine_tuned_weaponry:blazing_amplifier']:
                        self.assertNotIn(item, accepted)
                    native_tags = ['minecraft:swords', 'minecraft:axes']
                    native = {i for tag in native_tags for i in tags[tag] if i.startswith('fine_tuned_weaponry:')}
                    self.assertEqual(14, len(native))
                    self.assertTrue(native.issubset(accepted))
                    self.assertEqual(3, len(tags['fine_tuned_weaponry:gem']))
                    self.assertEqual(16, len(tags['fine_tuned_weaponry:amp']))
                    if loader == 'forge':
                        self.assertTrue(set(tags['forge:tools']).issubset(accepted))
                    if version == '1.20.1':
                        for item in ['minecraft:brush', 'minecraft:shears', 'minecraft:flint_and_steel']:
                            self.assertNotIn(item, accepted)
                    else:
                        for item in ['minecraft:brush', 'minecraft:shears', 'minecraft:flint_and_steel', 'minecraft:mace']:
                            self.assertIn(item, accepted)

    def test_optional_legacy_tag_and_pack_extensions(self):
        version, vanilla, loader, mod = self.packs('26.2', 'fabric')
        extension = {'data/forge/tags/item/tools.json': {'values': ['compat:legacy_weapon']},
                     'data/fine_tuned_weaponry/tags/item/anvil_tools.json': {'values': ['compat:explicit_weapon']}}
        tags = resolve_packs([vanilla, loader, mod, extension], version, 'c:tools')
        self.assertIn('compat:legacy_weapon', tags['fine_tuned_weaponry:anvil_tools'])
        self.assertIn('compat:explicit_weapon', tags['fine_tuned_weaponry:anvil_tools'])

    def test_paired_121_workspaces_have_identical_anvil_logic(self):
        for relative in ['util/ModTags.java', 'world/inventory/WeaponsAnvilGUIMenu.java',
                         'procedures/InsertGemsProcedure.java', 'procedures/RemoveGemProcedure.java', 'util/GemNbtKeys.java']:
            self.assertEqual((self.root / '26.2/common/src/main/java/com/naizo/finetuned' / relative).read_text(),
                             (self.root / '1.21.1/common/src/main/java/com/naizo/finetuned' / relative).read_text())


if __name__ == '__main__':
    unittest.main(verbosity=2)
