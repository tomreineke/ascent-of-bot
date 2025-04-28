package com.cerebrallychallenged.jun.skiatree.node

import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.SkiaTreeWidget
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_LONG

sealed class NodeCore {
    companion object {
        @JvmStatic
        private val nodeSetCoreNull = function(
            "skiatree_node_set_core_null",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG
        )

        @JvmStatic
        private val nodeSetCoreParagraph = function(
            "skiatree_node_set_core_paragraph",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            ADDRESS
        )
    }

    data object Null : NodeCore() {
        override fun setFor(node: Node) {
            guardedUnit {
                nodeSetCoreNull(
                    libraryPointer,
                    node.resource.key
                ) as Byte
            }
        }
    }

    data class Paragraph(val paragraph: com.cerebrallychallenged.jun.skiatree.text.Paragraph) : NodeCore() {
        override fun setFor(node: Node) {
            guardedUnit {
                nodeSetCoreParagraph(
                    libraryPointer,
                    node.resource.key,
                    paragraph.address
                ) as Byte
            }
            for (child in paragraph.nodes) {
                child.parent = node
                child.widget = node.widget
            }
        }

        override fun setWidget(widget: SkiaTreeWidget?) {
            for (child in paragraph.nodes) {
                child.widget = widget
            }
        }
    }

    internal abstract fun setFor(node: Node)

    internal open fun setWidget(widget: SkiaTreeWidget?) {}
}
