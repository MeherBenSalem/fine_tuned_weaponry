# Fine Tuned Weaponry 2.3.1

## Fixed
- Weapons Anvil accepts supported weapons and tools through direct insertion and shift-click on Minecraft 1.21.1 Fabric and NeoForge, with matching eligibility on supported 1.20.1 Fabric and Forge workspaces.
- Restored Minecraft 1.21.1 gem and amplifier tags so upgrade slots accept the intended items.
- Fixed Minecraft 1.21.1 socket occupancy and removal: occupied sockets no longer consume replacement upgrades, and installed gems/amplifiers can be returned. Historical `.0` socket keys remain supported.
- Release downloads declare Jauml as a required dependency on both platforms, matching the existing mod metadata. Dependency versions are unchanged.

## Compatibility and verification
- Weapon eligibility uses the data-pack-extensible `fine_tuned_weaponry:anvil_tools` tag, with supported common conventions and optional legacy Forge tags.
- Native/headless tests cover insertion, shift-click, socket application/removal, legacy data, custom names, damage, foreign NBT and bound-anvil container serialization/reopening. All supported workspace/loader builds pass.
- Real in-game clients, networking, chunk/region saving and enchantment preservation have not been verified in this release's test session.

# Fine Tuned Weaponry 2.3.0

## Added
- Research table recipes for base gems: Inferno Core, Frost Rune, and Storm Shard
- Right-click abilities for Earthly, Obsidian, and Rose Gold hammers
- Hollow Man staff Haste ability on right-click
- Jauml config keys for hammer and staff ability tuning

## Fixed
- Weapon tooltips now match implemented abilities
- Renamed `charger_amp_reicpe.json` to `charger_amp_recipe.json`
- Removed orphaned Kurasai Katana lang keys
- Fixed "Weapons Tempate" typo in research table tooltips

## Changed
- Project licensed under Apache-2.0 with standard OSS contributor docs
- CI build workflow targets the active `26.2/` workspace
