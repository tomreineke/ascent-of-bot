# Hypogean Model Effects

This subpackage defines the system for representing and modifying game properties through "Effects" and "Modifiers".

## Key Concepts

- **Effect**: A collection of `EffectValue`s that represent a payload of changes (e.g., 10 Fire Damage, 5 Healing).
- **EffectKind**: An abstract category of effect (e.g., `PhysicalDamage`, `EnergyRestoration`).
- **EffectValue**: A specific magnitude and kind of effect.
- **EffectModifier**: A collection of `EffectValueModifier`s that alter how effects are applied to an entity.
- **EffectModifierKind**: Defines how a modifier works (e.g., `Absolute` reduction or `Relative` percentage change).

## Usage

### Direct Effects
Entities can have a `directEffect` attribute, which defines the intrinsic effects they deal (e.g., a weapon's base damage).

### Passive Modifiers
Actors and items can have `passiveEffectModifier` attributes. When an effect is applied to an actor, the system checks these modifiers to calculate the final result (e.g., armor reducing incoming damage).

### Area Effects
The `AreaEffect` class handles effects that spread over multiple cells, potentially with falloff logic.

## See Also

- [Model Main README](../README.md)
- [Vanilla Cascade System](../../vanilla/cascade/README.md) for the logic that processes these effects and applies consequences to the world.
