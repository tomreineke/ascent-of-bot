package com.cerebrallychallenged.hypogean.model.maps

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import it.unimi.dsi.fastutil.ints.IntSet

abstract class EntityMap<K : Entity, V> @PublishedApi internal constructor(
        private val world: World,
        private val entityClass: Class<K>
) : MutableMap<K, V> {
    fun Int.toEntity(): K = world.byId(this, entityClass)

    // Needed as workaround, see https://youtrack.jetbrains.com/issue/KT-31005
    abstract override fun get(key: K): V?

    abstract override fun remove(key: K): V?

    abstract override val values: MutableCollection<V>

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other !is Map<*, *> -> false
        else -> entries == other.entries
    }

    override fun hashCode(): Int = entries.sumOf { it.hashCode() }

    override fun toString(): String =
            entries.joinToString(prefix = "{", postfix = "}") { "${it.key}=${it.value}" }

    fun initializer(keySet: IntSet): MutableSet<K> {
        return object : MapBackingSet<K>() {
            override fun clear() {
                keySet.clear()
            }

            override fun iterator(): MutableIterator<K> = keySet.iterator().map { it.toEntity() }

            override fun remove(element: K): Boolean = keySet.remove(element.id)

            override val size: Int
                get() = keySet.size

            override fun contains(element: K): Boolean = keySet.contains(element.id)

            override fun isEmpty(): Boolean = keySet.isEmpty()
        }
    }
}
