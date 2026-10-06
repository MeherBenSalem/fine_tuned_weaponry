// Regress the actual serialized upload metadata that CurseForge rejected with 400/1002.
const assert = require('node:assert/strict');
const fs = require('fs');
const path = require('path');
const vm = require('vm');
const root = path.resolve(__dirname, '../..');
const { targets, metadataFor } = require(path.join(root, '.github/scripts/recover_cf_231.cjs'));
const versions = [{ id: 1, name: '1.21.1' }, { id: 2, name: '1.20.1' },
                  { id: 3, name: 'Client' }, { id: 4, name: 'Server' }];
for (const target of targets) {
  const body = JSON.parse(JSON.stringify(metadataFor(target, 'release notes', versions)));
  assert.equal(typeof body.relations.projects[0].projectID, 'number');
  assert.equal(body.relations.projects[0].projectID, 1281310);
  assert.equal(body.relations.projects[0].type, 'requiredDependency');
  assert.ok(body.gameVersions.includes(target.loaderId));
  assert.ok(body.gameVersions.includes(target.game === '1.21.1' ? 1 : 2));
  assert.equal(body.displayName, `2.3.1 · ${target.loader} · ${target.game}`);
}
assert.throws(() => metadataFor(targets[0], 'notes', []), /Missing game version/);

// Exercise the normal publisher's real body construction as well, without network or builds.
const yaml = fs.readFileSync(path.join(root, '.github/workflows/publish.yml'), 'utf8').replace(/\r\n/g, '\n');
const begin = yaml.indexOf("          const fs = require('fs');");
const end = yaml.indexOf('\n          NODE', begin);
const code = yaml.slice(begin, end).split('\n').map(line => line.slice(10)).join('\n');
let uploads = 0;
const sandbox = {
  require: name => name === 'fs' ? { existsSync: () => true, readFileSync: file => file.endsWith('.jar') ? Buffer.from('fixture') : 'release notes' } : require(name),
  Blob, FormData,
  process: { env: { VERSION: '2.3.1', MODRINTH_ID: 'ENwBoUVH', CURSEFORGE_ID: '1150827',
    JAR_LIST: targets.map(target => `fine_tuned_weaponry-${target.loader}-${target.game}-2.3.1.jar`).join('\n') },
    exit: code => { throw new Error('fixture exit ' + code); } },
  console: { log: () => {}, warn: () => {}, error: () => {} },
  fetch: async (url, options = {}) => {
    if (url.includes('/project/ENwBoUVH/version')) return { ok: true, json: async () => [] };
    if (url.includes('/mods/1150827/files')) return { ok: true, json: async () => ({ data: [], pagination: { totalCount: 0 } }) };
    if (url.endsWith('/api/game/versions')) return { ok: true, json: async () => versions };
    if (url === 'https://api.modrinth.com/v2/version') return { ok: true, text: async () => '{"id":"fixture"}' };
    assert.equal(url, 'https://minecraft.curseforge.com/api/projects/1150827/upload-file');
    assert.equal(options.method, 'POST');
    const body = JSON.parse(options.body.get('metadata'));
    assert.equal(typeof body.relations.projects[0].projectID, 'number');
    assert.equal(body.relations.projects[0].projectID, 1281310);
    uploads++;
    return { ok: true, text: async () => '{"id":99000000}' };
  },
};
async function checkRecovery(alterBytes = false, conflict = false) {
  let posts = 0;
  const artifactRoot = process.env.RECOVERY_TEST_ARTIFACT_ROOT || path.join(root, 'release-artifacts');
  const program = fs.readFileSync(path.join(root, '.github/scripts/recover_cf_231.cjs'), 'utf8');
  const fixtureFs = {
    readFileSync: (file, encoding) => {
      if (file === 'CHANGELOG.md') return fs.readFileSync(path.join(root, file), encoding);
      const data = fs.readFileSync(path.join(artifactRoot, file.replace(/^release-artifacts[/\\]/, '')));
      return alterBytes ? Buffer.concat([data, Buffer.from('altered')]) : data;
    }, writeFileSync: () => {},
  };
  const fixtureModule = { exports: {} };
  const fixtureRequire = name => name === 'fs' ? fixtureFs : require(name);
  fixtureRequire.main = fixtureModule;
  const context = {
    require: fixtureRequire, module: fixtureModule, Blob, FormData,
    process: { env: { CURSEFORGE_TOKEN: 'fixture', CURSEFORGE_API_KEY: 'fixture' },
      exit: code => { throw new Error('fixture exit ' + code); } },
    console: { log: () => {}, error: () => {} },
    fetch: async (url, options = {}) => {
      if (url.includes('/mods/1150827/files?pageSize=')) return { ok: true, json: async () => ({
        data: conflict ? [{id: 1, fileName:'fine_tuned_weaponry-neoforge-1.21.1-2.3.1.jar', hashes:[], dependencies:[], gameVersions:[]}] : [],
        pagination: {totalCount: conflict ? 1 : 0} }) };
      if (url.endsWith('/api/game/versions')) return { ok: true, json: async () => versions };
      if (url.match(/\/mods\/1150827\/files\/\d+$/)) return { ok: false };
      assert.equal(url, 'https://minecraft.curseforge.com/api/projects/1150827/upload-file');
      assert.equal(options.method, 'POST');
      const metadata = JSON.parse(options.body.get('metadata'));
      assert.equal(typeof metadata.relations.projects[0].projectID, 'number');
      assert.equal(metadata.relations.projects[0].projectID, 1281310);
      posts++;
      return { ok: true, text: async () => JSON.stringify({id:99000000+posts}) };
    },
  };
  if (alterBytes || conflict) {
    await assert.rejects(vm.runInNewContext(program, context));
    assert.equal(posts, 0);
  } else {
    await vm.runInNewContext(program, context);
    assert.equal(posts, 4);
  }
}
Promise.resolve(vm.runInNewContext(code, sandbox)).then(async () => {
  assert.equal(uploads, 4);
  await checkRecovery();
  await checkRecovery(true);
  await checkRecovery(false, true);
  console.log('PASS: integer dependency IDs for four targets; original CI bytes accepted; altered bytes/conflicting files refuse all writes; recovery uses only CurseForge');
}).catch(error => { console.error(error); process.exit(1); });
