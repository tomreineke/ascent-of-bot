package com.cerebrallychallenged.hypogean.util

interface ConstraintsMap<E, V> {
    operator fun get(element: E): V
}

class MutableConstraintsMap<E, V>(
        private val defaultValue: (E) -> V = { throw NoSuchElementException("No matching constraint for $it") }
) : ConstraintsMap<E, V> {
    private data class Entry<E, V>(val constraint: (E) -> Boolean, val value: V)

    private val entries = mutableListOf<Entry<E, V>>()

    operator fun set(constraint: E.() -> Boolean, value: V) {
        entries.add(Entry(constraint, value))
    }

    override fun get(element: E): V =
            entries.firstOrNull { (constraint, _) -> constraint(element) }?.value
                    ?: defaultValue(element)

    fun <W> mapValues(valueMapping: (V) -> W): MutableConstraintsMap<E, W> =
            MutableConstraintsMap<E, W> { valueMapping(defaultValue(it)) }.also { result ->
                for ((constraint, value) in entries) {
                    result[constraint] = valueMapping(value)
                }
            }
}
