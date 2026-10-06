"""Compile/run focused common-code tests with existing cached Minecraft libraries.

Requires a development cache populated by a supported native build. Never installs
tools or writes to the supplied cache. See README.md for evidence boundaries.
"""
import argparse
import json
import os
import subprocess
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--cache', type=Path, required=True)
parser.add_argument('--version', choices=['1.21.1', '1.20.1'], default='1.21.1')
parser.add_argument('--release', type=Path, required=True)
parser.add_argument('--tags', type=Path, required=True)
parser.add_argument('--classes', type=Path, required=True)
parser.add_argument('--workspace', type=Path)
parser.add_argument('--baseline', action='store_true')
args = parser.parse_args()
cache = args.cache.resolve()
here = Path(__file__).resolve().parent
mc = next((cache / 'fabric-loom/minecraftMaven/net/minecraft/minecraft-merged').glob(
    f'{args.version}-*/minecraft-merged-*.jar'))
info = json.loads((cache / f'fabric-loom/{args.version}/mojang_minecraft_info.json').read_text())
jars = [mc]
for lib in info['libraries']:
    artifact = lib.get('downloads', {}).get('artifact')
    if not artifact:
        continue
    # No renderer is started; platform native libraries are unnecessary.
    group, name, version, *classifier = lib['name'].split(':')
    if classifier:
        continue
    jars.append(next((cache / 'modules-2/files-2.1' / group / name / version).rglob(Path(artifact['path']).name)))
jars.append(args.release.resolve())
annotations = cache / 'modules-2/files-2.1/com.google.code.findbugs/jsr305/3.0.1'
jars.append(next(annotations.rglob('jsr305-3.0.1.jar')))
classpath = os.pathsep.join(map(str, jars))
args.classes.mkdir(parents=True, exist_ok=True)
sources = [here / 'AnvilRegression.java', here / 'fixtures/FineTunedWeaponryModItems.java',
           here / 'fixtures/FineTunedWeaponryModBlockEntities.java']
if args.workspace:
    common = args.workspace.resolve() / 'common/src/main/java/com/naizo/finetuned'
    sources.extend(common / source for source in ['util/ModTags.java', 'world/inventory/WeaponsAnvilGUIMenu.java',
        'procedures/InsertGemsProcedure.java', 'procedures/RemoveGemProcedure.java', 'util/GemNbtKeys.java'])
    if args.version == '1.20.1':
        # Forge's published 1.20.1 methods use SRG names. Compile these common
        # classes against named vanilla instead; this is source-level regression.
        sources.extend(common / source for source in ['registry/RegistryHolder.java', 'Constants.java',
            'item/ClassicKatanaItem.java', 'block/entity/WeaponsAnvilBlockEntity.java'])
compile_result = subprocess.run(['javac', '-proc:none', '-cp', classpath, '-d', str(args.classes), *map(str, sources)])
if compile_result.returncode:
    raise SystemExit(compile_result.returncode)
command = ['java', '-cp', str(args.classes.resolve()) + os.pathsep + classpath,
           'AnvilRegression', str(args.tags.resolve())]
command.append('baseline' if args.baseline else args.version)
raise SystemExit(subprocess.run(command, cwd=args.classes.resolve()).returncode)
