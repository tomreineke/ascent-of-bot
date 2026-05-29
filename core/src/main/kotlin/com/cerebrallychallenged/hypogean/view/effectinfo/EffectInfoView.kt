package com.cerebrallychallenged.hypogean.view.effectinfo

import com.cerebrallychallenged.hypogean.gui.GuiConfig.DefaultTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.MediumTitleTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.SmallTitleTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.entityPortrait
import com.cerebrallychallenged.hypogean.gui.entityRef
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.paragraphNode
import com.cerebrallychallenged.hypogean.gui.title
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.linguistics.signedString
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute
import com.cerebrallychallenged.hypogean.model.cascade.CausalChange
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.effect.DamageKind
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.richtext.toRichText
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.cascade.EffectiveDelta
import com.cerebrallychallenged.hypogean.vanilla.cascade.EnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.effects.EnergyCharging
import com.cerebrallychallenged.hypogean.view.DisplayInfo
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.util.process
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.skiatree.table.Column
import com.cerebrallychallenged.jun.skiatree.table.Table
import com.cerebrallychallenged.jun.skiatree.text.ParagraphBuilderContext

class DisplayEffectInfo(
    val attribute: SimpleIntAttribute<out Entity>,
    val causalChange: CausalChange,
    val effectiveDelta: EffectiveDelta
) : DisplayInfo()

class EffectInfoView(private val context: ViewFactory.Context) : View, FactionContext by context {
    companion object {
        val ColumnFormats: List<Column.() -> Unit> = listOf(
            {
                minWidth = 140
                align = Align.Max
            },
            {
                minWidth = 140
                align = Align.Max
            },
            {
                minWidth = 40
                align = Align.Max
            },
            {
                align = Align.Min
            }
        )
    }

    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): View = EffectInfoView(context)
    }

    private val viewModel = context.viewModel

    private val contentNode: Node

    private val mainNode = context.widget.layers[GuiLayer.Overlay].window(hasCloseButton = true) {
        contentNode = this
    }.apply {
        visibility = Visibility.Hidden
        closeListener = {
            visibility = Visibility.Hidden
            contentNode.children.clear()
            viewModel.updateModalViewVisibility(this@EffectInfoView, false)
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        if (change is DisplayEffectInfo) {
            contentNode.children.clear()
            contentNode.paragraphNode(DefaultTextStyle) {
                change.show()
            }
            mainNode.visibility = Visibility.Visible
            viewModel.updateModalViewVisibility(this, true)
        }
    }

    context(ParagraphBuilderContext)
    private fun DisplayEffectInfo.show() {
        title("Effect Result for Change of ${attribute.symbol} ${attribute.name}", MediumTitleTextStyle)
        val effectResult = causalChange.effectResult
        for (signedTable in effectResult.tables) {
            val table = signedTable.table
            title(table.kind.toString(), SmallTitleTextStyle)
            embed(Table(columnCount = 4).apply {
                hgap = 10
                for (columnIndex in 0..3) {
                    columns[columnIndex].apply(ColumnFormats[columnIndex])
                }
                val showBaseRow = table.kind !is EnergyCharging || table.sampledBase != 0
                if (showBaseRow) {
                    addRow().apply {
                        this[0].paragraphNode(DefaultTextStyle) {
                            +when (table.kind) {
                                is EnergyConsumption -> "Base Consumption"
                                is DamageKind -> "Base Damage"
                                else -> "Base Effect"
                            }
                        }
                        this[1].paragraphNode(DefaultTextStyle) {
                            table.effect.toRichText().process(viewModel)
                        }
                        this[2].paragraphNode(DefaultTextStyle) {
                            +table.sampledBase.signedString()
                        }
                        this[3].paragraphNode(DefaultTextStyle) {
                            when (val reason = table.reason) {
                                is EffectReason.ByEntity -> {
                                    val entity = reason.entity
                                    entity.icon?.let {
                                        entityPortrait(entity, it, 100, 0, viewModel)
                                        +" "
                                    }
                                    entityRef(entity, viewModel = viewModel)
                                }
                                is EffectReason.Named -> +reason.name
                            }
                        }
                    }
                }
                for (phase in table.phases) {
                    for ((index, modifier) in phase.modifiers.withIndex()) {
                        addRow().apply {
                            if (index == 0) {
                                this[0].paragraphNode(DefaultTextStyle) { +phase.phase.displayName }
                            }
                            this[1].paragraphNode(DefaultTextStyle) {
                                +modifier.modifier.toString()
                            }
                            this[2].paragraphNode(DefaultTextStyle) { +modifier.amount.signedString(usePlusSign = true) }
                            this[3].paragraphNode(DefaultTextStyle) {
                                when (val reason = modifier.reason) {
                                    is EffectModifiers.Reason.By -> entityRef(reason.entity, viewModel = viewModel)
                                    EffectModifiers.Reason.Cover -> +"Cover"
                                    is EffectModifiers.Reason.Distance -> +"${"%.1f".format(reason.distance)}m"
                                }
                            }
                        }
                        addRow().apply {
                            topStrokeWidth = 1.0f
                            this[2].paragraphNode(DefaultTextStyle) {
                                +phase.total.signedString()
                            }
                            this[3].paragraphNode(DefaultTextStyle) {
                                +"Total"
                            }
                        }
                    }
                }
                val lastTotal = table.phases.lastOrNull()?.total
                if (lastTotal != null && lastTotal < 0) {
                    addRow().apply {
                        this[2].paragraphNode(DefaultTextStyle) {
                            +"⇒ 0"
                        }
                        this[3].paragraphNode(DefaultTextStyle) {
                            +"Effective ${table.kind}"
                        }
                    }
                }

            })
            newLine()
            +"\u2800" // Forces a new line
            newLine()
        }
        title("Summary", SmallTitleTextStyle)
        embed(Table(columnCount = 4).apply {
            hgap = 10
            for (columnIndex in 0..3) {
                columns[columnIndex].apply(ColumnFormats[columnIndex])
            }
            for (signedTable in effectResult.tables) {
                val table = signedTable.table
                addRow().apply {
                    this[2].paragraphNode(DefaultTextStyle) {
                        +(table.amount * signedTable.sign).signedString(usePlusSign = true)
                    }
                    this[3].paragraphNode(DefaultTextStyle) {
                        +"${table.kind}"
                    }
                }
            }
            addRow().apply {
                topStrokeWidth = 1.0f
                this[2].paragraphNode(DefaultTextStyle) {
                    +effectResult.delta.signedString(usePlusSign = true)

                }
                this[3].paragraphNode(DefaultTextStyle) {
                    +"Total"
                }
            }
        })
        effectiveDelta.explanation(attribute)?.let { explanation ->
            newLine(onlyIfNotAtStartOfLine = false)
            +explanation
        }
    }
}
