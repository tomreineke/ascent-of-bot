package com.cerebrallychallenged.jun.skiatree.geo

import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.math.geo.vec
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemoryLayout.structLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.lang.foreign.ValueLayout.JAVA_INT

data class IRect(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    companion object {
        val Empty = IRect(0, 0, 0, 0)

        @JvmStatic
        internal val Layout = structLayout(
            sequenceLayout(4, JAVA_INT).withName("components")
        )

        @JvmStatic
        private val ComponentHandle = Layout.varHandle(
            groupElement("components"),
            sequenceElement()
        )

        fun decodeFromSegment(segment: MemorySegment): IRect = IRect(
            ComponentHandle.get(segment, 0) as Int,
            ComponentHandle.get(segment, 1) as Int,
            ComponentHandle.get(segment, 2) as Int,
            ComponentHandle.get(segment, 3) as Int,
        )

        internal fun isErrorSegment(memorySegment: MemorySegment): Boolean =
            ComponentHandle.get(memorySegment, 0) as Int == Int.MIN_VALUE
    }

    context(SegmentAllocator)
    fun toSegment(): MemorySegment {
        val segment = allocate(Layout)
        ComponentHandle.set(segment, 0, left)
        ComponentHandle.set(segment, 1, top)
        ComponentHandle.set(segment, 2, right)
        ComponentHandle.set(segment, 3, bottom)
        return segment
    }

    val leftTop = vec(left, top)
}

internal fun IRect.offset(other: IRect): IRect = IRect(
    left - other.left,
    top - other.top,
    right + other.right,
    bottom + other.bottom
)

fun IRect.scale(factor: Float): IRect = IRect(
    (left * factor).floorToInt(),
    (top * factor).floorToInt(),
    (right * factor).floorToInt(),
    (bottom * factor).floorToInt()
)
