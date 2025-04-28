package com.cerebrallychallenged.hypogean.model.maps

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.util.appendBits
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet

private fun key(entity1: Entity, entity2: Entity): Long = entity1.id.appendBits(entity2.id)

/**
 * Stores relational data between two entities.
 * Note that this class does not implement the [Map] interface because it cannot efficiently enumerate its entries.
 */
class SparseEntityMatrix<K1: Entity, K2: Entity, V: Any> {
    private val map: Long2ObjectMap<V> = Long2ObjectOpenHashMap()

    fun clear() {
        map.clear()
    }

    operator fun get(entity1: K1, entity2: K2): V? = map.get(key(entity1, entity2))

    operator fun set(entity1: K1, entity2: K2, value: V) {
        @Suppress("ReplacePutWithAssignment") // Make sure the overload with primitive long key is called.
        map.put(key(entity1, entity2), value)
    }

    fun remove(entity1: K1, entity2: K2) {
        map.remove(key(entity1, entity2))
    }
}

class SparseEntityRelation<K1: Entity, K2: Entity> {
    private val set: LongSet = LongOpenHashSet()

    fun clear() {
        set.clear()
    }

    operator fun get(entity1: K1, entity2: K2): Boolean = set.contains(key(entity1, entity2))

    operator fun set(entity1: K1, entity2: K2, value: Boolean) {
        val key = key(entity1, entity2)
        if (value) {
            set.add(key)
        } else {
            set.remove(key)
        }
    }

    fun remove(entity1: K1, entity2: K2) {
        set.remove(key(entity1, entity2))
    }
}