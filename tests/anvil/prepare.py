"""Prepare public, immutable versioned artifacts and extract existing cached tags."""
import argparse
import hashlib
import io
import json
import urllib.request
import zipfile
from pathlib import Path

parser = argparse.ArgumentParser()
parser.add_argument('--cache', type=Path, required=True, help='Existing Gradle caches directory; read only')
parser.add_argument('--output', type=Path, required=True)
args = parser.parse_args()
args.output.mkdir(parents=True, exist_ok=True)
downloads = {
    'ftw-neoforge-1.21.1-2.3.0.jar': ('https://mediafilez.forgecdn.net/files/8777/615/fine_tuned_weaponry-neoforge-1.21.1-2.3.0.jar', 'f7f3d389b3773c1b4d4c9f8a80f859dcd0410835280c1e2d7ab66eb9f4bc4184'),
    'ftw-fabric-1.21.1-2.3.0.jar': ('https://mediafilez.forgecdn.net/files/8777/614/fine_tuned_weaponry-fabric-1.21.1-2.3.0.jar', '68cebda64abde34884c63dd418b4df3f959ec8f32858310988fbd9ddd44869af'),
    'ftw-forge-1.20.1-2.3.0.jar': ('https://mediafilez.forgecdn.net/files/8777/708/fine_tuned_weaponry-forge-1.20.1-2.3.0.jar', '319d9251cb270e5893a392f203b0607398bbb2e53a69643a8f504b03bcfe01a0'),
    'ftw-fabric-1.20.1-2.3.0.jar': ('https://mediafilez.forgecdn.net/files/8777/706/fine_tuned_weaponry-fabric-1.20.1-2.3.0.jar', 'e9f14003a502a98efe0194a16417c5f43f4b6ede3d92dba3c17522504fab8879'),
    'neoforge-21.1.80-universal.jar': ('https://maven.neoforged.net/releases/net/neoforged/neoforge/21.1.80/neoforge-21.1.80-universal.jar', '2abd02395e8049d4d6f079f75720164574373b25164381e2610a023e1a020600'),
    'forge-1.20.1-47.3.0-universal.jar': ('https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-47.3.0/forge-1.20.1-47.3.0-universal.jar', 'a3c07e6b33fe8a4b42fdb4841a37e3ffda62797366c7050c481d799d6d35831c'),
}
manifest = {}
for name, (url, expected_hash) in downloads.items():
    target = args.output / name
    if not target.exists():
        with urllib.request.urlopen(url, timeout=60) as response:
            target.write_bytes(response.read())
    actual_hash = hashlib.sha256(target.read_bytes()).hexdigest()
    if actual_hash != expected_hash:
        raise ValueError(f'Artifact checksum mismatch: {name}')
    manifest[name] = {'url': url, 'sha256': actual_hash}
for version in ['1.21.1', '1.20.1']:
    cached = args.cache / f'fabric-loom/{version}/minecraft-client.jar'
    with zipfile.ZipFile(cached) as jar:
        tags = {n: json.loads(jar.read(n)) for n in jar.namelist()
                if n.startswith('data/minecraft/tags/') and n.endswith('.json')}
    (args.output / f'vanilla-tags-{version}.json').write_text(json.dumps(tags), encoding='utf-8')
    manifest[f'vanilla-tags-{version}.json'] = {'source_jar_sha256': hashlib.sha256(cached.read_bytes()).hexdigest()}
for api in ['0.109.0+1.21.1', '0.92.1+1.20.1']:
    cached = next((args.cache / 'modules-2/files-2.1/net.fabricmc.fabric-api/fabric-api' / api).rglob(f'fabric-api-{api}.jar'))
    tags = {}
    with zipfile.ZipFile(cached) as jar:
        for name in jar.namelist():
            if 'convention' in name and name.endswith('.jar'):
                with zipfile.ZipFile(io.BytesIO(jar.read(name))) as nested:
                    tags.update({n: json.loads(nested.read(n)) for n in nested.namelist()
                                 if n.startswith('data/') and '/tags/' in n and n.endswith('.json')})
    (args.output / f'fabric-tags-{api}.json').write_text(json.dumps(tags), encoding='utf-8')
    manifest[f'fabric-tags-{api}.json'] = {'source_jar_sha256': hashlib.sha256(cached.read_bytes()).hexdigest()}
(args.output / 'test-artifact-manifest.json').write_text(json.dumps(manifest, indent=2), encoding='utf-8')
print('Verified public fixture checksums and extracted cached vanilla/Fabric tag resources.')
