# Hypogean Vanilla Triggers

The `triggers` subpackage contains logic for event-driven interactions that occur automatically based on world events or actor movement.

## Overview

Triggers are entities (often [StatusEffects](../../model/StatusEffect.kt) attached to world objects or locations) that react to game state changes. They are commonly used for environmental hazards, scripted events, and interactive props.

## Key Concepts

- **Trigger**: An entity that monitors for a specific condition.
- **isTriggeredBy**: Logic to determine if a specific actor or event should fire the trigger (e.g., check for proximity).
- **executeTrigger**: The code that runs when the trigger fires (e.g., dealing damage, opening a door, or starting a dialog).

## Common Triggers

- **MineTrigger**: Attached to `LandMine` entities. It fires when an actor enters its `triggerRange`, dealing area damage and destroying the mine.
- **ClosingDoorTrigger**: Automatically closes a door when no actors are nearby.
- **ChargingPlatformHintTrigger**: Provides a UI hint or message when an actor steps onto a charging platform.
- **GreatAiApprovalTrigger**: Part of a scripted event sequence for the "Great AI" interaction.

## Implementation Details

Triggers are often implemented by overriding `isTriggeredBy` and `executeTrigger` from a base class. They use the [Cascade System](../cascade/README.md) to apply effects to the world.

### Proximity Triggers
Many triggers use `triggerRange` to define a radius within which they detect actors. This is checked by the server during actor movement.

## See Also

- [Vanilla Package Main README](../README.md)
- [Cascade System](../cascade/README.md) for how triggers apply effects.
- [Props Subpackage](../props/README.md) for entities that often host triggers.
