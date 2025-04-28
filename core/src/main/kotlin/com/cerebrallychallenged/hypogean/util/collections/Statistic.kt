package com.cerebrallychallenged.hypogean.util.collections

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.maps.Entity2ObjectMap
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap
import it.unimi.dsi.fastutil.ints.Int2IntSortedMap
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import java.util.function.IntBinaryOperator

private class IntStatisticRecorder {
    private val map: Int2IntSortedMap = Int2IntRBTreeMap()

    private var localCount: Int = 0

    fun record(value: Int) {
        map.mergeInt(value, 1, IntBinaryOperator { old, new -> old + new })
        ++localCount
    }

    fun toIntStatistic(totalCount: Int): IntStatistic {
        if (totalCount > localCount) {
            map.mergeInt(0, totalCount - localCount, IntBinaryOperator { old, new -> old + new })
        }

        val distinctCount = map.size
        val probabilityArray = FloatArray(distinctCount * 3)
        val valueArray = map.keys.toIntArray()

        var linearProduct = 0L
        var count = 0
        for ((index, frequency) in map.values.withIndex()) {
            linearProduct += frequency * valueArray[index]
            count += frequency
            probabilityArray[index] = frequency.toFloat() / totalCount
            probabilityArray[distinctCount + index] = count.toFloat() / totalCount
        }
        count = 0
        for ((i, entry) in map.int2IntEntrySet().reversed().withIndex()) {
            val frequency = entry.intValue
            count += frequency
            val index = distinctCount - i - 1
            probabilityArray[2 * distinctCount + index] = count.toFloat() / totalCount
        }
        return ArrayIntStatistic(
            linearProduct.toFloat() / totalCount,
            distinctCount,
            valueArray,
            probabilityArray
        )
    }
}

private class TypedIntStatisticRecorder<T> {
    private val map: Object2ObjectMap<T, IntStatisticRecorder> = Object2ObjectArrayMap()

    private var hitCount: Int = 0

    fun record(kind: T, value: Int) {
        map.getOrPut(kind, ::IntStatisticRecorder).record(value)
    }

    fun recordHit() {
        ++hitCount
    }

    fun toTypedIntStatistic(totalCount: Int): TypedIntStatistic<T> = TypedIntStatistic(
        map.mapValuesTo(Object2ObjectArrayMap()) { (_, recorder) ->
            recorder.toIntStatistic(totalCount)
        },
        hitCount.toFloat() / totalCount
    )
}

class WorldStatisticRecorder<T>(private val world: World) {
    private val map: MutableMap<Entity, TypedIntStatisticRecorder<T>> = Entity2ObjectMap(world)

    private var totalCount: Int = 0

    fun record(entity: Entity, kind: T, value: Int) {
        map.getOrPut(entity, ::TypedIntStatisticRecorder).record(kind, value)
    }

    fun recordHit(entity: Entity) {
        map.getOrPut(entity, ::TypedIntStatisticRecorder).recordHit()
    }

    fun incCount() {
        ++totalCount
    }

    fun toWorldStatistic(): WorldStatistic<T> = WorldStatistic(
        map.mapValuesTo(Entity2ObjectMap(world)) { (_, recorder) ->
            recorder.toTypedIntStatistic(totalCount)
        }
    )
}

class TypedIntStatistic<T>(private val map: Map<T, IntStatistic>, val hitRatio: Float) {
    companion object {
        private val Empty = TypedIntStatistic<Any>(mapOf(), 0.0f)

        fun <T> empty(): TypedIntStatistic<T> {
            @Suppress("UNCHECKED_CAST")
            return Empty as TypedIntStatistic<T>
        }
    }

    operator fun get(kind: T): IntStatistic = map[kind] ?: IntStatistic.Empty
}

class WorldStatistic<T>(private val map: Map<Entity, TypedIntStatistic<T>>) {
    operator fun get(entity: Entity): TypedIntStatistic<T> = map[entity] ?: TypedIntStatistic.empty()
}
