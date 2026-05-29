# Hypogean Model Cascade

This subpackage defines the foundational system for processing game-state cascades and their consequences.

## Key Concepts

- **CascadeBlock**: A context for executing changes that can be scheduled and processed sequentially. It provides the environment for effects to trigger further changes.
- **EffectConsequence**: The base class for any specific result of an effect (e.g., reducing health). It provides a bridge between the abstract effect and the concrete world modification.
- **EffectReason**: A data structure that documents *why* an effect happened (e.g., `ByEntity` or a `Named` reason). This is essential for transparency in the UI and logging.
- **EffectResult**: Represents the final, calculated outcome of an effect after all modifiers have been applied.
- **CausalChange**: An interface representing a change that has a specific cause and a measurable impact (delta).

## Architecture

The model cascade system is designed to be content-agnostic. It provides the mechanisms for:
1.  **Scheduling**: Running complex, multi-step sequences of events that might include delays.
2.  **Attribution**: Tracking the origin of effects via `EffectReason`.
3.  **Result Calculation**: Storing the breakdown of how a base effect value was transformed by various modifiers into a final `EffectResult`.

## See Also

- [Model Main README](../README.md)
- [Model Effect Package](../effect/README.md) for the definitions of effects and modifiers.
- [Vanilla Cascade System](../../vanilla/cascade/README.md) for concrete implementations of consequences and the `dealEffect` logic.
