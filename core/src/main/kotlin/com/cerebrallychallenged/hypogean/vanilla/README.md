# Hypogean Vanilla Package

This package contains the base game content for Hypogean. It is designed to be fully moddable and serves as the reference implementation for game mechanics, content, and UI configuration.

## Overview

The `vanilla` package defines the "standard" game experience. It uses the core engine's modding API to register all game elements, including actors, items, actions, and levels. By following the patterns established here, modders can extend or replace parts of the game.

## Key Components

- **VanillaMod**: The main entry point for the vanilla game content. It implements the `Mod` interface and handles the registration of all game-specific features (actions, attributes, entities, etc.) into the [Hypogean Server](../server/README.md).
- **DefaultViews**: Defines the standard user interface layout and behavior using the `ViewsDefinition` DSL from the [Hypogean View Package](../view/README.md). It configures how different actions are visualized and how the UI reacts to game events.

## Package Structure

The package is organized into several subpackages by content type:

- **actions**: Implementations of player and NPC actions (e.g., `MoveAction`, `AttackAction`, `HackingAction`). See [Actions README](actions/README.md) for details.
- **actors**: Definitions of game entities like the player character, robots, and NPCs.
- **items**: Equipment, weapons, and chassis components that can be attached to actors.
- **levels**: Level definitions and layouts, including test levels and the main game progression.
- **props**: Interactive and decorative world objects (e.g., doors, crates, terminals).
- **behavior**: AI behaviors for different types of NPCs.
- **attributes**: Definition of game-specific properties like `Health`, `Energy`, and `Accuracy`.
- **cascade**: Consequence logic for effects (e.g., how damage affects health and energy). See [Cascade README](cascade/README.md) for the effect system architecture.
- **dialogs**: System for in-game conversations and interactions. See [Dialogs README](dialogs/README.md) for the dialogue DSL.
- **triggers**: Logic that fires based on world events or actor movement. See [Triggers README](triggers/README.md) for event-driven logic.
- **walls / blocks**: Building blocks for the game's environment.

## Modding

This package serves as the primary example for creating new mods. Most classes are designed to be extensible. Developers looking to create new content should examine how `VanillaMod` registers components and how individual subpackages implement game logic.

## Integration

- **Server**: The `VanillaMod` class is loaded by the server to populate the world with rules and entities.
- **View**: `DefaultViews` provides the standard configuration for the UI system, mapping game state to visual components.
