package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.Node
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun Node.node(
    style: Styling<Node, Unit>? = null,
    f: Node.() -> Unit
): Node {
    contract {
        callsInPlace(f, InvocationKind.EXACTLY_ONCE)
    }
    return Node().also {
        if (style != null) {
            it.applyStyle(style)
        }
        children.add(it)
        it.f()
    }
}

fun Node.clearLayer() {
    visibility = Visibility.Visible
    children.clear()
    clearBackgrounds()
}

fun Node.clearBackgrounds() {
    background[InputState.Empty] = null
    background[InputState.Hovered] = null
    background[InputState.Pressed] = null
    background[InputState.Selected] = null
    background[InputState.Hovered + InputState.Pressed] = null
    background[InputState.Hovered + InputState.Selected] = null
    background[InputState.Pressed + InputState.Selected] = null
    background[InputState.Hovered + InputState.Pressed + InputState.Selected] = null
}
