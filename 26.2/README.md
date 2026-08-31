# Fine Tuned Weaponry (Minecraft 1.21.1)

MultiLoader mod for **Fabric** and **NeoForge**, built on the MultiLoader Template 26.2 project structure.

## Requirements

- Java 21+
- Gradle wrapper included (`./gradlew`)

## Project layout

| Module | Purpose |
|--------|---------|
| `common/` | Shared game logic, content, assets, and data |
| `fabric/` | Fabric entrypoints, registry, networking, client GUIs, JEI |
| `neoforge/` | NeoForge entrypoints, registry, networking, client GUIs, JEI, datagen |

## Build

```bash
./gradlew :fabric:build :neoforge:build
```

## Run clients

```bash
./gradlew :fabric:runClient
./gradlew :neoforge:runClient
```

## Dependencies

- **jauml** (required) — runtime config generation
- **JEI** (optional) — custom recipe categories for weapons forge and research table

## Mod info

- **Mod ID:** `fine_tuned_weaponry`
- **Package:** `com.naizo.finetuned`
- **License:** Apache-2.0
