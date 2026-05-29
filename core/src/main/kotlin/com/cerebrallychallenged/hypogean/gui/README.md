# Hypogean GUI Package

This package provides a foundational UI framework and DSL built on top of the [SkiaTree](../../../jun/skiatree/README.md) rendering engine. It defines the core building blocks, styling systems, and resource management used by the [View Package](../view/README.md) to construct the game's interface.

## Overview

The GUI package implements a declarative, node-based system for creating layouts and components. It handles scaling, resource caching, and provides high-level abstractions for common UI patterns like buttons, windows, and tooltips.

## Key Components

### 1. Layout & Nodes
- **Node**: The base class for all UI elements.
- **VBox / HBox**: Layout containers for vertical and horizontal stacking of nodes.
- **Window**: A styled container for modal or non-modal dialogs, supporting optional close buttons.
- **NodeExtensions**: DSL helpers (like `node { ... }`) for hierarchical UI construction.

### 2. UI Components
- **StandardButton**: A general-purpose button with support for states (Normal, Hovered, Pressed) and sound effects.
- **FunctionButton**: A specialized button for action-specific UI elements.
- **Tooltip**: Implementation for hoverable information overlays.
- **VerticalScrollView / VerticalScrollBar**: Components for handling scrollable content.

### 3. Resource & Style Management
- **ResourceLibrary**: An object that manages the loading, scaling, and weak-reference caching of `GuiResource`s (Images, NinePatches, TextStyles).
- **GuiResource**: Interfaces and data classes defining UI assets (`ImageResource`, `TextStyleResource`, etc.).
- **GuiConfig**: Centralized configuration for scaling, default text styles, and relation-based coloring.
- **Styling**: A functional interface for applying declarative styles to nodes, allowing for theme-like overrides.

### 4. Rich Text (ParagraphBuilder)
Extensions in `ParagraphBuilderExtensions.kt` allow for the creation of interactive, game-aware text:
- **entityRef**: Creates clickable links to game [Entities](../model/README.md#core-entities) that trigger info views.
- **entityPortrait**: Embeds entity portraits directly into text flows.
- **title**: Standardized title formatting within paragraphs.

## Scaling System

The framework uses a global `guiScale` defined in `GuiConfig`. Components and resources use the `.scaled` extension property to ensure that the UI remains consistent across different resolutions.

## Integration with View Package

The [View Package](../view/README.md) uses this package to define high-level game screens (like the `ReportView` or `ActorStatusView`). While the `gui` package provides the "how" of the UI (buttons, boxes, styles), the `view` package provides the "what" (entity data, game state binding).

## Modding

Most style objects and configurations are marked as `open` or `var` to allow mods to customize the look and feel of the game's interface by overriding `GuiConfig` or providing custom `Styling` implementations.
