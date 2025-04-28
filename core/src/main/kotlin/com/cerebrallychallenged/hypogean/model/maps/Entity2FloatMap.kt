package com.cerebrallychallenged.hypogean.model.maps

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import it.unimi.dsi.fastutil.floats.FloatBinaryOperator
import it.unimi.dsi.fastutil.floats.FloatCollection
import it.unimi.dsi.fastutil.ints.Int2FloatMap
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap

class Entity2FloatMap<K : Entity> @PublishedApi internal constructor(
    world: World,
    entityClass: Class<K>
) : EntityMap<K, Float>(world, entityClass) {
    companion object {
        inline operator fun <reified E : Entity> invoke(world: World): Entity2FloatMap<E> =
            Entity2FloatMap(world, E::class.java)
    }

    private val map: Int2FloatMap = Int2FloatOpenHashMap().apply {
        defaultReturnValue(Float.NaN)
    }

    override val size: Int
        get() = map.size

    override fun containsKey(key: K): Boolean = map.containsKey(key.id)

    override fun containsValue(value: Float): Boolean = map.containsValue(value)

    override fun get(key: K): Float? = map.get(key.id).takeUnless { it.isNaN() }

    override fun isEmpty(): Boolean = map.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<K, Float>> by lazy {
        val entrySet = map.int2FloatEntrySet()

        object : MapBackingSet<MutableMap.MutableEntry<K, Float>>() {
            override fun clear() {
                entrySet.clear()
            }

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, Float>> =
                entrySet.iterator().map { entry ->
                    MutableEntry(entry.intKey.toEntity(), entry::getFloatValue, entry::setValue)
                }

            override fun remove(element: MutableMap.MutableEntry<K, Float>): Boolean =
                map.remove(element.key.id, element.value)

            override val size: Int
                get() = map.size

            override fun contains(element: MutableMap.MutableEntry<K, Float>): Boolean {
                @Suppress("UNCHECKED_CAST") // Ensures interoperability with Java code having null entries.
                val nullableElement = element as MutableMap.MutableEntry<K?, Float?>
                return map.get((nullableElement.key ?: return false).id) == (nullableElement.value ?: return false)
            }


            override fun isEmpty(): Boolean = map.isEmpty()
        }
    }

    override val keys: MutableSet<K> by lazy { initializer(map.keys) }

    override val values: FloatCollection
        get() = map.values

    override fun clear() {
        map.clear()
    }

    override fun put(key: K, value: Float): Float? = map.put(key.id, value).takeUnless { it.isNaN() }

    override fun putAll(from: Map<out K, Float>) {
        for ((entity, value) in from) {
            map[entity.id] = value
        }
    }

    override fun remove(key: K): Float? = map.remove(key.id).takeUnless { it.isNaN() }

    fun mergeFloat(key: K, value: Float, operator: FloatBinaryOperator) {
        map.mergeFloat(key.id, value, operator)
    }
}
