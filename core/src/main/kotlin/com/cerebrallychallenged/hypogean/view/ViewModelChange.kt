package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ViewActionInstance

sealed class ViewModelChange

data class ModelChange(val changes: List<WorldChange>) : ViewModelChange()

data class ActionInputStateChanged(val prevState: ActionInputState, val newState: ActionInputState) : ViewModelChange() {
    val hasHoverChanged: Boolean
        get() = prevState.hovered != newState.hovered

    val hasSelectionChanged: Boolean
        get() = prevState.selected != newState.selected

    val hasPrefixChanged: Boolean
        get() = prevState.prefix != newState.prefix

    val hasCompletenessChanged: Boolean
        get() = prevState.completeness != newState.completeness
}

data class EntityHovered(val entity: Entity?) : ViewModelChange()

data class ActionSubmitted(val actionInstance: ActionInstance) : ViewModelChange()

data class ViewActionExecuted(val viewActionInstance: ViewActionInstance) : ViewModelChange()

data class GuiScaled(val scale: Double) : ViewModelChange()

data class PickupRadialViewDisplay(
    val instances: List<ActionInstance>,
    val submit: (ActionInstance) -> Unit
) : ViewModelChange()

abstract class DisplayInfo : ViewModelChange()

/**
 * Indicates a visibility update for evading views, i.e., views which hide if a modal view is shown.
 * A value of `true` or `false` indicates that the evading view should become visible or invisible, respectively.
 * A value of `null` indicates that the visibility for the evading view should not change.
 */
data class ModalViewVisibilityChanged(val isAnyModalViewVisible: Boolean) : ViewModelChange()
