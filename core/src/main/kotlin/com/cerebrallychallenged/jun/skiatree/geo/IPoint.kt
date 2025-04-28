package com.cerebrallychallenged.jun.skiatree.geo

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemoryLayout.structLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.lang.foreign.ValueLayout.JAVA_INT

internal val IPointLayout = structLayout(
    sequenceLayout(2, JAVA_INT).withName("components")
)

internal fun Vec2i.Companion.isErrorSegment(memorySegment: MemorySegment): Boolean =
    IPointComponentHandle.get(memorySegment, 0) as Int == Int.MIN_VALUE

private val IPointComponentHandle = IPointLayout.varHandle(
    groupElement("components"),
    sequenceElement()
)

context(SegmentAllocator)
fun Vec2i.toSegment() = allocate(IPointLayout).also {
    IPointComponentHandle.set(it, 0, x)
    IPointComponentHandle.set(it, 1, y)
}

fun Vec2i.Companion.decodeFromSegment(segment: MemorySegment): Vec2i = vec(
    IPointComponentHandle.get(segment, 0) as Int,
    IPointComponentHandle.get(segment, 1) as Int
)
