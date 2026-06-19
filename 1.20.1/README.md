# Fine Tuned Weaponry (MultiLoader 1.20.1)

Cross-loader port of **Fine Tuned Weaponry** v2.2.0 for Minecraft 1.20.1 (Forge + Fabric).

| Module | Role |
|--------|------|
| `common/` | Blocks, items, block entities, 41 procedures, menus, assets/data |
| `forge/` | Forge entrypoint, DeferredRegister, capabilities, networking, screens, JEI |
| `fabric/` | Fabric entrypoint, registries, networking, player data, screens, JEI |

## Requirements

- **Java 17** (set `JAVA_HOME` before building on machines with newer JDKs)
- Forge 47.3.0 / Fabric Loader + Fabric API (versions in `gradle.properties`)
- Runtime dependency: **jauml** (both loaders)

## Build

```bash
./gradlew build
```

Loader-specific artifacts:

```bash
./gradlew :forge:build
./gradlew :fabric:build
```

## Run client

```bash
./gradlew :forge:runClient
./gradlew :fabric:runClient
```

## Data generation (Forge)

```bash
./gradlew :forge:runData
```

Generated output is written to `forge/src/generated/resources/`. Providers cover block loot tables, gem/amp item tags, and core workstation recipes.

## Project layout

Shared gameplay code lives in `common/`. Loader modules wire registries, events, networking, capabilities (Forge), and client screens. JEI integration is optional (`compileOnly`); install JEI at runtime to see custom forge/research recipes.

Based on the [MultiLoader Template](https://github.com/jaredlll08/MultiLoader-Template).
