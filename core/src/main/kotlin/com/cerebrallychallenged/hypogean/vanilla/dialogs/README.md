# Hypogean Vanilla Dialogs

The `dialogs` subpackage contains the definitions and logic for in-game conversations and scripted interactions between the player and NPCs or world objects.

## Overview

Dialogs are implemented using a custom DSL (Domain Specific Language) that allows for branching paths, role-based speech, and world-modifying consequences. They are built upon the base [Model Dialog Package](../../model/dialog/README.md) and are often triggered by the `TalkAction` or by environmental events.

## Key Concepts

- **Dialog**: The base object for a conversation definition.
- **Role**: Represents a participant in the dialog (e.g., `Protagonist`, `Merchant`, `AI`).
- **Node**: A specific point in the conversation containing descriptions, speech, and available options.
- **Consequences**: Dialog nodes can execute code to modify the world state (e.g., giving items, dealing damage, changing faction relations).

## Implementation Pattern

Dialogs typically follow this structure:

1. **Role Definition**: Define which roles are involved.
2. **Role Mapping**: The `determineRoles` method maps concrete game `Actor`s to the defined roles.
3. **Conversation Tree**: Defined using `node { ... }` blocks.
   - `say(...)`: A role speaks a line.
   - `describe(...)`: Flavor text describing the scene.
   - `select { ... }`: Provides options for the player to choose from.
   - `availableIf { ... }`: Conditional logic for whether a dialog option is shown.

## Example Dialogs

- **FirstLevelCompanionDialog**: Handles complex interactions with the player's initial companion.
- **GreatAIDialog**: Scripted interaction with a boss or major terminal.
- **TestDialog**: A reference implementation demonstrating basic features like branching and world effects.

## Interaction with UI

The `DialogView` in the [View Package](../../view/README.md) handles the presentation of these nodes, displaying the text and interactive buttons for the player to progress through the conversation.

## See Also

- [Vanilla Package Main README](../README.md)
- [Actions Subpackage](../actions/README.md) for how dialogs are initiated.
- [Cascade System](../cascade/README.md) for effects applied during dialogs.
- [Model Dialog Package](../../model/dialog/README.md) for base interfaces.
