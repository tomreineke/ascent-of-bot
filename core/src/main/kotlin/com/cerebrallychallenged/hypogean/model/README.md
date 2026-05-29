# Hypogean Model Package

This package contains the core game model and domain logic for Hypogean. It defines the fundamental entities, state management, and rules that govern the game world.

## Core Concepts

- **World**: The top-level container for the game state, including the map, all entities, and the simulation timeline.
- **Entity**: The base interface for anything that exists in the world. Key specializations include `Actor`, `Item`, and `Prop`.
- **Actor**: A specialized entity that can take actions, has attributes (like health and energy), and belongs to a faction.
- **Item**: Equipment that can be carried or equipped by actors, often providing new actions or modifying attributes.
- **Cell**: Represents a single tile in the game world's grid.
- **Rulebook**: Defines the global rules and registry of all modded game elements (actions, entity types, etc.).

## State Management

The model uses a deterministic state system:
- **WorldChange**: Represents a single, atomic modification to the world state.
- **ChangeSchedule**: Manages the sequencing and execution of world changes.
- **IniQueue**: A priority queue that determines the order of actor turns based on initiative costs.

## Subpackages

The core model is extended by several specialized subpackages:

- **[action](action/README.md)**: Interfaces for defining what actors can do.
- **[cascade](cascade/README.md)**: Foundation for state-altering consequences and effect processing.
- **[effect](effect/README.md)**: Systems for representing and modifying properties (damage, buffs, etc.).
- **[dialog](dialog/README.md)**: Base structures for conversation trees.
- **[trigger](trigger/README.md)**: Logic for event-based world interactions.
- **attribute**: Dynamic property system for entities.
- **maps**: Data structures for the game grid and spatial queries.

## Integration

- **[Server](../server/README.md)**: Uses the model to run the authoritative simulation.
- **[Vanilla](../vanilla/README.md)**: Provides concrete implementations of model interfaces to define the actual game content.
- **[View](../view/README.md)**: Observes the model to present the game state to the player.
- **[GUI](../gui/README.md)**: Provides extensions for displaying model data (like entity references and portraits) in rich text.
