package com.cerebrallychallenged.jun.util

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet

typealias CPointer = Long

@Suppress("NOTHING_TO_INLINE")
inline fun CPointer.isNull(): Boolean = this == 0L

@Suppress("NOTHING_TO_INLINE")
inline fun CPointer.isNotNull(): Boolean = this != 0L

class CPointer2ObjectOpenHashMap<T> {
    @PublishedApi
    internal val base = Long2ObjectOpenHashMap<T>()

    operator fun get(key: CPointer): T? = base.get(key)

    operator fun set(key: CPointer, value: T) {
        // Make sure to call the optimized specialized function here.
        @Suppress("ReplaceGetOrSet")
        base.set(key, value)
    }

    fun remove(key: CPointer): T? = base.remove(key)

    inline fun forEachKey(block: (CPointer) -> Unit) {
        val iterator = base.keys.iterator()
        while (iterator.hasNext()) {
            block(iterator.nextLong())
        }
    }

    inline fun forEachEntry(block: (CPointer, T) -> Unit) {
        for (entry in base.long2ObjectEntrySet()) {
            block(entry.longKey, entry.value)
        }
    }

    fun clear() {
        base.clear()
    }
}

class CPointer2CPointerOpenHashMap {
    @PublishedApi
    internal val base = Long2LongOpenHashMap()

    operator fun get(key: CPointer): CPointer = base.get(key)

    operator fun set(key: CPointer, value: CPointer) {
        // Make sure to call the optimized specialized function here.
        @Suppress("ReplaceGetOrSet")
        base.set(key, value)
    }

    fun remove(key: CPointer): CPointer = base.remove(key)

    inline fun forEachValue(block: (CPointer) -> Unit) {
        val iterator = base.values.iterator()
        while (iterator.hasNext()) {
            block(iterator.nextLong())
        }
    }

    fun clear() {
        base.clear()
    }
}

class CPointerOpenHashSet {
    @PublishedApi
    internal val base = LongOpenHashSet()

    fun add(element: CPointer) {
        base.add(element)
    }

    fun remove(element: CPointer): Boolean = base.remove(element)

    operator fun contains(element: CPointer): Boolean = element in base

    inline fun forEachValue(block: (CPointer) -> Unit) {
        val iterator = base.iterator()
        while (iterator.hasNext()) {
            block(iterator.nextLong())
        }
    }

    fun clear() {
        base.clear()
    }
}