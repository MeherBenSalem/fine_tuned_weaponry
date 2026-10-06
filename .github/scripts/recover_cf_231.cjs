// Metadata-only recovery: no builds, tags, Modrinth writes or replacement files.
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const targets = [
  { workspace: '1.20.1', loader: 'fabric', game: '1.20.1', loaderId: 7499,
    sha256: '49bf93dc4da58664eb7e1b08861d855d9d4b05d48e44dca745b150c159a3fd19' },
  { workspace: '1.20.1', loader: 'forge', game: '1.20.1', loaderId: 7498,
    sha256: '3ac8ae4d2fdf882edfca9dad934d8494c0c3cec39bb1aa16d816f12bfa2bc7f0' },
  { workspace: '26.2', loader: 'fabric', game: '1.21.1', loaderId: 7499,
    sha256: '12e24c0475d0e0a44b30209d6ddedf44bcb8fde25fb3cb15b4bda09ec8adcd8e' },
  { workspace: '26.2', loader: 'neoforge', game: '1.21.1', loaderId: 10150,
    sha256: '22dbe320ca70c7712a38f871480b5da9225658792b5442ce39c7831fd05fb011' },
];
const fileName = target => `fine_tuned_weaponry-${target.loader}-${target.game}-2.3.1.jar`;
const hash = (data, algorithm) => crypto.createHash(algorithm).update(data).digest('hex');

function metadataFor(target, changelog, versions) {
  const minecraft = versions.find(version => version.name === target.game);
  if (!minecraft || !Number.isInteger(minecraft.id)) throw new Error('Missing game version ' + target.game);
  const gameVersions = [minecraft.id, target.loaderId];
  for (const name of ['Client', 'Server']) {
    const version = versions.find(candidate => candidate.name === name);
    if (version && Number.isInteger(version.id)) gameVersions.push(version.id);
  }
  return { changelog, changelogType: 'markdown', displayName: `2.3.1 · ${target.loader} · ${target.game}`,
    gameVersions, releaseType: 'release',
    relations: { projects: [{ slug: 'jauml', projectID: 1281310, type: 'requiredDependency' }] } };
}

async function recover() {
  if (!process.env.CURSEFORGE_TOKEN || !process.env.CURSEFORGE_API_KEY) throw new Error('Existing CurseForge authentication unavailable');
  const changelog = fs.readFileSync('CHANGELOG.md', 'utf8');
  if (!changelog.startsWith('# Fine Tuned Weaponry 2.3.1')) throw new Error('Unexpected release notes');
  const files = targets.map(target => {
    const filename = fileName(target);
    const jar = path.join('release-artifacts', target.workspace, target.loader, 'build/libs', filename);
    const data = fs.readFileSync(jar);
    if (hash(data, 'sha256') !== target.sha256) throw new Error('Immutable release bytes differ: ' + filename);
    return { ...target, filename, data };
  });
  const apiHeaders = { 'x-api-key': process.env.CURSEFORGE_API_KEY };
  const existing = new Map();
  for (let index = 0; ; index += 50) {
    const response = await fetch(`https://api.curseforge.com/v1/mods/1150827/files?pageSize=50&index=${index}`, { headers: apiHeaders });
    if (!response.ok) throw new Error('CurseForge duplicate preflight ' + response.status);
    const page = await response.json();
    for (const file of page.data) existing.set(file.fileName, file);
    if (index + page.data.length >= page.pagination.totalCount) break;
    if (!page.data.length) throw new Error('Incomplete CurseForge file listing');
  }
  // Validate every existing target before making the first write.
  for (const file of files) {
    const prior = existing.get(file.filename);
    if (prior && (!prior.hashes.some(h => h.algo === 1 && h.value === hash(file.data, 'sha1')) ||
        !prior.dependencies.some(d => d.modId === 1281310 && d.relationType === 3) ||
        !prior.gameVersions.includes(file.game) || !prior.gameVersions.includes(file.loader === 'neoforge' ? 'NeoForge' : file.loader === 'forge' ? 'Forge' : 'Fabric'))) {
      throw new Error('Existing file differs; inspect before retry: ' + prior.id);
    }
  }
  const versionResponse = await fetch('https://minecraft.curseforge.com/api/game/versions', {
    headers: { 'X-Api-Token': process.env.CURSEFORGE_TOKEN },
  });
  if (!versionResponse.ok) throw new Error('CurseForge version lookup ' + versionResponse.status);
  const payload = await versionResponse.json();
  const versions = (payload.data ?? payload).flatMap(entry => entry.versions ?? [entry]);
  const receipts = [];
  const save = () => fs.writeFileSync('cf-recovery-receipts.json', JSON.stringify(receipts, null, 2));
  for (const file of files) {
    const metadata = metadataFor(file, changelog, versions);
    let id = existing.get(file.filename)?.id;
    if (!id) {
      const form = new FormData();
      form.append('metadata', JSON.stringify(metadata));
      form.append('file', new Blob([file.data]), file.filename);
      const response = await fetch('https://minecraft.curseforge.com/api/projects/1150827/upload-file', {
        method: 'POST', headers: { 'X-Api-Token': process.env.CURSEFORGE_TOKEN }, body: form,
      });
      const body = await response.text();
      if (!response.ok) throw new Error('CurseForge ' + response.status + ' ' + body.slice(0, 500));
      id = JSON.parse(body).id;
      if (!Number.isInteger(id)) throw new Error('CurseForge upload returned no integer file id');
    }
    receipts.push({ id, filename: file.filename, sha256: file.sha256, metadata,
      source_artifact_id: 11421202405, source_commit: '4b0555836962326c4110aeb4ebb7626264a4034c',
      action: existing.has(file.filename) ? 'verified_existing' : 'uploaded' });
    save();
    console.log('CurseForge OK', id, metadata.displayName, file.filename, 'sha256=' + file.sha256);
    const readback = await fetch(`https://api.curseforge.com/v1/mods/1150827/files/${id}`, { headers: apiHeaders });
    if (readback.ok) {
      const actual = (await readback.json()).data;
      receipts[receipts.length - 1].readback = { id: actual.id, fileName: actual.fileName,
        fileStatus: actual.fileStatus, isAvailable: actual.isAvailable, gameVersions: actual.gameVersions,
        dependencies: actual.dependencies, downloadUrl: actual.downloadUrl };
      save();
    }
  }
}

module.exports = { targets, metadataFor };
if (require.main === module) recover().catch(error => { console.error(error); process.exit(1); });
