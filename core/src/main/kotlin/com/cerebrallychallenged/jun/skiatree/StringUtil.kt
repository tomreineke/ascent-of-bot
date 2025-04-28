package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.stringFree
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator

context(SegmentAllocator)
fun String.toSegment(): MemorySegment = allocateUtf8String(this)

context(SegmentAllocator)
fun String?.toNullableSegment(): MemorySegment = this?.toSegment() ?: MemorySegment.NULL

fun MemorySegment.consumeFfiString(): String = getUtf8String(0).also {
    stringFree(this)
}

fun MemorySegment.consumeNullableFfiString(): String? =
    if (this == MemorySegment.NULL) null else consumeFfiString()
