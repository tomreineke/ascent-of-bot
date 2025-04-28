package com.cerebrallychallenged.jun.util

/**
 * Returns the sum of all values produced by [selector] function applied to each element in the collection.
 *
 * Is ported from std lib to [Float].
 */
inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum: Float = 0.0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

val IntRange.size: Int
    get() = if (isEmpty()) 0 else (last - first) + 1

val ClosedFloatingPointRange<Float>.length: Float
    get() = endInclusive - start

fun <T> bigConcat(vararg lists: List<T>): List<T> = mutableListOf<T>().apply {
    lists.forEach(::addAll)
}

fun <T> bigUnion(vararg sets: Set<T>): Set<T> = mutableSetOf<T>().apply {
    sets.forEach(::addAll)
}

inline fun <T> MutableCollection<T>.removeFirst(predicate: (T) -> Boolean): T? {
    val iterator = iterator()
    while (iterator.hasNext()) {
        val element = iterator.next()
        if (predicate(element)) {
            iterator.remove()
            return element
        }
    }
    return null
}

fun <T> MutableList<T>.removeConsecutiveDuplicates(): Boolean = removeConsecutiveDuplicatesBy { it }

inline fun <T, K> MutableList<T>.removeConsecutiveDuplicatesBy(selector: (T) -> K): Boolean {
    var lastKey: K? = null
    val iterator = iterator()
    var anyChange = false
    while (iterator.hasNext()) {
        val key = selector(iterator.next())
        if (key == lastKey) {
            iterator.remove()
            anyChange = true
        } else {
            lastKey = key
        }
    }
    return anyChange
}

inline fun <T, K: Any> Iterable<T>.findConsecutiveDuplicateBy(selector: (T) -> K): K? {
    var lastKey: K? = null
    for (element in this) {
        val key = selector(element)
        if (key == lastKey) {
            return key
        } else {
            lastKey = key
        }
    }
    return null
}
