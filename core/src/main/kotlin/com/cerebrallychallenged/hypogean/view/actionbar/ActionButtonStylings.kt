package com.cerebrallychallenged.hypogean.view.actionbar

import com.cerebrallychallenged.hypogean.gui.GuiConfig.DisabledColorFilterPaint
import com.cerebrallychallenged.hypogean.gui.GuiConfig.HoveredColorFilterPaint
import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.scaling
import com.cerebrallychallenged.hypogean.gui.tooltip
import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.EntityType
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.Rulebook
import com.cerebrallychallenged.hypogean.model.RulebookContext
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionMode
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.singleTool
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.hypogean.view.tooltip.createTooltip
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.geo.scale


abstract class ActionButtonStylings(private val block: Block.() -> Unit) {
    class StyleBuilder(val actionTable: ActionTable, val viewModel: ViewModel) {
        lateinit var icon: ActionIconRef

        private val badges = mutableListOf<BadgeGroup<*>.Entry>()

        var tooltip: Tooltip? = actionTable.singleTool?.let { tool ->
            if (actionTable.instances.isEmpty() && actionTable.obstacles.isNotEmpty()) {
                actionTable.createObstaclesTooltip()
            } else {
                tool.createTooltip(viewModel, false)
            }
        }

        fun <T : Any> badge(group: BadgeGroup<T>, value: T) {
            badges.add(group to value)
        }

        fun badge(group: BadgeGroup<Unit>) {
            badges.add(group to Unit)
        }

        internal fun create(): Styling<ActionButton, Unit> = Styling {
            scaling(0.64f) {
                val iconPath = buildString {
                    append("Images/generated/action_buttons/")
                    append(icon.id)
                    for ((id, value) in badges) {
                        append('-')
                        append(id)
                        append(value)
                    }
                }
                val overshoot = ActionButton.Style.Overshoot
                val standardImage = Background.Image(
                    ResourceLibrary[ImageResource("$iconPath.png"), guiScale],
                    overshoot.scale(guiScale)
                )
                this@StyleBuilder.tooltip?.let { tooltip = it }
                if (isEnabled) {
                    background[InputState.Empty] = standardImage
                    background[InputState.Hovered] = standardImage.copy(paint = HoveredColorFilterPaint)
                    val activeImage = Background.Image(
                        ResourceLibrary[ImageResource("$iconPath-active.png"), guiScale],
                        overshoot.scale(guiScale)
                    )
                    background[InputState.Pressed] = activeImage
                    background[InputState.Pressed, InputState.Hovered] = activeImage.copy(paint = HoveredColorFilterPaint)
                    if (!isIntransitive) {
                        val selectedImage = Background.Image(
                            ResourceLibrary[ImageResource("$iconPath-selected.png"), guiScale],
                            overshoot.scale(guiScale)
                        )
                        background[InputState.Selected] = selectedImage
                        background[InputState.Selected, InputState.Hovered] = selectedImage.copy(paint = HoveredColorFilterPaint)
                        val selectedActiveImage = Background.Image(
                            ResourceLibrary[ImageResource("$iconPath-selected-active.png"), guiScale],
                            overshoot.scale(guiScale)
                        )
                        background[InputState.Selected, InputState.Pressed] = selectedActiveImage
                        background[InputState.Selected, InputState.Pressed, InputState.Hovered] = selectedActiveImage.copy(paint = HoveredColorFilterPaint)
                    }
                } else {
                    background[InputState.Empty] = standardImage.copy(paint = DisabledColorFilterPaint)
                }
            }
        }
    }

    internal data class Entry(val glob: ActionGlob, val initBlock: StyleBuilder.() -> Unit)

    class Block internal constructor(
        rulebook: Rulebook,
        private val add: (Entry) -> Unit
    ): RulebookContext by rulebook {
        fun defineStyling(
                category: ActionCategory? = null,
                action: Action? = null,
                tool: EntityType<Item>? = null,
                mode: ActionMode? = null,
                setterBlock: StyleBuilder.() -> Unit
        ) {
            add(Entry(ActionGlob(category, action, tool, mode), setterBlock))
        }
    }

    internal fun collectStylings(rulebook: Rulebook, add: (Entry) -> Unit) {
        Block(rulebook, add).block()
    }
}

class ActionButtonDefinitionsRegistry : SimpleObjectRegistry<ActionButtonStylings>()

class ActionButtonStylingLibrary(rulebook: Rulebook) {
    private val entries: List<ActionButtonStylings.Entry> = mutableListOf<ActionButtonStylings.Entry>().also {
        for (definition in rulebook.feature<ActionButtonDefinitionsRegistry>()) {
            definition.collectStylings(rulebook, it::add)
        }
    }

    fun computeStyling(actionTable: ActionTable, viewModel: ViewModel): Styling<ActionButton, Unit> {
        val actualGlob = actionTable.toGlob()
        val initBlock = entries.maxByOrNull { it.glob.matchingScore(actualGlob) }!!.initBlock
        val builder = ActionButtonStylings.StyleBuilder(actionTable, viewModel).also(initBlock)
        if (actionTable.instances.isEmpty() && actionTable.obstacles.isNotEmpty()) {
            builder.tooltip = actionTable.createObstaclesTooltip()
        }
        return builder.create()
    }
}

fun ActionTable.createObstaclesTooltip(): Tooltip = Tooltip {
    +obstacleDescriptions.joinToString(", ")
}
