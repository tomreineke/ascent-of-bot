# Hypogean View Package

This package contains the core user interface architecture and implementations for the Hypogean game, built with Kotlin and integrated with Unreal Engine 5.1. It is built upon the foundational [GUI Package](../gui/README.md).

## Overview

The UI system follows a MVVM-like pattern where `ViewModel` manages the game state for the UI, and `View` implementations handle the rendering and user interaction using the [GUI DSL](../gui/README.md).

## Key Components

- **View**: The base interface for all UI components. It defines lifecycle methods like `onViewModelChange`, `onTick`, and `onInput`.
- **ViewModel**: Acts as the bridge between the [Hypogean Server](../server/README.md) and the UI. It manages UI state, animations, action submissions, and input processing.
- **ViewManager**: Responsible for managing the lifecycle of all active views, distributing ticks, input events, and view model changes.
- **ViewBaseMod**: A base class for modifiable views, providing common functionality for views that can be extended or modified.

## Subpackages

The package is organized into several subpackages, each responsible for a specific part of the UI:

- **actionbar**: Implementation of the player's action bar.
- **dialog**: Systems for handling in-game dialogues.
- **map**: UI elements related to the game map and world interaction.
- **modular**: A system for building complex views from smaller, reusable modules (e.g., `CharacterView`, `InfoView`).
- **mouse**: Cursor management and mouse-related UI logic.
- **radialmenu**: Radial menu implementation for quick action selection.
- **common/util**: Shared UI utilities and common assets.

## Usage

Views are typically defined and instantiated in a central location, such as the [Vanilla Package](../vanilla/README.md)'s `DefaultViews.kt`, using a `ViewsDefinition` DSL. This allows for a declarative way to set up the game's UI structure and behavior.

```kotlin
object DefaultViews : ViewsDefinition({
    view(ActionBarView::Factory) {
        // configuration
    }
    // ... other views
})
```

## Threading

UI operations generally happen on the Unreal thread to ensure safe interaction with Unreal Engine components. The `View` interface methods are explicitly documented to be executed in the Unreal thread.
