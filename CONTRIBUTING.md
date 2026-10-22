# Contributing

Thank you for your interest in Runeborn.

## Getting started

1. Fork the repository and create a branch from `main`.
2. Make your change in the right place (see the layout in the root [README](README.md)):
   - Game rules, catalog, formulas, network format and UI go in `core/`. It's plain Java with no
     Minecraft classes, and every version shares it.
   - Gameplay that needs Minecraft types goes in `shared/`. Only use APIs that exist in every supported
     version. Anything that differs between versions goes behind `Compat` (one copy per version, same
     signatures).
   - Items, mixins, rendering and loader glue go in `versions/<mc>/`.
3. Build and test before opening a pull request.

## Checks

```bash
tools/test-core.sh                                    # core tests
cd versions/<mc> && ./gradlew :fabric:build :<neoforge|forge>:build
```

If you change weapons, materials, bosses or English strings in `core/`, re-run
`python3 tools/assetgen/generate.py` and commit the regenerated assets. The core tests fail when
`en_us.json` is out of date.

Open each `versions/<mc>/` directory as its own Gradle root in your IDE. The 26.x workspaces need
Gradle running on JDK 25.

## Pull requests

- Keep changes focused and explain what problem they solve.
- Link related issues when applicable.
- Make sure the builds you touched pass. A change to `core/` or `shared/` touches every version.

## Issues

Use GitHub Issues for bug reports and feature requests. Include the Minecraft version, mod loader,
mod version and steps to reproduce.

## License

By contributing, you agree that your contributions will be licensed under the
[Apache License 2.0](LICENSE).
