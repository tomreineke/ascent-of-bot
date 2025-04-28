package com.cerebrallychallenged.hypogean.model.maps

abstract class MapBackingSet<T> : MutableSet<T> {
    class MutableEntry<K, V>(
            override val key: K,
            private val getter: () -> V,
            private val setter: (V) -> V
    ) : MutableMap.MutableEntry<K, V> {
        override var value: V
            get() = getter()
            set(value) {
                setter(value)
            }

        override fun setValue(newValue: V): V = setter(newValue)

        override fun equals(other: Any?): Boolean = when {
            this === other -> true
            other !is Map.Entry<*, *> -> false
            else -> key == other.key && value == other.value
        }

        override fun hashCode(): Int = key.hashCode() xor value.hashCode()
    }

    override fun add(element: T): Boolean {
        throw UnsupportedOperationException("Cannot add elements to set backing map")
    }

    override fun addAll(elements: Collection<T>): Boolean {
        throw UnsupportedOperationException("Cannot add elements to set backing map")
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return if (size > elements.size) {
            var changed = false
            for (element in elements) {
                changed = changed or remove(element)
            }
            changed
        } else {
            removeAll { it in elements }
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean = retainAll { it in elements }

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { it in this }

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Set<*> -> false
        else -> other.size == this.size && other.all { it in this }
    }

    override fun hashCode(): Int = sumOf { it.hashCode() }

    override fun toString(): String = joinToString(prefix = "[", postfix = "]")
}
