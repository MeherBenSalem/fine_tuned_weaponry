# Contributing

Thank you for your interest in Fine Tuned Weaponry.

## Getting started

1. Fork the repository and create a branch from `main`.
2. Make your changes in the appropriate version workspace (see the root [README](README.md)).
3. Build and test locally before opening a pull request.

## Development

- Active development targets the [`26.2/`](26.2/) workspace (Fabric + NeoForge, Minecraft 1.21.1).
- Open the version directory as your IDE/Gradle root (for example `26.2/`).
- Match existing code style and naming in the module you edit.

```bash
cd 26.2
./gradlew :fabric:build :neoforge:build
```

## Pull requests

- Keep changes focused and explain what problem they solve.
- Link related issues when applicable.
- Ensure builds pass for the workspaces you touched.

## Issues

Use GitHub Issues for bug reports and feature requests. Include Minecraft version, mod loader, mod version, and steps to reproduce bugs.

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE).
