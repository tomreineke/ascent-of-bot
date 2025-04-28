package com.cerebrallychallenged.hypogean.model.maps

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import it.unimi.dsi.fastutil.doubles.DoubleCollection
import it.unimi.dsi.fastutil.ints.Int2DoubleMap
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap

class Entity2DoubleMap<K : Entity> @PublishedApi internal constructor(
        world: World,
        entityClass: Class<K>
) : EntityMap<K, Double>(world, entityClass) {
    companion object {
        inline operator fun <reified E : Entity> invoke(world: World): Entity2DoubleMap<E> =
                Entity2DoubleMap(world, E::class.java)
    }

    private val map: Int2DoubleMap = Int2DoubleOpenHashMap().apply {
        defaultReturnValue(Double.NaN)
    }

    override val size: Int
        get() = map.size

    override fun containsKey(key: K): Boolean = map.containsKey(key.id)

    override fun containsValue(value: Double): Boolean = map.containsValue(value)

    override fun get(key: K): Double? = map.get(key.id).takeUnless { it.isNaN() }

    override fun isEmpty(): Boolean = map.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<K, Double>> by lazy {
        val entrySet = map.int2DoubleEntrySet()

        object : MapBackingSet<MutableMap.MutableEntry<K, Double>>() {
            override fun clear() {
                entrySet.clear()
            }

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, Double>> =
                    entrySet.iterator().map { entry ->
                        MutableEntry(entry.intKey.toEntity(), entry::getDoubleValue, entry::setValue)
                    }

            override fun remove(element: MutableMap.MutableEntry<K, Double>): Boolean =
                    map.remove(element.key.id, element.value)

            override val size: Int
                get() = map.size

            override fun contains(element: MutableMap.MutableEntry<K, Double>): Boolean {
                @Suppress("UNCHECKED_CAST") // Ensures interoperability with Java code having null entries.
                val nullableElement = element as MutableMap.MutableEntry<K?, Double?>
                return map.get((nullableElement.key ?: return false).id) == (nullableElement.value ?: return false)
            }


            override fun isEmpty(): Boolean = map.isEmpty()
        }
    }

    override val keys: MutableSet<K> by lazy { initializer(map.keys) }

    override val values: DoubleCollection
        get() = map.values

    override fun clear() {
        map.clear()
    }

    override fun put(key: K, value: Double): Double? = map.put(key.id, value).takeUnless { it.isNaN() }

    override fun putAll(from: Map<out K, Double>) {
        for ((entity, value) in from) {
            map[entity.id] = value
        }
    }

    override fun remove(key: K): Double? = map.remove(key.id).takeUnless { it.isNaN() }
}
