# Hypogean Model Triggers

This subpackage contains foundational logic for event-driven interactions and movement-based triggering.

## Key Concepts

- **CurvedMoveSegment**: Represents a portion of an actor's movement path, including the precise curve and the cells it intersects.
- **Triggering Path**: The movement of an actor can be analyzed to determine which cells are "traversed" or "intersected", which in turn can fire triggers (like mines or environmental hazards) located on those cells.

## Path Processing

The system converts a high-level `CellPath` into a sequence of `CurvedMoveSegment`s. This allows for:
- Precise visibility checks along the path.
- Detection of intersected cells that might contain traps or triggers.
- Smooth visual representation of movement.

## See Also

- [Model Main README](../README.md)
- [Vanilla Triggers](../../vanilla/triggers/README.md) for concrete implementations of triggers like `MineTrigger`.
