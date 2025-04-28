package com.cerebrallychallenged.hypogean.linguistics

fun Int.signedString(usePlusSign: Boolean = false): String = when {
    this < 0 -> "\u2212${-this}"
    this > 0 -> if (usePlusSign) "+$this" else "$this"
    else -> "0"
}
