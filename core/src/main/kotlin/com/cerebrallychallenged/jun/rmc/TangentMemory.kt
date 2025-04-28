package com.cerebrallychallenged.jun.rmc

import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.util.buffers.Memory
import com.cerebrallychallenged.jun.util.buffers.Memory.Layout
import com.cerebrallychallenged.jun.util.buffers.toPackedByte
import com.cerebrallychallenged.jun.util.buffers.toPackedShort
import com.cerebrallychallenged.jun.util.buffers.toUnpackedFloat
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_SHORT


object LayoutFPackedRGBA16N : Layout(
    sequenceLayout(2, sequenceLayout(4, JAVA_SHORT))
) {
    @JvmField
    internal val x = handle(sequenceElement(), sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(), sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(), sequenceElement(2))

    @JvmField
    internal val pad = handle(sequenceElement(), sequenceElement(3))

    class Channel(memory: Memory<LayoutFPackedRGBA16N>, private val channelIndex: Int) : Memory.View<Vec3f>(memory) {
        override fun get(index: Long): Vec3f = vec(
            (x.get(segment, index, channelIndex) as Short).toUnpackedFloat(),
            (y.get(segment, index, channelIndex) as Short).toUnpackedFloat(),
            (z.get(segment, index, channelIndex) as Short).toUnpackedFloat()
        )

        override fun set(index: Long, value: Vec3f) {
            x.set(segment, index, channelIndex, value.x.toPackedShort())
            y.set(segment, index, channelIndex, value.y.toPackedShort())
            z.set(segment, index, channelIndex, value.z.toPackedShort())
            pad.set(segment, index, channelIndex, Short.MAX_VALUE)
        }
    }
}

val Memory<LayoutFPackedRGBA16N>.tangent: LayoutFPackedRGBA16N.Channel
    get() = LayoutFPackedRGBA16N.Channel(this, 0)

val Memory<LayoutFPackedRGBA16N>.normal: LayoutFPackedRGBA16N.Channel
    get() = LayoutFPackedRGBA16N.Channel(this, 1)


object LayoutFPackedNormal : Layout(
    sequenceLayout(2, sequenceLayout(4, JAVA_BYTE))
) {
    @JvmField
    internal val x = handle(sequenceElement(), sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(), sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(), sequenceElement(2))

    @JvmField
    internal val pad = handle(sequenceElement(), sequenceElement(3))

    class Channel(memory: Memory<LayoutFPackedNormal>, private val channelIndex: Int) : Memory.View<Vec3f>(memory) {
        override fun get(index: Long): Vec3f = vec(
            (x.get(segment, index, channelIndex) as Byte).toUnpackedFloat(),
            (y.get(segment, index, channelIndex) as Byte).toUnpackedFloat(),
            (z.get(segment, index, channelIndex) as Byte).toUnpackedFloat()
        )

        override fun set(index: Long, value: Vec3f) {
            x.set(segment, index, channelIndex, value.x.toPackedByte())
            y.set(segment, index, channelIndex, value.y.toPackedByte())
            z.set(segment, index, channelIndex, value.z.toPackedByte())
            pad.set(segment, index, channelIndex, Byte.MAX_VALUE)
        }
    }
}

val Memory<LayoutFPackedNormal>.tangent: LayoutFPackedNormal.Channel
    get() = LayoutFPackedNormal.Channel(this, 0)

val Memory<LayoutFPackedNormal>.normal: LayoutFPackedNormal.Channel
    get() = LayoutFPackedNormal.Channel(this, 1)
