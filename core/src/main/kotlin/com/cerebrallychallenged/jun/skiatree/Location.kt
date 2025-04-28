package com.cerebrallychallenged.jun.skiatree

import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemoryLayout.structLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.lang.foreign.ValueLayout.JAVA_INT

enum class Axis {
    Horizontal,
    Vertical
}

sealed class Location {
    companion object {
        @JvmStatic
        internal val Layout = structLayout(
            JAVA_INT.withName("tag"),
            JAVA_INT.withName("v0"),
            JAVA_INT.withName("v1")
        )

        @JvmStatic
        private val TagHandle = Layout.varHandle(groupElement("tag"))

        @JvmStatic
        private val V0Handle = Layout.varHandle(groupElement("v0"))

        @JvmStatic
        private val V1Handle = Layout.varHandle(groupElement("v1"))
    }

    data class LoHi(val lo: Int, val hi: Int) : Location() {
        override fun writeToSegment(segment: MemorySegment) {
            TagHandle.set(segment, 0)
            V0Handle.set(segment, lo)
            V1Handle.set(segment, hi)
        }
    }
    data class LoSize(val lo: Int, val size: Int) : Location() {
        override fun writeToSegment(segment: MemorySegment) {
            TagHandle.set(segment, 1)
            V0Handle.set(segment, lo)
            V1Handle.set(segment, size)
        }
    }
    data class HiSize(val hi: Int, val size: Int) : Location() {
        override fun writeToSegment(segment: MemorySegment) {
            TagHandle.set(segment, 2)
            V0Handle.set(segment, hi)
            V1Handle.set(segment, size)
        }
    }
    data class CenteredSize(val size: Int) : Location() {
        override fun writeToSegment(segment: MemorySegment) {
            TagHandle.set(segment, 3)
            V0Handle.set(segment, size)
        }
    }
    object Unspecified : Location() {
        override fun writeToSegment(segment: MemorySegment) {
            TagHandle.set(segment, 4)
        }
    }

    protected abstract fun writeToSegment(segment: MemorySegment)

    context(SegmentAllocator)
    internal fun toSegment(): MemorySegment = allocate(Layout).apply(::writeToSegment)
}
