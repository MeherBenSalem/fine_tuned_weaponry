# Weapons anvil regression

These tests use Python's standard library and the existing Java/Minecraft development
cache. They install no tools and do not change that cache. Run from the repository
root after the supported native builds populate a cache. Keep generated artifacts
and classes outside the repository.

Example PowerShell commands (replace the cache path):

```powershell
$cache = 'C:\path\to\.gradle\caches'
$artifacts = 'C:\path\to\test-output\artifacts'
python tests/anvil/prepare.py --cache $cache --output $artifacts
$env:FTW_TEST_ARTIFACTS = $artifacts
python -m unittest discover -s tests/anvil -p 'test_*.py' -v
python tests/anvil/resources.py $artifacts neoforge 1.21.1 C:\path\to\test-output\baseline-tags.json
python tests/anvil/run_native.py --cache $cache --release "$artifacts\ftw-neoforge-1.21.1-2.3.0.jar" --tags C:\path\to\test-output\baseline-tags.json --classes C:\path\to\test-output\baseline --baseline
python tests/anvil/resources.py $artifacts neoforge 1.21.1 C:\path\to\test-output\fixed-tags.json --source 26.2/common/src/main/resources
python tests/anvil/run_native.py --cache $cache --release "$artifacts\ftw-neoforge-1.21.1-2.3.0.jar" --tags C:\path\to\test-output\fixed-tags.json --classes C:\path\to\test-output\fixed --workspace 26.2
```

Repeat fixture resolution with `fabric` for Fabric 1.21.1. For 1.20.1 use `fabric`
or `forge`, `--source 1.20.1/common/src/main/resources`, then `--version 1.20.1`,
`--workspace 1.20.1` and the published `ftw-forge-1.20.1-2.3.0.jar`. The stable
registry fixture is shared. 1.20.1 tests compile additional common source classes
because its Forge release uses SRG method names. Use a fresh classes directory
when switching between baseline and source tests.

The headless baseline executes the published NeoForge 2.3.0 menu, real Minecraft
slots, `safeInsert` and `quickMoveStack`. The source tests additionally exercise
the actual gem procedures and custom-data helpers. Third-party test weapons have
no tool class inheritance; tag membership is their sole eligibility mechanism.
An actual `ClassicKatanaItem` is constructed; other registered mod items are
registry identity fixtures. All 14 native weapons are checked by resource tests.

Coverage includes direct/shift-click weapon insertion, unrelated-item rejection,
loader-specific tool categories, all 3 gems and 16 amplifiers being routed into
their correct slots, application/removal, occupied sockets, legacy `.0` socket
keys, legacy modification flags, custom names, damage and foreign NBT.

The persistence checks use the real `WeaponsAnvilBlockEntity` inventory,
`createMenu`, `saveWithFullMetadata`, compressed `NbtIo` serialization and
`BlockEntity.loadStatic`. They close/reopen a menu bound to the restored container,
then remove upgrades and serialize/restore again. Both current and legacy sockets,
unused input stacks and the block entity's non-menu slots are checked. A minimal
version-specific `Level` adapter supplies only the bound block entity/state. Its
native factory is registered against a vanilla chest test state; production block
registration and chunk storage are not simulated.

Evidence boundaries: this is **headless common-code verification**, not an in-game
client, full loader lifecycle, networking, region/chunk persistence or rendering
test. The harness reopens only its own test JVM's vanilla item/block-entity registries and
allocates a minimal player/world fixture without their constructors. It supplies
a test registry instead of the production loader registration sequence. Native
block-entity serialization is covered; world saving, dirty-chunk handling and
dedicated-server/client synchronization remain unverified. Enchantments are not
included in the headless component fixture.
Resource resolution implements additive/replace and required/optional tag
references, then binds holders through Minecraft; it does not run either loader's
resource manager. Gradle reports `test NO-SOURCE`; the focused tests are explicitly
run through these scripts. The tests are intentionally separate from shipped
runtime classes and do not add runtime dependencies.

Before publication, verify a clean in-game NeoForge and Fabric client: place an
anvil, insert and shift-click each weapon class, apply/remove upgrades, reopen the
bound block inventory, and confirm customized/enchanted weapon data survives.
