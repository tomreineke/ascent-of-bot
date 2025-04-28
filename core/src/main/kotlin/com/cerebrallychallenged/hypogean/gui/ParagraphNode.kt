package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.hypogean.gui.GuiConfig.guiScale
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.skiatree.node.NodeCore
import com.cerebrallychallenged.jun.skiatree.text.Paragraph
import com.cerebrallychallenged.jun.skiatree.text.ParagraphBuilderContext
import com.cerebrallychallenged.jun.skiatree.text.TextStyle
import com.cerebrallychallenged.jun.skiatree.text.buildParagraph
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class ParagraphNode(
    textStyle: TextStyle? = null,
    block: (ParagraphBuilderContext.() -> Unit)? = null
) : Node() {
    constructor(
        textStyle: TextStyleResource,
        block: ParagraphBuilderContext.() -> Unit
    ) : this(ResourceLibrary[textStyle, guiScale], block)

    var paragraph: Paragraph? = null
        set(value) {
            field = value
            core = if (value != null) NodeCore.Paragraph(value) else NodeCore.Null
        }

    init {
        if (block != null) {
            paragraph = buildParagraph(textStyle = textStyle, block = block)
        }
    }
}

fun Node.paragraphNode(
    textStyle: TextStyleResource? = null,
    style: Styling<ParagraphNode, Unit>? = null,
    block: ParagraphBuilderContext.() -> Unit
): ParagraphNode {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return ParagraphNode(textStyle?.let { ResourceLibrary[it, guiScale] }, block).also {
        if (style != null) {
            it.applyStyle(style)
        }
        children.add(it)
    }
}
