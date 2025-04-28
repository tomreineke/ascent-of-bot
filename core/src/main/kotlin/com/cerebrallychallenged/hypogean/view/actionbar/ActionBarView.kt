package com.cerebrallychallenged.hypogean.view.actionbar

import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionMap
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.singleCategory
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.hypogean.view.ActionSubmitted
import com.cerebrallychallenged.hypogean.view.ModalViewVisibilityChanged
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.Node

class ActionBarView private constructor(
    context: ViewFactory.Context,
    private val controllers: Map<ActionCategory, MergeConfig>
) : View, FactionContext by context {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DefaultStyle = Styling<Node, Unit> {
            left = 20.scaled
            right = 1600.scaled
            horizontalAlign = Align.Center
            verticalAlign = Align.Max
            flow = Flow.LeftToRightThenBottomToTop
//            background[InputState.Empty] = Background.Rect(FLinearColor.Green, PaintStyle.Fill)
        }

        var style: Styling<Node, Unit> = DefaultStyle
    }

    class Factory : ViewFactory {
        private val controllers = mutableMapOf<ActionCategory, MergeConfig>()

        fun defineController(
            actionCategory: ActionCategory,
            mergeActions: Boolean,
            mergeTools: Boolean,
            mergeModes: Boolean
        ) {
            controllers[actionCategory] = MergeConfig(mergeActions, mergeTools, mergeModes)
        }

        override suspend fun create(context: ViewFactory.Context): View {
            return ActionBarView(context, controllers)
        }
    }

    private data class MergeConfig(val mergeActions: Boolean, val mergeTools: Boolean, val mergeModes: Boolean)

    private val rulebook = context.rulebook

    private val viewModel = context.viewModel

    private val widget = context.widget

    private val stylingLibrary = ActionButtonStylingLibrary(rulebook)

    private var mainNode: Node? = null

    private val currentActionButtons: MutableList<ActionButton> = mutableListOf()

    private val changeVisitor = object : WorldChange.SimpleVisitor {
        override fun visit(change: WorldChange.Clear) {
            hide()
        }

        override fun visit(change: WorldChange.ActiveStateChanged) {
            val availableActions = change.ownActions
            if (availableActions != null) {
                show(availableActions)
            } else {
                hide()
            }
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        when (change) {
            is ModelChange -> change.changes.forEach { it.accept(changeVisitor) }
            is ActionSubmitted -> hide()
            is ActionInputStateChanged -> {
                val (prevState, newState) = change
                if (prevState.selected != newState.selected) {
                    val newSelected = newState.selected
                    for (button in currentActionButtons) {
                        val table = button.actionTable
                        button.isSelected = table == newSelected || newState.originalPrefix in table.instances
                    }
                    // Move to IniBar
//                    newSelected.instances.map {
//                        it.tool.baseActiveEnergyConsumption + it.tool.adjustableActiveEnergyConsumption
//                    }.distinct().singleOrNull()?.let {
//                        val activeActor = newSelected.activeActor!!
//                        webUI.send(UpdateEnergyDisplay(activeActor.energy, activeActor.maxEnergy, it))
//                    }
                }

            }
            is ModalViewVisibilityChanged -> {
                mainNode?.visibility = Visibility.visibleIf(!change.isAnyModalViewVisible)
            }
            else -> {}
        }
    }

    fun hide() {
        mainNode?.detach()
        mainNode = null
        currentActionButtons.clear()
    }

    fun show(availableActions: ActionTable) {
        hide()
        val tables = controllers.flatMap { (category, mergeConfig) ->
            val (mergeActions, mergeTools, mergeModes) = mergeConfig
            val tableForCategory = availableActions.groupedByCategory[category]

            // Shortcut: If we have no action instances and no obstacles for that category, skip the split.
            // We check the obstacles because if there are any, an action button with a respective tooltip shall
            // be displayed.
            if (!tableForCategory.hasInstances() && !tableForCategory.hasObstacles()) return@flatMap listOf()

            var tableList: Collection<ActionTable> = listOf(tableForCategory)
            fun <T> Collection<ActionTable>.splitEachBy(
                fn: ActionTable.() -> ActionMap<T>
            ): Collection<ActionTable> = flatMap { it.fn().values }

            if (!mergeActions) {
                tableList = tableList.splitEachBy { groupedByAction }
            }
            if (!mergeTools) {
                tableList = tableList.splitEachBy { groupedByEquipment }
            }
            if (!mergeModes) {
                tableList = tableList.splitEachBy { groupedByMode }
            }

            tableList
        }

        mainNode = Node().also { node ->
            node.debugName = "ActionBarView"
            this@ActionBarView.widget.layers[GuiLayer.Base].children.add(node)
            node.applyStyle(Style.style)
            for (table in tables) {
                val button = ActionButton(table, viewModel).apply {
                    debugName = "ActionButton[${table.singleCategory}]"
                    applyStyle(stylingLibrary.computeStyling(table, viewModel))
                }
                node.children.add(button)
                currentActionButtons.add(button)
            }
        }
    }
}
