# Architecture

This folder documents **how the system works**: multi-file interactions, module responsibilities, design patterns, and dataflow.

These are **living documents** — kept true as the codebase evolves. If you change a significant architectural fact, update the relevant file here.

## Topics

- **Game Simulation** — Game state, turn cycle, simulation loop
- **Skiatree Rendering** — 2D scene tree, layout, text rendering, input handling
- **Action System** — How actors submit and execute actions; cascade effects
- **NPC Behavior & AI** — Decision-making, pathfinding, behavior trees
- **Damage & Effects** — Weapon effects, status effects, damage cascade
- **Networking** — Client-server communication, state synchronization
- **Unreal Integration** — JVM ↔ Unreal via JNI, asset management

## Getting Started

Refer to [`../CLAUDE.md`](../../CLAUDE.md) for the high-level overview. Add architecture docs here as you document system interactions.