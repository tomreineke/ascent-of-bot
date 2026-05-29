# Hypogean Vanilla Cascade System

The `cascade` subpackage implements the game's consequence and effect system. It defines how actions and events result in changes to the world state, such as health reduction, energy consumption, or entity destruction.

## Overview

The cascade system is designed to handle complex chains of effects. When an effect is "dealt" to a target, it can trigger multiple consequences based on the target's attributes, equipment, and status effects. It builds upon the [Model Cascade Package](../../model/cascade/README.md) and [Model Effect Package](../../model/effect/README.md).

## Key Components

- **CascadeBlock**: (from Model) A context for executing changes that can be scheduled and processed sequentially.
- **DealEffect**: The entry point for applying an effect (damage, healing, etc.) to an entity. It handles modifiers (absolute/relative) and immunities.
- **Consequence**: Specific implementations of `EffectConsequence`, such as `ChangeHealthConsequence` or `AddStatusEffectConsequence`.
- **EffectReason**: (from Model) Documents *why* an effect happened.

## Logic Flow

1. **Sampling**: The base value of an effect (e.g., 10 damage) is sampled, potentially including randomness.
2. **Modifiers**: The system applies modifiers from various sources:
   - Passive modifiers from the target's equipment or status effects.
   - Active modifiers from the action itself.
3. **Immunities**: If the target has immunity to a specific `EffectKind`, the effect is blocked.
4. **Consequence Dispatch**: Based on the `EffectKind` (e.g., `BluntDamage`, `LaserDamage`), the system looks up registered consequences and schedules them for execution.
5. **Execution**: Consequences modify attributes (Health, Energy) or trigger further events (like `DestroyEntity`).

## Important Files

- `DealEffect.kt`: Core logic for applying effects and calculating modified values.
- `DealAreaEffect.kt`: Handles effects that apply to multiple tiles/entities in a radius.
- `ChangeHealthConsequence.kt` / `ChangeEnergyConsequence.kt`: Implement the actual modification of actor stats.
- `AddStatusEffectConsequence.kt`: Handles the application of ongoing effects like `Burning` or `HealingOverTime`.

## See Also

- [Vanilla Package Main README](../README.md)
- [Actions Subpackage](../actions/README.md) for where effects usually originate.
- [Triggers Subpackage](../triggers/README.md) for environmental effect sources.
- [Model Cascade Package](../../model/cascade/README.md) for base cascade logic.
- [Model Effect Package](../../model/effect/README.md) for effect and modifier definitions.
