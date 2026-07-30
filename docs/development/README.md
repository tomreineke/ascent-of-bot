# Development

How-to guides for setting up, building, testing, running, and deploying the project.

These are **guides** — updated when the process changes. Not a reference for architecture or design; use [`../architecture/`](../architecture/) for that.

## Quick Start

Refer to [`../../CLAUDE.md`](../../CLAUDE.md) for the quick build/test/run commands.

## Topics

- **Environment Setup** — JDK, Kotlin, Unreal Engine, Rust, IDE configuration
- **Building** — Gradle tasks, Rust plugin build, asset generation
- **Testing** — Unit tests, integration tests, running test suites
- **Running** — Starting the app locally, debugging, profiling
- **Git Workflow** — Branch conventions, commit message style
- **MCP Setup** — Any Model Context Protocol integrations

## Common Commands

See [`../../CLAUDE.md`](../../CLAUDE.md) for the full list. Key commands:

```bash
gradle build                    # Build all modules
gradle core:test               # Run tests
gradle core:createJunConfigXml # Generate JVM config for Unreal
```

For detailed guides on specific topics, add files to this folder as needed.