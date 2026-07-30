# Ascent of Bot — Documentation System

This folder organizes all project knowledge by **lifecycle**: how documents change over time, not by subject. This structure makes it easier to maintain single sources of truth and keep knowledge in sync with code.

## Document Organization

| Folder                               | Purpose                             | Lifecycle         | Role                                                             |
|--------------------------------------|-------------------------------------|-------------------|------------------------------------------------------------------|
| [`architecture/`](#architecture)     | How the system is built and works   | **LIVING**        | Reference for understanding; kept true as code changes           |
| [`conventions/`](#conventions)       | Rules every developer must follow   | **LIVING**        | Enforced during code review; broken rules signal unclear docs    |
| [`decisions/`](#decisions)           | Why design choices were made        | **APPEND-ONLY**   | Historical record; superseded decisions never rewritten          |
| [`development/`](#development)       | Setup, build, run, test, deploy     | **GUIDE**         | How-to docs; updated when process changes                        |
| [`investigations/`](#investigations) | Open analyses tied to backlog items | **WORKING-STATE** | Temporary; folded into living docs, then deleted when work lands |

## Routing Rule: Where Does a New Document Belong?

**Architecture** — How does the system work?
- Multi-file interactions, module responsibilities, design patterns, dataflow
- **Example:** "Pathfinding architecture", "Skiatree rendering pipeline", "NPC behavior decision flow"

**Conventions** — What must I follow when writing code?
- Naming rules, code style, patterns that must be applied uniformly, library usage
- **Example:** "Kotlin style guide", "Skiatree node layout conventions", "How to emit action events"

**Decisions** — Why did we choose this, not that?
- Trade-offs explored, context (constraints, prior incidents), resolution
- **Example:** "Why we use Kryo for serialization (not JSON)", "Why EffectKinds is currently a temporary construct"

**Development** — How do I build, test, run this?
- Environment setup, build steps, CI/CD, debugging, local testing, deployment
- **Example:** "Setting up Rust plugin build", "Running game in Unreal Editor", "Writing integration tests"

**Investigations** — What are we currently figuring out?
- Open questions, in-flight analysis, ticket-linked explorations
- Lives in git while ticket is open, gets folded into architecture/conventions/decisions, then deleted
- **Example:** "BACKLOG-42: Extended Recon System design", "Event-triggered dialogue scope"

---

## Reading Paths

**I'm new here.** Start at [`../CLAUDE.md`](../CLAUDE.md) for a 5-minute overview, then browse [`development/`](#development) for setup steps.

**I'm implementing a feature.** Check [`conventions/`](#conventions) for rules you must follow, and [`architecture/`](#architecture) for how things fit together.

**I'm reviewing a PR.** Cross-check against [`conventions/`](#conventions) and [`architecture/`](#architecture) to catch misses.

**I'm investigating a design question.** Look in [`investigations/`](#investigations) for open work, or read [`decisions/`](#decisions) to understand prior choices.

---

## Living Documents

The **architecture** and **conventions** folders contain the "living" knowledge. They are kept true through three commitments:

1. **Single Source of Truth** — When a fact changes, exactly one file is edited. Summaries link to the home; restating in full is duplication.
2. **Git-First** — All knowledge lives in the repo, visible to the team (not in private chat/memory), so findings survive authors and are reviewable.
3. **The One-Edit Test** — If a change breaks a fact in multiple files, the docs are not structured yet.

---

## History & Evolution

See [`PROJECT-DOCS-PLAYBOOK.md`](../PROJECT-DOCS-PLAYBOOK.md) for the playbook used to build this system (it's project-agnostic and can be applied to any codebase).

---

### Architecture

Placeholder for architecture documentation. Topics to document as they become relevant:
- Game state simulation architecture
- Skiatree rendering pipeline
- Action system design
- NPC behavior and decision-making
- Pathfinding and movement
- Damage and effect cascade system
- Networking and client-server communication
- Unreal Engine integration

### Conventions

Placeholder for code conventions and patterns. Topics to add:
- Kotlin style and naming conventions
- Skiatree node and layout patterns
- Action system usage
- Resource management (AutoCloseable pattern)
- Test conventions
- Error handling patterns

### Decisions

Decision records for significant design choices. When creating a new decision:
- Number sequentially (e.g., 0001, 0002, ...)
- Record context, trade-offs, and resolution
- Once made, never rewrite—append new decisions instead

### Development

Guides for building, testing, and running the project. See [`../CLAUDE.md`](../CLAUDE.md) for the quick start; add detailed guides here as needed.

### Investigations

Temporary working-state analyses tied to backlog items (e.g., "BACKLOG-42: Extended Recon System"). When an investigation is complete:
1. Fold findings into [`architecture/`](#architecture) or [`conventions/`](#conventions)
2. Remove from this folder (it's working-state, not a final record)
3. Close the associated backlog item