# Fine Tuned Weaponry v2.2.0

### New Features
* GitHub Actions CI builds Forge and Fabric jars on Java 17
* Forge data generation for block loot tables, gem/amp tags, and core workstation recipes (`./gradlew :forge:runData`)
* `AttackGemTriggersProcedure` gates gem combat effects so only relevant gems run on hit

### Improvements
* MultiLoader project consolidated under `1.20.1/` with root README and `.gitignore`
* Fabric player variables persist across sessions via world `SavedData` (parity with Forge capabilities)
* Moon's Lunar Bloomfang passive throttled to every 20 ticks; no longer sets grass every tick
* Gem trigger procedures refactored to use `GemNbtKeys`, `LivingEntity` queries, and level-scoped RNG
* Consolidated weapon cooldown tooltips into `GiveWeaponTooltipProcedure`
* Jauml config paths and item tags normalized from `finetunned` to `fine_tuned_weaponry`
* Pinned `fabric-loom` to 1.9.2; removed stale MultiLoader template SPI files
* VS Code launch configs use `${workspaceFolder}` paths

### Bug Fixes
* Fixed Fabric player GUI page data lost on disconnect
* Fixed redundant `createConfigFile` double-call in config procedures
* Fixed `RandomSource.create()` usage in combat and crafting procedures

### Configuration
* Jauml config folder renamed to `fine_tuned_weaponry` (legacy `finetunned` paths still read for existing saves)
* Gem socket NBT flag uses `fine_tuned_weaponry_modified` (legacy `finetunned` flag still recognized)

### Compatibility
* Minecraft 1.20.1 — Forge 47.3.0 and Fabric Loader 0.16.9
* Requires **jauml** at runtime (both loaders)
* JEI remains optional
* Drop-in update from 2.1.0; existing worlds and socketed weapons retain gem data via legacy NBT keys
