package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.jun.util.removeConsecutiveDuplicates
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

sealed class EffectKindSet {
    companion object {
        private fun create(subclasses: List<KClass<out EffectKind>>, kinds: MutableList<EffectKind>): EffectKindSet {
            return if (subclasses.isNotEmpty()) {
                Complex(subclasses, kinds)
            } else {
                kinds.sort()
                kinds.removeConsecutiveDuplicates()
                when (kinds.size) {
                    0 -> Empty
                    1 -> Singleton(kinds[0])
                    else -> Complex(subclasses, kinds)
                }
            }
        }

        fun of(vararg kinds: EffectKind): EffectKindSet = create(listOf(), kinds.toMutableList())
    }

    object Empty : EffectKindSet() {
        override fun contains(kind: EffectKind): Boolean = false

        override fun plus(other: EffectKindSet): EffectKindSet = other

        override fun plus(other: EffectKind): EffectKindSet = Singleton(other)

        override fun minus(other: EffectKindSet): EffectKindSet = this

        override fun minus(other: EffectKind): EffectKindSet = this

        override fun toSequence(): Sequence<EffectKind> = emptySequence()

        override fun isEmpty(): Boolean = true
    }

    internal data class Singleton(val kind: EffectKind) : EffectKindSet() {
        override fun contains(kind: EffectKind): Boolean = this.kind == kind

        override fun plus(other: EffectKindSet): EffectKindSet = other + kind

        override fun plus(other: EffectKind): EffectKindSet =
            if (kind == other) this else Complex(listOf(), listOf(kind, other))

        override fun minus(other: EffectKindSet): EffectKindSet = if (kind in other) Empty else this

        override fun minus(other: EffectKind): EffectKindSet = if (kind == other) Empty else this

        override fun toSequence(): Sequence<EffectKind> = sequenceOf(kind)

        override fun isEmpty(): Boolean = false
    }

    @PublishedApi
    internal data class Complex(
        val subclasses: List<KClass<out EffectKind>>,
        val kinds: List<EffectKind>
    ) : EffectKindSet() {
        override fun contains(kind: EffectKind): Boolean =
            subclasses.any { it.isInstance(kind) } || kinds.contains(kind)

        override fun plus(other: EffectKindSet): EffectKindSet {
            return when (other) {
                is Empty -> this
                is Singleton -> if (other.kind in this) {
                    this
                } else {
                    create(subclasses, kinds.toMutableList().also { it.add(other.kind) })
                }
                is Complex -> {
                    val sortedSubclasses = subclasses.toMutableList()
                    sortedSubclasses.addAll(other.subclasses)
                    sortedSubclasses.sortWith { c1, c2 ->
                        when {
                            c1.isSuperclassOf(c2) -> -1
                            c2.isSuperclassOf(c1) -> 1
                            else -> 0
                        }
                    }
                    val resultSubclasses = mutableListOf<KClass<out EffectKind>>()
                    for (c in sortedSubclasses) {
                        if (resultSubclasses.none { it.isSuperclassOf(c) }) {
                            resultSubclasses.add(c)
                        }
                    }
                    val resultKinds = mutableListOf<EffectKind>()
                    for (kind in kinds.asSequence() + other.kinds.asSequence()) {
                        if (resultSubclasses.none { it.isInstance(kind) } && kind !in resultKinds) {
                            resultKinds.add(kind)
                        }
                    }
                    create(resultSubclasses, resultKinds)
                }
            }
        }

        override fun plus(other: EffectKind): EffectKindSet =
            if (other in this) this else create(subclasses, kinds.toMutableList().also { it.add(other) })

        override operator fun minus(other: EffectKindSet): EffectKindSet {
            var anyRemoved = false
            val resultKinds = toSequence().filter {
                val isRemoved = it !in other
                if (isRemoved) {
                    anyRemoved = true
                }
                isRemoved
            }.toMutableList()
            if (!anyRemoved) return this
            return create(subclasses, resultKinds)
        }

        override fun minus(other: EffectKind): EffectKindSet = when (other) {
            !in this -> this
            in kinds -> create(subclasses, kinds.toMutableList().also { it.remove(other) })
            else -> create(listOf(), toSequence().filterNot { it == other }.toMutableList())
        }

        override fun toSequence(): Sequence<EffectKind> =
            if (subclasses.isEmpty()) kinds.asSequence() else EffectKinds.asSequence().filter { it in this }

        override fun isEmpty(): Boolean = false
    }

    abstract operator fun contains(kind: EffectKind): Boolean

    abstract operator fun plus(other: EffectKindSet): EffectKindSet

    abstract operator fun plus(other: EffectKind): EffectKindSet

    abstract operator fun minus(other: EffectKindSet): EffectKindSet

    abstract operator fun minus(other: EffectKind): EffectKindSet

    abstract fun toSequence(): Sequence<EffectKind>

    abstract fun isEmpty(): Boolean
}

inline fun <reified T: EffectKind> any(): EffectKindSet = EffectKindSet.Complex(listOf(T::class), listOf())
