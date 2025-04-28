package com.cerebrallychallenged.jun.util

import java.nio.ByteBuffer

@JvmInline
value class HalfFloat(val bits: UShort) {
    fun toFloat(): Float {
        val intBits = bits.toUInt()
        val signBits = (intBits and 0b1000000000000000u) shl 16
        val exponent = (intBits and 0b0111110000000000u) shr 10
        val mantissa = intBits and 0b0000001111111111u
        return Float.fromBits((signBits or when (exponent) {
            0u -> {
                if (mantissa == 0u) {
                    0u
                } else {
                    return nativeHalfFloatToFloat(bits)
                }
            }
            31u -> 0x477FE000u
            else -> ((exponent + 112u) shl 23) or (mantissa shl 13)
        }).toInt())
    }
}

fun Float.toHalfFloat(): HalfFloat {
    val bits = toRawBits().toUInt()
    val signBits = (bits and 0x80000000u) shr 16
    val exponent = (bits and 0x7F800000u) shr 23
    val mantissa = bits and 0x007FFFFFu
    return HalfFloat((signBits or when {
        exponent <= 112u -> {
            val newExp = exponent.toInt() - 112
            if (newExp >= -10) {
                (((mantissa or 0x800000u) shr (13 - newExp)) + 1u) shr 1
            } else {
                0u
            }
        }
        exponent >= 143u -> 0x7BFFu
        else -> ((exponent - 112u) shl 10) or (mantissa shr 13)
    }).toUShort())
}

fun ByteBuffer.putHalfFloat(index: Int, value: HalfFloat) {
    putShort(index, value.bits.toShort())
}

fun ByteBuffer.getHalfFloat(index: Int): HalfFloat = HalfFloat(getShort(index).toUShort())

private external fun nativeHalfFloatToFloat(bits: UShort): Float

private external fun nativeFloatToHalfFloat(value: Float): UShort
