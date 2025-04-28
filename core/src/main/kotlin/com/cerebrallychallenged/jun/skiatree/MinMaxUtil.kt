@file:Suppress("NOTHING_TO_INLINE")

package com.cerebrallychallenged.jun.skiatree

import kotlin.math.max
import kotlin.math.min

inline fun minNullable(a: Int, b: Int?): Int = if (b != null) min(a, b) else a
inline fun minNullable(a: Int?, b: Int): Int = minNullable(b, a)
inline fun minNullable(a: Int?, b: Int?): Int? = if (a != null) minNullable(a, b) else b
inline fun minNullable(a: Int, b: Int?, c: Int?): Int = minNullable(minNullable(a, b), c)

inline fun maxNullable(a: Int, b: Int?): Int = if (b != null) max(a, b) else a
inline fun maxNullable(a: Int?, b: Int): Int = maxNullable(b, a)
inline fun maxNullable(a: Int?, b: Int?): Int? = if (a != null) maxNullable(a, b) else b
inline fun maxNullable(a: Int, b: Int?, c: Int?): Int = maxNullable(maxNullable(a, b), c)
