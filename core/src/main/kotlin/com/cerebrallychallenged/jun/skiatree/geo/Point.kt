package com.cerebrallychallenged.jun.skiatree.geo

import com.cerebrallychallenged.jun.math.geo.Vec2f
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemoryLayout.structLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.lang.foreign.ValueLayout.JAVA_FLOAT

internal val PointLayout = structLayout(
    sequenceLayout(2, JAVA_FLOAT).withName("components")
)

private val PointComponentHandle = PointLayout.varHandle(
    groupElement("components"),
    sequenceElement()
)

context(SegmentAllocator)
fun Vec2f.toSegment(): MemorySegment = allocate(PointLayout).also {
    PointComponentHandle.set(it, 0, x)
    PointComponentHandle.set(it, 1, y)
}
