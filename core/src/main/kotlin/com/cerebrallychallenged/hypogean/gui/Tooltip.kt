package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.Paint
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.SkiaTreeWidget
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.input.TooltipHandler
import com.cerebrallychallenged.jun.skiatree.layout.Margin
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.skiatree.node.NodeCore
import com.cerebrallychallenged.jun.skiatree.text.ParagraphBuilderContext
import com.cerebrallychallenged.jun.skiatree.text.buildParagraph
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import kotlin.math.min

class Tooltip(
    private val placementStrategy: PlacementStrategy = DefaultPlacementStrategy,
    private val create: ParagraphBuilderContext.() -> Unit
) {
    enum class Placement {
        Left,
        Top,
        Right,
        Bottom;

        fun isPlaceable(receiverRect: IRect, tooltipSize: Vec2i, widgetSize: Vec2i, distance: Int): Boolean = when (this) {
            Left -> tooltipSize.x + distance <= receiverRect.left
            Top -> tooltipSize.y + distance <= receiverRect.top
            Right -> tooltipSize.x + distance <= widgetSize.x - receiverRect.right
            Bottom -> tooltipSize.y + distance <= widgetSize.y - receiverRect.bottom
        }

        fun computePosition(
            receiverRect: IRect,
            tooltipSize: Vec2i,
            widgetSize: Vec2i,
            distance: Int,
            mousePosition: Vec2i
        ): Vec2i {
            val x = when (this) {
                Left -> receiverRect.left - distance - tooltipSize.x
                Right -> receiverRect.right + distance
                Top, Bottom -> mousePosition.x.coerceAtMost(widgetSize.x - tooltipSize.x).coerceAtLeast(0)
            }
            val y = when (this) {
                Left, Right -> mousePosition.y.coerceAtMost(widgetSize.y - tooltipSize.y).coerceAtLeast(0)
                Top -> receiverRect.top - distance - tooltipSize.y
                Bottom -> receiverRect.bottom + distance
            }
            return vec(x, y)
        }
    }

    class PlacementStrategy(private val firstPlacement: Placement, private vararg val remainingPlacements: Placement) {
        fun computePosition(
            receiverRect: IRect,
            tooltipSize: Vec2i,
            widgetSize: Vec2i,
            distance: Int,
            mousePosition: Vec2i
        ): Vec2i {
            val placement = sequenceOf(firstPlacement, *remainingPlacements).firstOrNull {
                it.isPlaceable(receiverRect, tooltipSize, widgetSize, distance)
            } ?: firstPlacement
            return placement.computePosition(receiverRect, tooltipSize, widgetSize, distance, mousePosition)
        }
    }

    companion object {
        val DefaultPlacementStrategy = PlacementStrategy(Placement.Top, Placement.Bottom)
    }

    fun show(widget: SkiaTreeWidget, receiverBounds: IRect, mousePosition: Vec2i): Node = Node().apply {
        children.add(Node().apply {
            margin = Margin.all(5)
            core = NodeCore.Paragraph(buildParagraph(block = create))
        })
        background[InputState.Empty] = Background.Rect(
            Paint().apply {
                style = PaintStyle.Fill
//                    color = FLinearColor.rgba(0.3f, 0.3f, 0.3f, 0.85f)
                color = FLinearColor.rgb(0.1f, 0.1f, 0.1f)
            },
            Paint().apply {
                style = PaintStyle.Stroke
                color = FLinearColor.rgb(0.4f, 0.4f, 0.4f)
            },
            5.0f
        )
        widget.layers[GuiLayer.Tooltip].children.add(this)
        forceLayout(vec(min(1000, widget.size.x), min(1000, widget.size.y)))
        val position = placementStrategy.computePosition(
            receiverBounds,
            size,
            widget.size,
            5,
            mousePosition
        )
        left = position.x
        top = position.y
    }
}

class DefaultTooltipHandler(internal val tooltip: Tooltip) : TooltipHandler {
    private var node: Node? = null

    override fun showTooltip(receiver: Node, mousePosition: Vec2i) {
        hideTooltip()
        val widget = receiver.widget ?: return
        node = tooltip.show(widget, receiver.bounds, mousePosition)
    }

    override fun hideTooltip() {
        node?.apply {
            detach()
            close()
        }
        node = null
    }
}

var Node.tooltip: Tooltip?
    get() = (tooltipHandler as? DefaultTooltipHandler)?.tooltip
    set(value) {
        tooltipHandler?.hideTooltip()
        tooltipHandler = value?.let { DefaultTooltipHandler(it) }
    }
