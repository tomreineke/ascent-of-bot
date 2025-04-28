package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_LONG

class Layers private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<Layers>(::Layers, "skiatree_forest_delete") {
        @JvmStatic
        private val forestNew = function(
            "skiatree_forest_new",
            ADDRESS
        )

        @JvmStatic
        private val forestSetLayer = function(
            "skiatree_forest_set_layer",
            JAVA_BYTE,
            ADDRESS,
            ADDRESS,
            JAVA_LONG,
            JAVA_LONG
        )

        @JvmStatic
        private val forestDrawOnSurface = function(
            "skiatree_forest_draw_on_surface",
            JAVA_BYTE,
            ADDRESS,
            ADDRESS,
            ADDRESS
        )

        operator fun invoke(widget: SkiaTreeWidget): Layers =
            Layers { forestNew() as MemorySegment }.also { it.widget = widget }
    }

    lateinit var widget: SkiaTreeWidget
        private set

    private val layers: MutableList<Node> = mutableListOf()

    private fun createNode(index: Int): Node = Node().also {
        it.widget = widget
        guardedUnit {
            forestSetLayer(
                libraryPointer,
                address,
                index.toLong(),
                it.resource.key
            ) as Byte
        }
        it.debugName = "Root$index"
        it.horizontalAlign = Align.Stretch
        it.verticalAlign = Align.Stretch
        it.consumesHover = false
    }

    operator fun get(index: Int): Node {
        for (i in layers.size..index) {
            layers.add(createNode(i))
        }
        return layers[index]
    }

    fun drawOnSurface(surface: Surface) {
        guardedUnit {
            forestDrawOnSurface(
                libraryPointer,
                address,
                surface.address
            ) as Byte
        }
    }
}
