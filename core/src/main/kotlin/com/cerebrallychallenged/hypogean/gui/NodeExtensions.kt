package com.cerebrallychallenged.hypogean.gui

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
