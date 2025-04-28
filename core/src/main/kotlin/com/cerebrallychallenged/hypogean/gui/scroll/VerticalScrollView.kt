package com.cerebrallychallenged.hypogean.gui.scroll

import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.node.Node
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class VerticalScrollView : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        const val DefaultHGap = 20

        var gap = DefaultHGap

        val DefaultStyle = Styling<VerticalScrollView, Unit> {
            flow = Flow.LeftToRight
            hgap = gap.scaled
        }

        var style: Styling<VerticalScrollView, Unit> = DefaultStyle
    }

    var scrollsLastElementUpToTop = false

    var autoScroll = true

    val content = Node().apply {
        this@VerticalScrollView.children.add(this)
        flow = Flow.Vertical
        isScrollViewport = true
        maxHeight = 300
        minHeight = 300
        resizeListeners += {
            bar.viewportHeight = size.y
            bar.totalHeight = if (scrollsLastElementUpToTop) {
                contentSize.y + (size.y - (children.lastOrNull()?.size?.y ?: 0)).coerceAtLeast(0)
            } else {
                contentSize.y
            }
            if (autoScroll) {
                bar.scrollTo(Int.MAX_VALUE)
            }
        }
    }

    private val bar: VerticalScrollBar = VerticalScrollBar().apply {
        this@VerticalScrollView.children.add(this)
        valueListeners += { content.layoutTranslation = vec(0, -it) }
    }

    fun add(node: Node) {
        content.children.add(node)
        if (autoScroll) {
            bar.scrollTo(Int.MAX_VALUE)
        }
    }

    fun clear() {
        content.children.clear()
    }

    init {
        bar.viewportHeight = content.size.y
        bar.totalHeight = content.contentSize.y
        mouseWheelListeners += { _, delta, _ ->
            bar.scrollTo(bar.value - (delta * 50.0f).toInt())
            true
        }
        applyStyle(Style.style)
    }
}

inline fun Node.verticalScrollView(
    style: Styling<VerticalScrollView, Unit>? = null,
    maxContentHeight: Int? = null,
    f: Node.() -> Unit
): VerticalScrollView {
    contract {
        callsInPlace(f, InvocationKind.EXACTLY_ONCE)
    }
    return VerticalScrollView().also {
        if (style != null) {
            it.applyStyle(style)
        }
        if (maxContentHeight != null) {
            maxHeight = maxContentHeight.scaled.coerceAtLeast(minHeight)
        }
        children.add(it)
        f(it.content)
    }
}
