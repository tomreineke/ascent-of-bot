package com.cerebrallychallenged.jun.apibuilder

import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator


context(SegmentAllocator)
private fun MemorySegment.copyToNative(): MemorySegment = allocate(byteSize()).also {
    it.copyFrom(this)
}

context(SegmentAllocator)
fun FloatArray.toSegment(): MemorySegment = MemorySegment.ofArray(this).copyToNative()

context(SegmentAllocator)
fun FloatArray?.toNullableSegment(): MemorySegment = this?.toSegment() ?: MemorySegment.NULL
