package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemoryLayout.structLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.lang.foreign.ValueLayout.JAVA_FLOAT

internal val Color4fLayout = structLayout(
    sequenceLayout(4, JAVA_FLOAT).withName("components")
)

private val Color4fComponentHandle = Color4fLayout.varHandle(
    groupElement("components"),
    sequenceElement()
)

context(SegmentAllocator)
fun FLinearColor.toSegment(): MemorySegment = allocate(Color4fLayout).also {
    Color4fComponentHandle.set(it, 0, r)
    Color4fComponentHandle.set(it, 1, g)
    Color4fComponentHandle.set(it, 2, b)
    Color4fComponentHandle.set(it, 3, a)
}

private val Color4fArrayLayout = sequenceLayout(sequenceLayout(4, JAVA_FLOAT))

private val Color4fArrayComponentHandle = Color4fArrayLayout.varHandle(
    sequenceElement(),
    sequenceElement()
)

context(SegmentAllocator)
fun Array<FLinearColor>.toSegment(): MemorySegment = allocate(Color4fArrayLayout.withElementCount(size.toLong())).also {
    for ((i, color) in withIndex()) {
        Color4fArrayComponentHandle.set(it, i, 0, color.r)
        Color4fArrayComponentHandle.set(it, i, 1, color.g)
        Color4fArrayComponentHandle.set(it, i, 2, color.b)
        Color4fArrayComponentHandle.set(it, i, 3, color.a)
    }
}
