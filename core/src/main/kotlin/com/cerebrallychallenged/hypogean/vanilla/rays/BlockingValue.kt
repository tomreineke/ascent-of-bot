package com.cerebrallychallenged.hypogean.vanilla.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodec
import com.cerebrallychallenged.hypogean.rays.RayOrientation
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue.Companion.ParameterCombinationCount
import java.io.DataInput
import java.io.DataOutput

class BlockingValue(internal val array: FloatArray) {
    init {
        require(array.size == ParameterCombinationCount)
    }

    data class Parameter(val orientation: RayOrientation?, val heading: Heading?)

    companion object {
        private val OrientationCount = RayOrientation.values().size

        private val HeadingCount = Heading.values().size

        internal val ParameterCombinationCount = (OrientationCount + 1) * (HeadingCount + 1)

        private fun Parameter.index(): Int {
            val orientationIndex = orientation?.ordinal ?: OrientationCount
            val headingIndex = heading?.ordinal ?: HeadingCount
            return orientationIndex * HeadingCount + headingIndex
        }

        private val ParameterCombinations = buildList {
            for (orientation in listOf(RayOrientation.Inbound, RayOrientation.Outbound, null)) {
                for (heading in Heading.values()) {
                    add(Parameter(orientation, heading))
                }
                add(Parameter(orientation, null))
            }
        }

        operator fun invoke(
                f: Parameter.() -> Float
        ): BlockingValue = BlockingValue(FloatArray(ParameterCombinationCount) { index ->
            ParameterCombinations[index].f()
        })
    }

    operator fun invoke(orientation: RayOrientation? = null, heading: Heading? = null): Float =
            array[Parameter(orientation, heading).index()]
}

private fun DataInput.readBlockingValue(): BlockingValue = BlockingValue(FloatArray(ParameterCombinationCount) {
    readFloat()
})

private fun DataOutput.writeBlockingValue(blockingValue: BlockingValue) {
    for (value in blockingValue.array) {
        writeFloat(value)
    }
}

object BlockingValueCodec : AttributeCodec<BlockingValue>
