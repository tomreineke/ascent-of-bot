package com.cerebrallychallenged.hypogean.view.inibar

import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.buildParagraph
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.node
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.singleInitiativeCost
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.math.interpolate
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.Paint
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.SkiaPath
import com.cerebrallychallenged.jun.skiatree.SkiaTreeWidget
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.layout.Visibility.Companion.visibleIf
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.skiatree.node.NodeCore
import com.cerebrallychallenged.jun.skiatree.text.FontStyle
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import kotlin.math.roundToInt

internal class IniArrowNode(widget: SkiaTreeWidget, private val iniBarView: IniBarView) : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DeltaColor = GuiConfig.ColorByFactionRelation.getValue(Faction.Relation.SAME)

        val DestroyColor = FLinearColor.Red

        var deltaPaint: Paint = Paint().apply {
            style = PaintStyle.Stroke
            color = DeltaColor
            strokeWidth = 4.0f
            antiAlias = true
        }

        var destroyPaint: Paint = Paint().apply {
            style = PaintStyle.Stroke
            color = DestroyColor
            strokeWidth = 10.0f
            antiAlias = true
        }

        var arrowHeight: Int = 120

        var deltaTextStyle = GuiConfig.DefaultTextStyle.copy(
            fontSize = 128.0f,
            fontStyle = FontStyle.Bold,
            color = DeltaColor,
            outlineShadows = listOf(FLinearColor.White to 0.5f)
        )

        var destroyTextStyle = deltaTextStyle.copy(color = DestroyColor)
    }

    private val textNode = node {}

    private fun arrowPath(startX: Float, endX: Float, startY: Float, midY: Float, endY: Float): SkiaPath = SkiaPath().apply {
        moveTo(vec(startX, startY))
        lineTo(vec(startX, midY))
        lineTo(vec(endX, midY))
        lineTo(vec(endX, endY))
        moveTo(vec(endX - 5, endY + 20))
        lineTo(vec(endX, endY))
        moveTo(vec(endX + 5, endY + 20))
        lineTo(vec(endX, endY))
    }

    fun updateArrow(actionTable: ActionTable) {
        val visActiveActor = iniBarView.currentActiveVisIniHolder
        val iniCost = actionTable.singleInitiativeCost
        val visible = if (iniCost != null && visActiveActor != null) {
            val left = visActiveActor.left.toFloat()
            val top = visActiveActor.top.toFloat()
            val size = visActiveActor.size
            val right = left + size.x
            val bottom = top + size.y
            val arrowHeight = Style.arrowHeight.scaled
            val textShiftY = -Style.deltaTextStyle.fontSize.scaled
            val textShiftX = 8.scaled
            when (iniCost) {
                is InitiativeCost.KeepTurn -> {
                    iniBarView.updateDividerVisibility(-1)
                    val rightX = interpolate(left, 0.66f, right)
                    background[InputState.Empty] = Background.Path(
                        arrowPath(
                            rightX,
                            interpolate(left, 0.33f, right),
                            bottom,
                            bottom + arrowHeight,
                            bottom
                        ),
                        Style.deltaPaint
                    )
                    textNode.core = NodeCore.Paragraph(buildParagraph(textStyle = Style.deltaTextStyle) {
                        +"Keep Turn"
                    })
                    textNode.left = rightX.roundToInt() + textShiftX
                    textNode.top = bottom.roundToInt() + arrowHeight + textShiftY
                    true
                }
                is InitiativeCost.Destroy -> {
                    iniBarView.updateDividerVisibility(-1)
                    background[InputState.Empty] = Background.Path(SkiaPath().apply {
                        moveTo(vec(left, top))
                        lineTo(vec(right, bottom))
                        moveTo(vec(right, top))
                        lineTo(vec(left, bottom))
                    }, Style.destroyPaint)
                    textNode.core = NodeCore.Paragraph(buildParagraph(textStyle = Style.destroyTextStyle) {
                        +"Destroy"
                    })
                    textNode.left = left.roundToInt()
                    textNode.top = bottom.roundToInt()
                    true
                }
                is InitiativeCost.Delta -> {
                    iniBarView.updateDividerVisibility(iniCost.rounds)
                    val divider = iniBarView.timeSlotDividers.getOrNull(iniCost.rounds)
                    if (divider != null) {
                        background[InputState.Empty] = Background.Path(
                            arrowPath(
                                interpolate(left, 0.5f, right),
                                divider.left - 3.0f,
                                bottom,
                                bottom + arrowHeight,
                                (divider.top + divider.size.y).toFloat()
                            ),
                            Style.deltaPaint
                        )
                        textNode.core = NodeCore.Paragraph(buildParagraph(textStyle = Style.deltaTextStyle) {
                            +"+${iniCost.rounds}"
                        })
                        textNode.left = divider.left + textShiftX
                        textNode.top = bottom.roundToInt() + arrowHeight + textShiftY
                        true
                    } else {
                        false
                    }
                }
            }
        } else {
            iniBarView.updateDividerVisibility(-1)
            false
        }
        visibility = visibleIf(visible)
    }

    init {
        widget.layers[GuiLayer.Overlay].children.add(this)
        hitModel = HitModel.None
    }
}
