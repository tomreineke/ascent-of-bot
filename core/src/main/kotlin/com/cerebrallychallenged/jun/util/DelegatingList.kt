package com.cerebrallychallenged.jun.util

private class DelegatingListIterator<T>(
        private val list: DelegatingList<T>,
        private var index: Int = 0
) : ListIterator<T> {
    private val size = list.size

    override fun hasNext(): Boolean = index < size

    override fun hasPrevious(): Boolean = index > 0

    override fun next(): T {
        if (hasNext()) {
            return list[index].also { ++index }
        } else {
            throw NoSuchElementException()
        }
    }

    override fun nextIndex(): Int = index

    override fun previous(): T {
        if (hasPrevious()) {
            return list[index - 1].also { --index }
        } else {
            throw NoSuchElementException()
        }
    }

    override fun previousIndex(): Int = index - 1
}

/**
 * Provides default implementations for [List] classes that cannot inherit from [AbstractList],
 * e.g., because they are inline classes.
 */
interface DelegatingList<T> : List<T> {
    override fun contains(element: T): Boolean = indexOf(element) != -1

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { it in this }

    override fun indexOf(element: T): Int = (0 until size).firstOrNull { this[it] == element } ?: -1

    override fun isEmpty(): Boolean = size == 0

    override fun iterator(): Iterator<T> = listIterator()

    override fun lastIndexOf(element: T): Int = (size - 1 downTo 0).firstOrNull { this[it] == element } ?: -1

    override fun listIterator(): ListIterator<T> = DelegatingListIterator(this, 0)

    override fun listIterator(index: Int): ListIterator<T> = DelegatingListIterator(this, index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> = (fromIndex until toIndex).map { this[it] }
}