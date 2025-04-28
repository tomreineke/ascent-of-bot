package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.util.toSimpleClassString

abstract class EffectKind : Comparable<EffectKind> {
    private val simpleClassString = toSimpleClassString()

    override fun toString(): String = simpleClassString

    override fun compareTo(other: EffectKind): Int = simpleClassString.compareTo(other.simpleClassString)

    operator fun plus(other: EffectKindSet): EffectKindSet = other + this

    operator fun plus(other: EffectKind): EffectKindSet =
        if (other == this) EffectKindSet.Singleton(this) else EffectKindSet.Complex(listOf(), listOf(this, other))
}
