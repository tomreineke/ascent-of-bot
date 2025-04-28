package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.curve.Polyline
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.util.buffers.LayoutVec2i64
import com.cerebrallychallenged.jun.util.buffers.Memory
import com.cerebrallychallenged.jun.util.buffers.set
import com.cerebrallychallenged.jun.util.buffers.toMemory
import com.cerebrallychallenged.jun.util.buffers.withLayout
import com.cerebrallychallenged.jun.wrapSharedRef
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.nio.ByteBuffer

interface Path {
    companion object {
        fun makeShared(count: Int = 0): TSharedRef<Path> = makeSharedOfPath(count).wrapSharedRef()

        fun makeShared(buffer: Memory<LayoutVec2i64>): TSharedRef<Path> =
                makeSharedOfPath(buffer.segment.asByteBuffer()).wrapSharedRef()
    }
}

context(SegmentAllocator)
@Convenience
fun Polyline<Vec2f>.toPath(scale: Float): TSharedRef<Path> =
        Path.makeShared(vertices.toMemory(LayoutVec2i64, Memory<LayoutVec2i64>::set) { (it * scale).round() })

@Convenience
fun AnyRef<Path>.toBuffer(): Memory<LayoutVec2i64> =
    MemorySegment.ofBuffer(toBuffer(directPtr)).withLayout(LayoutVec2i64)

private external fun makeSharedOfPath(count: Int): CPointer

private external fun makeSharedOfPath(buffer: ByteBuffer): CPointer

private external fun toBuffer(directPtr: CPointer): ByteBuffer
