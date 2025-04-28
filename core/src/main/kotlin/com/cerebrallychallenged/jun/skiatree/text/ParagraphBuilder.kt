package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiConfig.DefaultTextStyle
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.TextStyleResource
import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.CloseableResourceFactory
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.VOID
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.skiatree.node.NodeCore
import com.cerebrallychallenged.jun.skiatree.toSegment
import com.cerebrallychallenged.jun.util.confinedArena
import it.unimi.dsi.fastutil.Stack
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG

class ParagraphBuilder private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<ParagraphBuilder>(::ParagraphBuilder, "skiatree_paragraph_builder_delete") {
        @JvmStatic
        private val paragraphBuilderNew = function(
            "skiatree_paragraph_builder_new",
            ADDRESS,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val paragraphBuilderPushStyle = function(
            "skiatree_paragraph_builder_push_style",
            VOID,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val paragraphBuilderPop = function(
            "skiatree_paragraph_builder_pop",
            VOID,
            ADDRESS
        )

        @JvmStatic
        private val paragraphBuilderAddText = function(
            "skiatree_paragraph_builder_add_text",
            VOID,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val paragraphBuilderAddPlaceholder = function(
            "skiatree_paragraph_builder_add_placeholder",
            JAVA_BYTE,
            ADDRESS,
            ADDRESS,
            JAVA_LONG,
            JAVA_INT,
            JAVA_INT,
            JAVA_FLOAT
        )

        @JvmStatic
        private val paragraphBuilderBuild = function(
            "skiatree_paragraph_builder_build",
            ADDRESS,
            ADDRESS
        )

        operator fun invoke(
            paragraphStyle: ParagraphStyle,
            fontCollection: FontCollection = GlobalFontRegistry.fontCollection
        ): ParagraphBuilder = ParagraphBuilder {
            paragraphBuilderNew(
                paragraphStyle.address,
                fontCollection.address
            ) as MemorySegment
        }
    }

    private val nodes: MutableList<Node> = mutableListOf()

    private val styleStack: Stack<TextStyle> = ObjectArrayList()

    var isAtParagraphStart: Boolean = true
        private set

    var isAtLineStart: Boolean = true
        private set

    fun pushStyle(style: TextStyle) {
        styleStack.push(style)
        paragraphBuilderPushStyle(address, style.address)
    }

    fun pop() {
        styleStack.pop()
        paragraphBuilderPop(address)
    }

    val currentStyle: TextStyle?
        get() = if (styleStack.isEmpty) null else styleStack.top()

    fun addText(text: String) {
        isAtParagraphStart = false
        if (text.isNotEmpty()) {
            isAtLineStart = text.last() == '\n'
        }
        confinedArena {
            paragraphBuilderAddText(address, text.toSegment())
        }
    }

    fun addPlaceholder(
        node: Node,
        placeholderAlignment: PlaceholderAlignment = PlaceholderAlignment.Baseline,
        baseline: TextBaseline = TextBaseline.Alphabetic,
        offset: Float = 0.0f
    ) {
        isAtParagraphStart = false
        nodes.add(node)
        guardedUnit {
            paragraphBuilderAddPlaceholder(
                libraryPointer,
                address,
                node.resource.key,
                placeholderAlignment.ordinal,
                baseline.ordinal,
                offset
            ) as Byte
        }
    }

    fun build(): Paragraph = Paragraph { paragraphBuilderBuild(address) as MemorySegment }.also { it.nodes = nodes }
}

@JvmInline
value class ParagraphBuilderContext(private val builder: ParagraphBuilder) {
    companion object {
        val DefaultStyle = ParagraphStyle()
    }

    val currentStyle: TextStyle?
        get() = builder.currentStyle

    val isAtParagraphStart: Boolean
        get() = builder.isAtParagraphStart

    val isAtLineStart: Boolean
        get() = builder.isAtLineStart

    operator fun String.unaryPlus() {
        builder.addText(this)
    }

    fun deriveStyle(modify: TextStyle.() -> Unit): TextStyle = (currentStyle?.clone() ?: TextStyle()).apply(modify)

    fun withStyle(textStyle: TextStyle, f: ParagraphBuilderContext.() -> Unit) {
        try {
            builder.pushStyle(textStyle)
            @Suppress("UNUSED_EXPRESSION") // False positive due to compiler bug
            f()
        } finally {
            builder.pop()
        }
    }

    fun embed(
        node: Node,
        placeholderAlignment: PlaceholderAlignment = PlaceholderAlignment.Top,
        baseline: TextBaseline = TextBaseline.Alphabetic,
        offset: Float = 0.0f
    ) {
        builder.addPlaceholder(node, placeholderAlignment, baseline, offset)
    }

    fun link(
        textStyleResource: TextStyleResource? = DefaultTextStyle,
        placeholderAlignment: PlaceholderAlignment = PlaceholderAlignment.Top,
        baseline: TextBaseline = TextBaseline.Alphabetic,
        offset: Float = 0.0f,
        f: ParagraphBuilderContext.() -> Unit
    ): Node = Node().apply {
        core = NodeCore.Paragraph(buildParagraph {
            if (textStyleResource != null) {
                withStyle(ResourceLibrary[textStyleResource, GuiConfig.guiScale]) {
                    f()
                }
            } else {
                f()
            }
        })
        embed(this, placeholderAlignment, baseline, offset)
    }

    fun newLine(onlyIfNotAtStartOfLine: Boolean = true) {
        if (!onlyIfNotAtStartOfLine || !isAtLineStart) {
            +"\n"
        }
    }
}

fun buildParagraph(
    paragraphStyle: ParagraphStyle = ParagraphBuilderContext.DefaultStyle,
    textStyle: TextStyle? = null,
    block: ParagraphBuilderContext.() -> Unit
): Paragraph {
    val builder = ParagraphBuilder(paragraphStyle)
    with (ParagraphBuilderContext(builder)) {
        if (textStyle != null) {
            withStyle(textStyle, block)
        } else {
            block()
        }
    }
    return builder.build()
}
