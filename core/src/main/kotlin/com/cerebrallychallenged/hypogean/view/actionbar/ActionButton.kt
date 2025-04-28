package com.cerebrallychallenged.hypogean.view.actionbar

import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.scaling
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.isIntransitive
import com.cerebrallychallenged.hypogean.model.action.isSingleton
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.jun.input.InputReason
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.node.Node

class ActionButton(val actionTable: ActionTable, val viewModel: ViewModel) : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val Overshoot = IRect(6, 6, 15, 14)

        private val DefaultHitModel = HitModel.RoundedRect(Overshoot, 260)

        val DefaultStyle = Styling<ActionButton, Unit> {
            scaling(0.64f) {
                val size = 539.scaled
                minWidth = size
                maxWidth = size
                minHeight = size
                maxHeight = size
                hitModel = DefaultHitModel.scale(guiScale)
            }
            horizontalAlign = Align.Center
        }

        var style: Styling<ActionButton, Unit> = DefaultStyle
    }

    val isEnabled: Boolean = actionTable.hasInstances()
    val isIntransitive: Boolean = actionTable.isIntransitive
    val isSingleton: Boolean = actionTable.isSingleton

    init {
        primaryPressedListeners += { _, _ ->
            true
        }
        primaryReleasedListeners += { _, hoveredNode, _ ->
            if (isAncestorOf(hoveredNode)) {
                when {
                    !isEnabled -> {}
                    isIntransitive -> {
                        if (actionTable.isSingleton) {
                            viewModel.submitAction(actionTable.instances.single())
                        } else {
                            // FIXME open radial menu
                        }
                    }
                    else -> {
                        if (isSelected) {
                            isSelected = false
                            viewModel.selectViewActions()
                        } else {
                            isSelected = true
                            viewModel.selectActions(actionTable)
                        }
                    }
                }
            }
            true
        }
        hoverListeners += { hovered ->
            viewModel.hoverActions(if (hovered) actionTable else ActionTable.Empty, InputReason.GUI)
        }
        applyStyle(Style.style)
    }
}
