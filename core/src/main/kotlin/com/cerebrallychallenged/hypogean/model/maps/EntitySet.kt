package com.cerebrallychallenged.hypogean.model.maps

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.util.ids
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.ints.IntSet

class EntitySet<E : Entity> @PublishedApi internal constructor(
        private val world: World,
        private val entityClass: Class<E>
) : MutableSet<E> {
    companion object {
        inline operator fun <reified E : Entity> invoke(world: World): EntitySet<E> =
                EntitySet(world, E::class.java)
    }

    private val set: IntSet = IntOpenHashSet()

    override fun add(element: E): Boolean = set.add(element.id)

    override fun addAll(elements: Collection<E>): Boolean {
        var anyChange = false
        for (entity in elements) {
            anyChange = anyChange or add(entity)
        }
        return anyChange
    }

    override fun remove(element: E): Boolean = set.remove(element.id)

    override operator fun contains(element: E): Boolean = set.contains(element.id)

    override fun clear() {
        set.clear()
    }

    override val size: Int
        get() = set.size

    override fun iterator(): MutableIterator<E> {
        val iterator = set.iterator()
        return object : MutableIterator<E> {
            override fun hasNext(): Boolean = iterator.hasNext()

            override fun next(): E = world.byId(iterator.nextInt(), entityClass)

            override fun remove() = iterator.remove()
        }
    }

    override fun removeAll(elements: Collection<E>): Boolean = set.removeAll(elements.ids())

    override fun retainAll(elements: Collection<E>): Boolean = set.retainAll(elements.ids())

    override fun containsAll(elements: Collection<E>): Boolean = set.containsAll(elements.ids())

    override fun isEmpty(): Boolean = set.isEmpty()

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other !is Set<*> -> false
        else -> other.size == this.size && other.all { it in this }
    }

    override fun hashCode(): Int = sumOf { it.hashCode() }

    override fun toString(): String = joinToString(separator = ", ", prefix = "[", postfix = "]")
}

inline fun <reified E : Entity> Sequence<E>.toEntitySet(world: World): EntitySet<E> = toCollection(EntitySet(world))
