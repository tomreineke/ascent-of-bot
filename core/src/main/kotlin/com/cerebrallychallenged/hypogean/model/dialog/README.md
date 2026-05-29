# Hypogean Model Dialogs

This subpackage defines the base structure for the game's dialogue system.

## Key Components

- **Dialog**: The base class for defining a conversation tree. It contains a set of `Node`s and defines the entry point (`start`).
- **Node**: A specific point in a dialogue that can execute logic and lead to a `Continuation` (like another node, a selection, or the end).
- **Role**: An abstract participant in a dialogue (e.g., "Speaker", "Listener").
- **RoleMap**: A mapping from abstract `Role`s to concrete game `Entity`s for a specific conversation instance.
- **Select**: A type of continuation that presents the player with multiple `Option`s to choose from.

## Execution Flow

1. **Initiation**: A dialogue is started by calling `Dialog.initiate`, which creates an `ActiveDialog` state.
2. **Progression**: The system moves from node to node. If a `Node` contains logic, it is executed within a `CascadeContext`.
3. **Branching**: When a `Select` node is reached, the UI displays the options. Choosing an option leads to the next node or ends the dialogue.
4. **Conclusion**: When an `End` node is reached, the dialogue finishes, and the active actor's turn is updated based on the specified `InitiativeCost`.

## See Also

- [Model Main README](../README.md)
- [Vanilla Dialogs](../../vanilla/dialogs/README.md) for the high-level DSL used to define concrete conversations.
