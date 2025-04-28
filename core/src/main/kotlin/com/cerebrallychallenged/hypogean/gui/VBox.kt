package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.node.Node
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class VBox : Node() {
    init {
        flow = Flow.Vertical
    }
}

inline fun Node.vBox(
    style: Styling<VBox, Unit>? = null,
    f: VBox.() -> Unit
): VBox {
    contract {
        callsInPlace(f, InvocationKind.EXACTLY_ONCE)
    }
    return VBox().also {
        if (style != null) {
            it.applyStyle(style)
        }
        children.add(it)
        it.f()
    }
}
