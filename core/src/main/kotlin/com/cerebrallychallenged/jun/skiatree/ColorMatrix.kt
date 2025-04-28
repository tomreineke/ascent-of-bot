package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import java.lang.foreign.Arena
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_FLOAT


class ColorMatrix private constructor(internal val segment: MemorySegment) {
    companion object {
        private val Layout = sequenceLayout(4, sequenceLayout(5, JAVA_FLOAT))

        @JvmStatic
        private val ElementHandle = Layout.varHandle(
            sequenceElement(),
            sequenceElement()
        )

        operator fun invoke(): ColorMatrix = ColorMatrix(Arena.ofAuto().allocate(Layout)).also {
            it[0, 0] = 1.0f
            it[1, 1] = 1.0f
            it[2, 2] = 1.0f
            it[3, 3] = 1.0f
        }

        fun color(value: FLinearColor): ColorMatrix = ColorMatrix().also {
            it[0, 0] = value.r
            it[1, 1] = value.g
            it[2, 2] = value.b
        }

        fun brightness(value: Float): ColorMatrix = ColorMatrix().also {
            it[0, 0] = value
            it[1, 1] = value
            it[2, 2] = value
        }

        fun grayscale(value: Float): ColorMatrix = ColorMatrix().also {
            val v = value / 3.0f
            for (row in 0..2) {
                for (col in 0..2) {
                    it[row, col] = v
                }
            }
        }
    }

    operator fun set(row: Int, col: Int, value: Float) {
        ElementHandle.set(segment, row, col, value)
    }

    operator fun get(row: Int, col: Int): Float = ElementHandle.get(segment, row, col) as Float
}
