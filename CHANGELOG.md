# Runeborn 3.0.0

Fine Tuned Weaponry is now **Runeborn**: a ground-up remake as a souls-like RPG progression mod.
The mod id is now `runeborn`, so this release does not carry over items from Fine Tuned Weaponry 2.x.

## Added
- Rune-based leveling with eight attributes (Vigor, Mind, Endurance, Strength, Dexterity, Intelligence,
  Faith, Arcane), runes dropped on death and recoverable where you fell.
- 10 origins with starting attributes, an armament and a kit.
- 74 armaments in 19 classes with five damage types, S–E scaling, attribute requirements and status
  buildup (bleed, frost, poison, blight).
- 23 weapon arts and 12 spells (sorceries and incantations) on an FP pool.
- Reinforcement to +25 (tempering stones) or +10 (lustrous stones) and 13 whetstone affinities.
- Ember Shrines: kindle, rest, level up, reinforce, infuse, upgrade flasks, fast travel, merchant,
  relic exchange, crown shards and rebirth.
- Ember and Azure flasks with upgradable charges and potency.
- Mob levels by distance and dimension, champions with 10 affixes.
- Seven summonable Sovereign bosses with phases and abilities. The Wither, Ender Dragon, Warden and
  Elder Guardian also drop relics and crown shards.
- Stamina, a dodge roll with invulnerability frames, and status buildup on hit.
- New HUD (HP, FP and stamina bars, runes, flasks, equipment, target and boss bars), event banners,
  status and shrine screens, and rich weapon tooltips.
- Shared progression with RPG Attribute System. Shrine level-ups need it installed, and every rune level
  bought also grants RPG levels (`rpgLevelsPerRuneLevel`) through its own level-up pipeline. Without it,
  the Level Up service stays locked and everything else works.
- A Runeborn advancement tab: 22 feats, from kindling a shrine to felling every Sovereign.
- The Codex, a guide to every system with crafting recipes; Runeborn recipes are added to the recipe book.
- Server and client JSON configs, and `/runeborn` operator commands. The status key is <kbd>I</kbd>
  (RPG Attribute System uses <kbd>K</kbd>).
- Builds for Minecraft 1.20.1 (Forge, Fabric), 1.21.1 (NeoForge, Fabric), 26.2 and 26.3
  (NeoForge, Fabric).

## Removed
- The 2.x weapon catalog, gem sockets, Weapons Anvil, Weapons Forge and Research Table.
- The Jauml dependency. Runeborn has no dependencies besides Fabric API on Fabric.
