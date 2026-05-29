# Hypogean Model Actions

This subpackage defines the foundational interfaces and structures for the game's action system.

## Key Components

- **Action**: The base interface for defining a type of behavior (e.g., Move, Attack). It is responsible for generating valid `ActionInstance`s for a given actor and world state.
- **ActionInstance**: Represents a specific, parametrized execution of an action (e.g., "Move to Cell X"). It contains the logic for estimating consequences and executing the action.
- **ActionTable**: A collection of available `ActionInstance`s, often organized hierarchically (e.g., a "Move" action might have multiple destination instances).
- **ActionCategory**: Used to group actions for UI presentation (e.g., "Navigation", "Combat").
- **InitiativeCost**: Defines how much "time" or "turn progress" an action consumes.

## Workflow

1. **Generation**: The `Action.createInstances` method is called to populate an `ActionTable` with all valid options for the current actor.
2. **Selection**: A player or AI selects an `ActionInstance` from the table.
3. **Estimation**: The system calls `estimateConsequences()` to show potential outcomes (like damage) in the UI or for AI decision-making.
4. **Execution**: The `execute()` method is called within a `CascadeContext` to apply the action's effects to the world.

## See Also

- [Model Main README](../README.md)
- [Vanilla Actions](../../vanilla/actions/README.md) for concrete implementations of these interfaces.
