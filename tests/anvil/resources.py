"""Resolve versioned item-tag resources for the native headless menu regression.

Inputs are authentic vanilla, loader and published mod JAR resources. Source
resources may replace the published mod pack for post-fix regression runs.
No client, loader lifecycle or Minecraft resource-manager execution is claimed.
"""
import argparse
import json
import zipfile
from pathlib import Path


def jar_tags(path):
    with zipfile.ZipFile(path) as jar:
        return {n: json.loads(jar.read(n)) for n in jar.namelist()
                if n.startswith('data/') and '/tags/' in n and n.endswith('.json')}


def resolve_packs(packs, version, third_party_tag):
    folder = 'item' if version == '1.21.1' else 'items'
    merged = {}
    for pack in packs:
        for path, content in pack.items():
            parts = path.split('/')
            if len(parts) < 5 or parts[2:4] != ['tags', folder]:
                continue
            key = parts[1] + ':' + '/'.join(parts[4:])[:-5]
            if content.get('replace', False):
                merged[key] = []
            merged.setdefault(key, []).extend(content['values'])
    merged.setdefault(third_party_tag, []).append('anvil_test:third_party_weapon')
    results = {}

    def resolve(key, chain=()):
        if key in results:
            return results[key]
        if key not in merged or key in chain:
            return None
        values = set()
        for entry in merged[key]:
            entry_id = entry if isinstance(entry, str) else entry['id']
            required = isinstance(entry, str) or entry.get('required', True)
            nested = resolve(entry_id[1:], (*chain, key)) if entry_id.startswith('#') else {entry_id}
            if nested is None:
                if required:
                    return None
            else:
                values.update(nested)
        results[key] = sorted(values)
        return results[key]

    for key in merged:
        resolve(key)
    return results


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('artifacts', type=Path)
    parser.add_argument('loader', choices=['neoforge', 'forge', 'fabric'])
    parser.add_argument('version', choices=['1.21.1', '1.20.1'])
    parser.add_argument('output', type=Path)
    parser.add_argument('--source', type=Path)
    parser.add_argument('--mod-jar', type=Path, help='Inspect a newly built JAR instead of the published mod pack')
    args = parser.parse_args()
    artifact = args.artifacts
    vanilla = json.loads((artifact / f'vanilla-tags-{args.version}.json').read_text())
    if args.loader == 'fabric':
        api = '0.109.0+1.21.1' if args.version == '1.21.1' else '0.92.1+1.20.1'
        loader = json.loads((artifact / f'fabric-tags-{api}.json').read_text())
    else:
        jar = 'neoforge-21.1.80-universal.jar' if args.loader == 'neoforge' else 'forge-1.20.1-47.3.0-universal.jar'
        loader = jar_tags(artifact / jar)
    mod = jar_tags(args.mod_jar or artifact / f'ftw-{args.loader}-{args.version}-2.3.0.jar')
    if args.source:
        mod = {p.relative_to(args.source).as_posix(): json.loads(p.read_text())
               for p in (args.source / 'data').rglob('*.json') if '/tags/' in p.as_posix()}
    third_party = 'c:tools' if args.version == '1.21.1' else 'c:swords'
    folder = 'item' if args.version == '1.21.1' else 'items'
    extension = {f'data/forge/tags/{folder}/tools.json': {'values': ['anvil_test:legacy_weapon']}}
    if any(path.endswith('/anvil_tools.json') for path in mod):
        extension[f'data/fine_tuned_weaponry/tags/{folder}/anvil_tools.json'] = {'values': ['anvil_test:explicit_weapon']}
    tags = resolve_packs([vanilla, loader, mod, extension], args.version, third_party)
    args.output.write_text(json.dumps(tags, indent=2))
    print(args.loader, args.version, {tag: len(tags.get(tag, [])) for tag in
          ['forge:tools', 'c:tools', 'fine_tuned_weaponry:anvil_tools', 'fine_tuned_weaponry:gem', 'fine_tuned_weaponry:amp']})
