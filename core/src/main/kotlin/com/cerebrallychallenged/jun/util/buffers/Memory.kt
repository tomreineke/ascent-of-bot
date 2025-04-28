package com.cerebrallychallenged.jun.util.buffers

import com.cerebrallychallenged.jun.util.buffers.Memory.Layout
import java.lang.foreign.Arena
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.lang.foreign.SequenceLayout
import java.lang.invoke.VarHandle

// Type parameter `L` declares the layout of the wrapped `MemorySegment`.
class Memory<L : Layout>(val segment: MemorySegment, val layout: L) {
    abstract class Layout(val elementLayout: MemoryLayout) {
        @Suppress("LeakingThis")
        internal val empty = Memory(Arena.global().allocate(1), this)

        val sequenceLayout: SequenceLayout = sequenceLayout(elementLayout)

        protected fun handle(vararg pathElements: PathElement): VarHandle =
            sequenceLayout.varHandle(
                PathElement.sequenceElement(),
                *pathElements
            )
    }

    abstract class View<T>(val memory: Memory<*>) : Indexed<T> {
        @JvmField
        protected val segment = memory.segment

        override val size: Long = memory.size
    }

    inline val baseAddress: Long
        get() = segment.address()

    fun copyFrom(other: Memory<L>) {
        val otherSegment = other.segment
        require(segment.byteSize() == otherSegment.byteSize())
        segment.copyFrom(otherSegment)
    }

    val size: Long
        get() {
            val elementSize = layout.elementLayout.byteSize()
            val segmentSize = segment.byteSize()
            return if (elementSize == 0L || segmentSize <= 1) 0 else segmentSize / elementSize
        }
}

fun <L : Layout> MemorySegment.withLayout(layout: L): Memory<L> = Memory(this, layout)

context(SegmentAllocator)
fun <L : Layout> L.allocate(elementCount: Long): Memory<L> =
    this@SegmentAllocator.allocate(sequenceLayout.withElementCount(elementCount)).withLayout(this)

context(SegmentAllocator)
fun <L : Layout> L.allocate(elementCount: Int): Memory<L> = allocate(elementCount.toLong())

@Suppress("UNCHECKED_CAST")
fun <L : Layout> L.emptyMemory(): Memory<L> = empty as Memory<L>

fun <S, T> Memory.View<S>.derive(sToT: (S) -> T, tToS: (T) -> S): Memory.View<T> = object : Memory.View<T>(memory) {
    override fun get(index: Long): T = sToT(this@derive[index])

    override fun set(index: Long, value: T) {
        this@derive[index] = tToS(value)
    }
}

operator fun <T> Memory.View<T>.get(index: Int): T = get(index.toLong())

operator fun <T> Memory.View<T>.set(index: Int, value: T) = set(index.toLong(), value)

context(Arena)
fun <T, L : Layout> Collection<T>.toMemory(layout: L, viewFn: (Memory<L>) -> Memory.View<T>): Memory<L> =
    layout.allocate(size).also {
        val view = viewFn(it)
        for ((i, value) in withIndex()) {
            view[i] = value
        }
    }

context(SegmentAllocator)
inline fun <T, U, L : Layout> Collection<T>.toMemory(
    layout: L,
    setter: (Memory<L>, Int, U) -> Unit,
    transform: (T) -> U
): Memory<L> = layout.allocate(size).also {
    for ((i, value) in withIndex()) {
        setter(it, i, transform(value))
    }
}

context(SegmentAllocator)
inline fun <T, L : Layout> Collection<T>.toMemory(
    layout: L,
    setter: (Memory<L>, Int, T) -> Unit,
): Memory<L> = toMemory(layout, setter) { it }
