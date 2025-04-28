package com.cerebrallychallenged.hypogean.util

fun Int.appendBits(other: Int): Long = ((toULong() shl 32) or (other.toUInt().toULong())).toLong()
private const val HI_MASK: ULong = 0b1111111111111111111111111111111100000000000000000000000000000000UL
private const val LO_MASK: ULong = 0b0000000000000000000000000000000011111111111111111111111111111111UL

fun Long.separateBits(): Pair<Int, Int> = Pair(
        ((toULong() and HI_MASK) shr 32).toUInt().toInt(),
        (toULong() and LO_MASK).toUInt().toInt()
)