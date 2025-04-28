package com.cerebrallychallenged.jun.skiatree.geo

import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemoryLayout.structLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.lang.foreign.ValueLayout.JAVA_FLOAT

data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float) {
    companion object {
        @JvmStatic
        internal val Layout = structLayout(
            sequenceLayout(4, JAVA_FLOAT).withName("components")
        )

        @JvmStatic
        private val ComponentHandle = Layout.varHandle(
            groupElement("components"),
            sequenceElement()
        )
    }

    context(SegmentAllocator)
    fun toSegment(): MemorySegment = allocate(Layout).also {
        ComponentHandle.set(it, 0, left)
        ComponentHandle.set(it, 1, top)
        ComponentHandle.set(it, 2, right)
        ComponentHandle.set(it, 3, bottom)
    }
}
