package com.cerebrallychallenged.jun.util

/**
 * Returns the sum of all values produced by [selector] function applied to each element in the collection.
 *
 * Is ported from std lib to [Float].
 */
inline fun <T> Sequence<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0.0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

/**
 * Returns a sequence of
 * every element of this sequence paired with every element of the other sequence.
 */
fun <T, U> Sequence<T>.crossJoin(other: Sequence<U>): Sequence<Pair<T, U>> = sequence {
    for (t in this@crossJoin) {
        for (u in other) {
            yield(Pair(t, u))
        }
    }
}

/**
 * Returns a list of all elements of this sequence yielding a maximal value with respect to the specified comparator.
 */
inline fun <T, R : Comparable<R>> Sequence<T>.maxAllBy(selector: (T) -> R, comparator: Comparator<R>): List<T> {
    val maxElements = mutableListOf<T>()
    var maxValue: R? = null
    for (element in this) {
        val value = selector(element)
        val cmp = maxValue?.let { comparator.compare(it, value) } ?: -1
        when {
            cmp < 0 -> {
                maxValue = value
                maxElements.clear()
                maxElements.add(element)
            }
            cmp == 0 -> {
                maxElements.add(element)
            }
        }
    }
    return maxElements
}

/**
 * Returns a list of all elements of this sequence yielding a maximal value.
 */
inline fun <T, R : Comparable<R>> Sequence<T>.maxAllBy(selector: (T) -> R): List<T>
        = maxAllBy(selector, Comparator.naturalOrder())


/**
 * Returns a list of all elements of this sequence yielding a minimal value.
 */
inline fun <T, R : Comparable<R>> Sequence<T>.minAllBy(selector: (T) -> R): List<T>
        = maxAllBy(selector, Comparator.reverseOrder())

/**
 * Splits this sequence into a sequence of lists for which the specified selector yielded consecutive numbers.
 */
inline fun <T> Sequence<T>.chunkedConsecutiveBy(crossinline selector: (T) -> Int): Sequence<List<T>> = sequence {
    val iterator = this@chunkedConsecutiveBy.iterator()
    if (iterator.hasNext()) {
        val firstElement = iterator.next()
        var list = mutableListOf(firstElement)
        var prevValue = selector(firstElement)
        for (element in iterator) {
            val value = selector(element)
            if (value == prevValue + 1) {
                list.add(element)
            } else {
                yield(list)
                list = mutableListOf(element)
            }
            prevValue = value
        }
        if (list.isNotEmpty()) {
            yield(list)
        }
    }
}

/**
 * Splits this sequence into a sequence of lists of consecutive numbers.
 */
fun Sequence<Int>.chunkedConsecutive(): Sequence<List<Int>> = chunkedConsecutiveBy { it }

/**
 * Returns the product of all elements in the sequence.
 *
 * The operation is _terminal_.
 *
 * Is implemented analogous to [sum].
 */
@JvmName("productOfInt")
fun Sequence<Int>.product(): Int {
    var product = 1
    for (element in this) {
        product *= element
    }
    return product
}

/**
 * Returns the product of all elements in the sequence.
 *
 * The operation is _terminal_.
 *
 * Is implemented analogous to [sum].
 */
@JvmName("productOfDouble")
fun Sequence<Double>.product(): Double {
    var product = 1.0
    for (element in this) {
        product *= element
    }
    return product
}

/**
 * Returns the product of all elements in the sequence.
 *
 * The operation is _terminal_.
 *
 * Is implemented analogous to [sum].
 */
@JvmName("productOfFloat")
fun Sequence<Float>.product(): Float {
    var product = 1.0f
    for (element in this) {
        product *= element
    }
    return product
}

fun <T> Iterable<T>.associateWithIndex(): Map<T, Int>
        = withIndex().associate { (index, value) -> value to index }

fun <T> Sequence<T>.associateWithIndex()
        = withIndex().associate { (index, value) -> value to index }

/**
 * Splits this sequence into chunks as determined by the specified predicate.
 * The predicate is repeatedly given the current non-empty chunk and the next element in this sequence.
 * If the predicate returns `true`, the element is added to that chunk.
 * Otherwise, the chunk is closed and the the element becomes the first in a new chunk.
 */
inline fun <T> Sequence<T>.chunkedPartitionBy(
        crossinline predicate: (currentChunk: List<T>, nextElement: T) -> Boolean
): Sequence<List<T>> = sequence {
    val iterator = iterator()
    if (iterator.hasNext()) {
        var currentChunk = mutableListOf(iterator.next())
        for (element in iterator) {
            if (predicate(currentChunk, element)) {
                currentChunk.add(element)
            } else {
                yield(currentChunk)
                currentChunk = mutableListOf(element)
            }
        }
        yield(currentChunk)
    }
}

fun Sequence<*>.consume(): Unit = forEach { _ -> }

inline fun <T : Any, R> Sequence<T>.mapNotThrowing(crossinline f: (T) -> R): Sequence<R> = mapNotNull {
    try {
        f(it)
    } catch (e: Throwable) {
        null
    }
}

fun <T : Any> List<List<T?>>.forEachVariation(block: (List<T>) -> Unit) {
    forEachVariation(this, listOf(), block)
}

private fun <T : Any> forEachVariation(tail: List<List<T?>>, prefix: List<T>, block: (List<T>) -> Unit) {
    if (tail.isEmpty()) {
        block(prefix)
    } else {
        val tailTail = tail.drop(1)
        for (element in tail.first()) {
            val extendedPrefix = if (element != null) {
                prefix + element
            } else {
                prefix
            }
            forEachVariation(tailTail, extendedPrefix, block)
        }
    }
}
