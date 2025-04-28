package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.hypogean.gui.GuiConfig.guiScale
import com.cerebrallychallenged.hypogean.view.util.CommonGuiImages
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.layout.Margin
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.Node
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Window(hasCloseButton: Boolean) : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DefaultWindowStyle = Styling<Window, Unit> {
            background[InputState.Empty] = ResourceLibrary[CommonGuiImages.ViewFrameNinePatch, guiScale * 0.5f]
            horizontalAlign = Align.Center
            verticalAlign = Align.Center
            maxWidth = 800
//            maxHeight = widget?.let { widget -> widget.size.y - 200 } ?: 500
        }

        var windowStyle: Styling<Window, Unit> = DefaultWindowStyle

        val DefaultContentStyle = Styling<Node, Unit> {
            debugName = "contentNode"
            verticalAlign = Align.Stretch
            margin = Margin.all(104.scaled)
            flow = Flow.Vertical
            vgap = 40.scaled
        }

        var contentStyle: Styling<Node, Unit> = DefaultContentStyle
    }

    init {
        if (hasCloseButton) {
            CloseButton().apply {
                this@Window.children.add(this)
                horizontalAlign = Align.Max
                verticalAlign = Align.Min
                actionListener = {
                    closeListener?.invoke()
                    this@Window.visibility = Visibility.Hidden
                }
            }
        }
    }

    val contentNode = Node().apply {
        this@Window.children.add(this)
        applyStyle(Style.contentStyle)
    }

    var closeListener: (() -> Unit)? = null

    init {
        applyStyle(Style.windowStyle)
    }
}

inline fun Node.window(
    style: Styling<Window, Unit>? = null,
    hasCloseButton: Boolean = false,
    f: Node.() -> Unit
): Window {
    contract {
        callsInPlace(f, InvocationKind.EXACTLY_ONCE)
    }
    return Window(hasCloseButton).also {
        if (style != null) {
            it.applyStyle(style)
        }
        children.add(it)
        it.contentNode.f()
    }
}
