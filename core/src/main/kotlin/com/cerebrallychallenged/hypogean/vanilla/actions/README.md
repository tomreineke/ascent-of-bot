# Hypogean Vanilla Actions

This subpackage contains implementations of actions that actors (players and NPCs) can perform in the game world.

## Overview

Actions are the primary way to interact with the game world and its entities. They are registered in the `VanillaMod` and can be categorized into various types such as movement, combat, hacking, and social interactions.

## Key Concepts

- **Action**: Defines the logic and rules for a specific type of interaction (e.g., `MoveAction`, `AttackAction`).
- **ActionInstance**: Represents a concrete occurrence of an action with specific parameters (e.g., a target location or entity).
- **ActionCategory**: Groups actions for UI purposes (e.g., `MoveCategory`, `AttackCategory`).

## Implementation Details

### Action Instances
Most actions implement a `createInstances` method that returns a list of valid `ActionInstance`s for a given actor and state. For example, `MoveAction` generates instances for all reachable cells.

### Execution
The `ActionInstance.execute()` method contains the logic that modifies the world state when the action is performed.

### Consequences
Actions often estimate their consequences (e.g., damage dealt, energy consumed) using `estimateConsequences()`, which is used by the AI and shown in the UI. This mechanism is defined in the [Model Action Package](../../model/action/README.md).

## Common Actions

- **MoveAction**: Handles actor movement across the map, including pathfinding and curved movement segments.
- **AttackAction**: Base for various combat actions like `MeleeAction` and `DirectShotAction`.
- **HackingAction**: Allows interacting with terminals and other hackable entities.
- **TalkAction**: Initiates [Dialogs](../dialogs/README.md) with other actors.
- **PickupAction**: Interacting with items on the ground.

## See Also

- [Vanilla Package Main README](../README.md)
- [Cascade System](../cascade/README.md) for how action effects are processed.
- [Model Action Package](../../model/action/README.md) for base interfaces.
