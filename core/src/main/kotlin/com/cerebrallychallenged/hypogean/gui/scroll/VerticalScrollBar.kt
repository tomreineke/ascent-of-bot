package com.cerebrallychallenged.hypogean.gui.scroll

import com.cerebrallychallenged.hypogean.gui.GuiConfig.guiScale
import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.gui.NinePatchResource
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.Node
import kotlin.math.max

class VerticalScrollBar : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val BarImage = ImageResource("Images/gui/slider-bar.png")

        val BarBackground = NinePatchResource(
            BarImage,
            IRect(8, 7, 10, 2),
            IRect(59, 59, 60, 60)
        )

        const val DefaultBarWidth = 104

        var barWidth = DefaultBarWidth

        val BarDefaultStyle = Styling<VerticalScrollBar, Unit> {
            background[InputState.Empty] = ResourceLibrary[BarBackground, guiScale]
            val realWidth = barWidth.scaled
            minWidth = realWidth
            maxWidth = realWidth
            clippingTop = (BarBackground.center.top - BarBackground.overshoot.top).scaled
            clippingBottom = (ResourceLibrary[BarBackground.image].size.y - BarBackground.center.bottom).scaled
            verticalAlign = Align.Stretch
        }

        var barStyle: Styling<VerticalScrollBar, Unit> = BarDefaultStyle

        val ThumbnailImage = ImageResource("Images/gui/slider-button.png")

        val ThumbnailBackground = NinePatchResource(
            ThumbnailImage,
            IRect(8, 7, 10, 2),
            IRect(60, 59, 61, 60)
        )

        val DefaultThumbnailStyle = Styling<Node, Unit> {
            background[InputState.Empty] = ResourceLibrary[ThumbnailBackground, guiScale]
            horizontalAlign = Align.Stretch
        }

        var thumbnailStyle: Styling<Node, Unit> = DefaultThumbnailStyle
    }

    var clippingTop: Int = 0

    var clippingBottom: Int = 0

    var totalHeight: Int = 0
        set(value) {
            field = value
            updateThumbnail()
        }

    var viewportHeight: Int = 0
        set(value) {
            field = value
            updateThumbnail()
        }

    val maxValue: Int
        get() = (totalHeight - viewportHeight).coerceAtLeast(0)

    private var maxTop: Int = 0

    private val thumbnailNode = Node().apply {
        this@VerticalScrollBar.children.add(this)
        applyStyle(Style.thumbnailStyle)
    }

    private fun updateThumbnail() {
        val usableHeight = viewportHeight - clippingTop - clippingBottom
        val height = clippingTop + clippingBottom + if (totalHeight > 0) {
            (viewportHeight * usableHeight / max(totalHeight, viewportHeight).toFloat()).ceilToInt()
        } else 0
        thumbnailNode.minHeight = height
        thumbnailNode.maxHeight = height
        thumbnailNode.top = 0
        maxTop = viewportHeight - height
//        JunManager.LOGGER.info { "totalHeight=$totalHeight viewportHeight=$viewportHeight clippingTop=$clippingTop clippingBottom=$clippingBottom usableHeight=$usableHeight height=$height" }
        visibility = if (maxValue > 0) Visibility.Visible else Visibility.Hidden
    }

    private var topAtDragStart: Int = 0

    private var mouseAtDragStart: Int = 0

    var value: Int = 0
        private set(value) {
            field = value
            for (listener in valueListeners) {
                listener(value)
            }
        }

    var valueListeners: List<(Int) -> Unit> = listOf()

    fun scrollTo(requestedValue: Int) {
        val maxScroll = maxValue
        val newValue = requestedValue.coerceIn(0, maxScroll)
        thumbnailNode.top = (newValue / maxScroll.toFloat() * maxTop).ceilToInt()
        value = newValue
    }

    init {
        primaryPressedListeners += { mousePosition, _ ->
            mouseAtDragStart = mousePosition.y
            topAtDragStart = thumbnailNode.top
            true
        }

        mouseDragListeners += { mousePosition, _, _ ->
            val top = (topAtDragStart + mousePosition.y - mouseAtDragStart).coerceIn(0, maxTop)
            thumbnailNode.top = top
            value = (top / maxTop.toFloat() * (totalHeight - viewportHeight)).ceilToInt()
            true
        }
        applyStyle(Style.barStyle)
    }
}
