package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.node.Node
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class HBox(isWrapping: Boolean = false) : Node() {
    init {
        flow = if (isWrapping) Flow.LeftToRightThenTopToBottom else Flow.LeftToRight
    }
}

inline fun Node.hBox(
    isWrapping: Boolean = false,
    style: Styling<HBox, Unit>? = null,
    f: HBox.() -> Unit
): HBox {
    contract {
        callsInPlace(f, InvocationKind.EXACTLY_ONCE)
    }
    return HBox(isWrapping).also {
        if (style != null) {
            it.applyStyle(style)
        }
        children.add(it)
        it.f()
    }
}
