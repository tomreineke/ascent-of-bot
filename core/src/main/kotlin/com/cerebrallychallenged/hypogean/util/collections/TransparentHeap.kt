package com.cerebrallychallenged.hypogean.util.collections

private const val INVALID_INDEX = -1

/**
 * Min heap.
 */
class TransparentHeap<K, T>(private val keyComparator: Comparator<K>) {
    companion object {
        operator fun <K : Comparable<K>, T> invoke(): TransparentHeap<K, T> =
                TransparentHeap(Comparator.naturalOrder())
    }

    inner class Node internal constructor(val data: T, initialKey: K, internal var index: Int) {
        var key: K = initialKey
            private set

        fun remove() {
            if (index == INVALID_INDEX) {
                throw IllegalStateException("Node has already been removed")
            }
            val size = list.size
            val lastIndex = size - 1
            if (index == lastIndex) {
                list.removeAt(lastIndex)
            } else {
                val last = list[lastIndex]
                list[index] = last
                last.index = index
                list.removeAt(lastIndex)
                last.bubbleUp()
                last.bubbleDown()
            }
            index = INVALID_INDEX
        }

        fun decreaseKey(key: K) {
            require(keyComparator.compare(key, this.key) <= 0)
            this.key = key
            bubbleUp()
        }

        fun updateKey(key: K) {
            this.key = key
            bubbleUp()
            bubbleDown()
        }

        internal fun bubbleUp() {
            while (index > 0) {
                val parentIndex = (index - 1) / 2
                val parentNode = list[parentIndex]
                if (keyComparator.compare(key, parentNode.key) < 0) {
                    list[index] = parentNode
                    list[parentIndex] = this
                    parentNode.index = index
                    index = parentIndex
                } else {
                    break
                }
            }
        }

        internal fun bubbleDown() {
            while (true) {
                val leftChildIndex = 2 * index + 1
                val rightChildIndex = 2 * index + 2
                if (leftChildIndex >= list.size) {
                    break
                }
                var minChild = list[leftChildIndex]
                if (rightChildIndex < list.size) {
                    val rightChild = list[rightChildIndex]
                    if (keyComparator.compare(minChild.key, rightChild.key) > 0) {
                        minChild = rightChild
                    }
                }
                val minChildIndex: Int = minChild.index
                if (keyComparator.compare(key, minChild.key) > 0) {
                    list[index] = minChild
                    list[minChildIndex] = this
                    minChild.index = index
                    index = minChildIndex
                } else {
                    break
                }
            }
        }
    }

    private val list = mutableListOf<Node>()

    fun add(data: T, key: K): Node = Node(data, key, list.size).apply {
        list.add(this)
        bubbleUp()
    }

    fun extractMin(): Node {
        val size = list.size
        if (size == 0) {
            throw NoSuchElementException()
        }
        val result = list[0]
        if (size > 1) {
            val lastIndex = size - 1
            val last = list[lastIndex]
            list[0] = last
            last.index = 0
            list.removeAt(lastIndex)
            last.bubbleDown()
        } else {
            list.clear()
        }
        result.index = INVALID_INDEX
        return result
    }

    val size: Int
        get() = list.size

    fun isEmpty(): Boolean = list.isEmpty()

    fun clear() {
        for (node in list) {
            node.index = INVALID_INDEX
        }
        list.clear()
    }
}