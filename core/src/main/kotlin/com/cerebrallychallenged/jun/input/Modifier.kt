package com.cerebrallychallenged.jun.input

import java.util.*

enum class Modifier {
    SHIFT,
    LEFT_SHIFT,
    RIGHT_SHIFT,
    CONTROL,
    LEFT_CONTROL,
    RIGHT_CONTROL,
    ALT,
    LEFT_ALT,
    RIGHT_ALT,
    COMMAND,
    LEFT_COMMAND,
    RIGHT_COMMAND;

    companion object {
        fun fromMagic(magic: Int): EnumSet<Modifier> {
            val allModifiers = Modifier.values()
            val enumSet = EnumSet.noneOf(Modifier::class.java)
            return magic.bits(allModifiers.size).mapTo(enumSet) { allModifiers[it] }
        }
    }
}

fun EnumSet<Modifier>.toMagic(): Int {
    var magic = 0
    for (modifier in this) {
        magic = magic or (1 shl modifier.ordinal)
    }
    return magic
}

private fun Int.bits(maxBit: Int = 31): Sequence<Int> = sequence {
    var v = this@bits
    for (i in 0..maxBit) {
        if (v == 0) break
        if (v and 1 != 0) {
            yield(i)
        }
        v = v shr 1
    }
}
