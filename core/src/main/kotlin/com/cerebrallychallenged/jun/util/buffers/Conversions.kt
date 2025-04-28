package com.cerebrallychallenged.jun.util.buffers

import com.cerebrallychallenged.jun.math.clamp
import kotlin.math.roundToInt

fun Float.toPackedByte(): Byte =
        clamp(
                (this * Byte.MAX_VALUE).roundToInt(),
                Byte.MIN_VALUE.toInt(),
                Byte.MAX_VALUE.toInt()
        ).toByte()

fun Byte.toUnpackedFloat(): Float = toFloat() / Byte.MAX_VALUE

fun Float.toPackedShort(): Short =
        clamp(
                (this * Short.MAX_VALUE).roundToInt(),
                Short.MIN_VALUE.toInt(),
                Short.MAX_VALUE.toInt()
        ).toShort()

fun Short.toUnpackedFloat(): Float = toFloat() / Short.MAX_VALUE
