# SkiaTree Package

The `com.cerebrallychallenged.jun.skiatree` package provides a high-performance, 2D scene tree rendering system based on [Google's Skia Graphics Library](https://skia.org/). It serves as the foundational rendering engine for the game's [GUI](../hypogean/gui/README.md).

## Architecture

SkiaTree is designed as a Kotlin wrapper around a native Rust library (`skiatree.dll`). It utilizes the **Java Foreign Function & Memory (FFM) API** (JEP 454) to achieve low-overhead interop between the JVM and native code.

### Core Components

- **[Node](node/README.md)**: The base class for all elements in the scene tree. Nodes handle layout, parent-child relationships, and visual transformations.
- **[SkiaTreeWidget](SkiaTreeWidget.kt)**: The integration point with the Unreal Engine Slate system, providing a canvas for rendering.
- **[Layers](Layers.kt)**: Manages multiple independent scene trees (layers) within a single widget, allowing for complex UI stacking (e.g., HUD, menus, tooltips).
- **[SkiaTreeApi](SkiaTreeApi.kt)**: Internal utility for managing native method handles, upcalls, and memory segments.

## Subpackages

- **[geo](geo/)**: Primitive geometry types including `Rect`, `IRect`, `Point`, and `IPoint`.
- **[input](input/)**: Native input event handling, hit testing, and tooltip management.
- **[layout](layout/)**: Flex-like layout system including alignment, visibility, and parameter delegates.
- **[node](node/)**: Specific node implementations like `ImageNode` and the base `Node` logic.
- **[table](table/)**: Declarative table layout components.
- **[text](text/)**: Rich text rendering integration using Skia's Paragraph module.

## Native Interop

The system relies on a set of guarded primitives (`guardedUnit`, `guardedPointer`, etc.) to ensure that native resources are managed safely and that errors from the native side are captured and logged as Kotlin exceptions.

Resources that wrap native pointers (like `Paragraph`, `Surface`, or `Node`) implement `AutoCloseable` (often via `CloseableKey` or `CloseableResource`) to prevent memory leaks.

## Integration with GUI

The [GUI package](../hypogean/gui/README.md) provides a higher-level, DSL-based abstraction over SkiaTree. While SkiaTree focuses on the efficient rendering and layout of the scene tree, the GUI package handles game-specific UI components, resource management, and theme styling.

---
*Part of the Jun Framework for Hypogean.*
