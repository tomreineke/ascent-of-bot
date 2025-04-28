package com.cerebrallychallenged.hypogean.view.mouse

import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.util.ConstraintsMap
import com.cerebrallychallenged.hypogean.util.MutableConstraintsMap
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.util.mouse.MouseCursor

class MouseView(val context: ViewFactory.Context, private val cursors: ConstraintsMap<ActionTable, MouseCursor>) : View {
    companion object {
        var actionMouseCursor: MouseCursor? = null
            set(value) {
                field = value
                updateMouseCursor()
            }

        private val cursorMap = mutableMapOf<Any, MouseCursor>()

        fun addMouseCursor(requester: Any, mouseCursor: MouseCursor) {
            cursorMap[requester] = mouseCursor
            updateMouseCursor()
        }

        fun removeMouseCursor(requester: Any) {
            cursorMap.remove(requester)
            updateMouseCursor()
        }

        private fun updateMouseCursor() {
            MouseCursor.currentCursor = cursorMap.values.firstOrNull() ?: actionMouseCursor ?: MouseCursor.Default
        }
    }

    class Factory : ViewFactory {
        val cursors = MutableConstraintsMap<ActionTable, MouseCursor> { MouseCursor.Default }

        override suspend fun create(context: ViewFactory.Context): View = MouseView(context, cursors)
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        if (change is ActionInputStateChanged) {
            if (change.hasSelectionChanged || change.hasHoverChanged) {
                val hovered = change.newState.hovered
                actionMouseCursor = when {
                    !hovered.isSingleTargetFocused -> MouseCursor.Default
                    hovered.hasInstances() -> cursors[hovered]
                    hovered.hasObstacles() -> MouseCursor.Obstacle
                    else -> MouseCursor.Default
                }
            }
        }
    }
}
