package com.cerebrallychallenged.jun.rmc

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.util.HalfFloat
import com.cerebrallychallenged.jun.util.buffers.Memory
import com.cerebrallychallenged.jun.util.toHalfFloat
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.ValueLayout
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_SHORT

abstract class LayoutTexCoord(channelCount: Int, carrier: ValueLayout) : Memory.Layout(
    sequenceLayout(channelCount.toLong(), sequenceLayout(2, carrier))
)

sealed class LayoutTexCoordF32(val channelCount: Int) : LayoutTexCoord(channelCount, JAVA_FLOAT) {
    private val x = handle(sequenceElement(), sequenceElement(0))

    private val y = handle(sequenceElement(), sequenceElement(1))

    inner class Channel(
        memory: Memory<out LayoutTexCoordF32>,
        private val channelIndex: Int
    ) : Memory.View<Vec2f>(memory) {
        init {
            require(channelIndex in 0 until channelCount)
        }

        override fun get(index: Long): Vec2f = vec(
            x.get(segment, index, channelIndex) as Float,
            y.get(segment, index, channelIndex) as Float
        )

        override fun set(index: Long, value: Vec2f) {
            x.set(segment, index, channelIndex, value.x)
            y.set(segment, index, channelIndex, value.y)
        }
    }
}

data object LayoutTexCoord1f32 : LayoutTexCoordF32(1)
data object LayoutTexCoord2f32 : LayoutTexCoordF32(2)
data object LayoutTexCoord3f32 : LayoutTexCoordF32(3)
data object LayoutTexCoord4f32 : LayoutTexCoordF32(4)

val LayoutTexCoordF32List = listOf(
    LayoutTexCoord1f32,
    LayoutTexCoord2f32,
    LayoutTexCoord3f32,
    LayoutTexCoord4f32
)

fun <L : LayoutTexCoordF32> Memory<L>.channel(channelIndex: Int): LayoutTexCoordF32.Channel {
    val channelCount = layout.channelCount
    require(channelIndex < channelCount)
    return LayoutTexCoordF32List[channelCount - 1].Channel(this, channelIndex)
}

sealed class LayoutTexCoordF16(val channelCount: Int) : LayoutTexCoord(channelCount, JAVA_SHORT) {
    private val x = handle(sequenceElement(), sequenceElement(0))

    private val y = handle(sequenceElement(), sequenceElement(1))


    inner class Channel(
        memory: Memory<out LayoutTexCoordF16>,
        private val channelIndex: Int
    ) : Memory.View<Vec2f>(memory) {
        init {
            require(channelIndex in 0 until channelCount)
        }

        override fun get(index: Long): Vec2f = vec(
            HalfFloat((x.get(segment, index, channelIndex) as Short).toUShort()).toFloat(),
            HalfFloat((y.get(segment, index, channelIndex) as Short).toUShort()).toFloat()
        )

        override fun set(index: Long, value: Vec2f) {
            x.set(segment, index, channelIndex, value.x.toHalfFloat().bits.toShort())
            y.set(segment, index, channelIndex, value.y.toHalfFloat().bits.toShort())
        }
    }
}

object LayoutTexCoord1f16 : LayoutTexCoordF16(1)
object LayoutTexCoord2f16 : LayoutTexCoordF16(2)
object LayoutTexCoord3f16 : LayoutTexCoordF16(3)
object LayoutTexCoord4f16 : LayoutTexCoordF16(4)

val LayoutTexCoordF16List = listOf(
    LayoutTexCoord1f16,
    LayoutTexCoord2f16,
    LayoutTexCoord3f16,
    LayoutTexCoord4f16
)

fun <L : LayoutTexCoordF16> Memory<L>.channel(channelIndex: Int): LayoutTexCoordF16.Channel {
    val channelCount = layout.channelCount
    require(channelIndex < channelCount)
    return LayoutTexCoordF16List[channelCount - 1].Channel(this, channelIndex)
}
