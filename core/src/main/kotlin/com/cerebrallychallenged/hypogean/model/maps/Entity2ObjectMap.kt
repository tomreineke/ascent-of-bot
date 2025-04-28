package com.cerebrallychallenged.hypogean.model.maps

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap

class Entity2ObjectMap<K : Entity, T> @PublishedApi internal constructor(
        world: World,
        entityClass: Class<K>
) : EntityMap<K, T>(world, entityClass) {
    companion object {
        inline operator fun <reified E : Entity, T> invoke(world: World): Entity2ObjectMap<E, T> =
                Entity2ObjectMap(world, E::class.java)
    }

    private val map: Int2ObjectMap<T> = Int2ObjectOpenHashMap()

    override val size: Int
        get() = map.size

    override fun containsKey(key: K): Boolean = map.containsKey(key.id)

    override fun containsValue(value: T): Boolean = map.containsValue(value)

    override fun get(key: K): T? = map.get(key.id)

    override fun isEmpty(): Boolean = map.isEmpty()

    override val entries: MutableSet<MutableMap.MutableEntry<K, T>> by lazy {
        val entrySet = map.int2ObjectEntrySet()

        object : MapBackingSet<MutableMap.MutableEntry<K, T>>() {
            override fun clear() {
                entrySet.clear()
            }

            override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, T>> =
                    entrySet.iterator().map { entry ->
                        MutableEntry(entry.intKey.toEntity(), entry::value, entry::setValue)
                    }

            override fun remove(element: MutableMap.MutableEntry<K, T>): Boolean =
                    map.remove(element.key.id, element.value as Any)

            override val size: Int
                get() = map.size

            override fun contains(element: MutableMap.MutableEntry<K, T>): Boolean {
                val id = element.key.id
                return map.containsKey(id) && map.get(id) == element.value
            }

            override fun isEmpty(): Boolean = map.isEmpty()
        }
    }

    override val keys: MutableSet<K> by lazy { initializer(map.keys) }

    override val values: MutableCollection<T>
        get() = map.values

    override fun clear() {
        map.clear()
    }

    override fun put(key: K, value: T): T? = map.put(key.id, value)

    override fun putAll(from: Map<out K, T>) {
        for ((entity, value) in from) {
            map[entity.id] = value
        }
    }

    override fun remove(key: K): T? = map.remove(key.id)

    fun merge(key: K, value: T, remappingFunction: (T, T) -> T) {
        map.merge(key.id, value, remappingFunction)
    }
}
