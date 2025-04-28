package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.linguistics.signedString

interface EffectValueModifierExpression {
    fun asEffectValueModifier(): EffectValueModifier
}

sealed class EffectValueModifier: EffectValueModifierExpression {
    data class Absolute internal constructor(
        val value: Int,
        override val kinds: EffectKindSet
    ) : EffectValueModifier() {
        override fun updateKinds(kinds: EffectKindSet): EffectValueModifier = copy(kinds = kinds)

        override fun toString(): String = value.signedString(usePlusSign = true)
    }

    data class Relative internal constructor(
        val percent: Int,
        override val kinds: EffectKindSet
    ) : EffectValueModifier() {
        override fun updateKinds(kinds: EffectKindSet): EffectValueModifier = copy(kinds = kinds)

        override fun toString(): String = "${percent.signedString(usePlusSign = true)}%"
    }

    abstract val kinds: EffectKindSet

    override fun asEffectValueModifier(): EffectValueModifier = this

    abstract fun updateKinds(kinds: EffectKindSet): EffectValueModifier
}

infix fun Int.of(effectKinds: EffectKindSet): EffectValueModifier = EffectValueModifier.Absolute(this, effectKinds)

infix fun Int.percentOf(kind: EffectKind): EffectValueModifier =
    EffectValueModifier.Relative(this, EffectKindSet.Singleton(kind))

infix fun Int.percentOf(kind: EffectKindSet): EffectValueModifier = EffectValueModifier.Relative(this, kind)
