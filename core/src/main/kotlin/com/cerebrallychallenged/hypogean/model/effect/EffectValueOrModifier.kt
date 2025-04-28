package com.cerebrallychallenged.hypogean.model.effect

class EffectValueOrEffectModifier(
    private val value: Int,
    override val kind: EffectKind
): EffectValueExpression, EffectValueModifierExpression {
    override fun asEffectValue(): EffectValue = EffectValue.Absolute(value..value, kind)

    override fun asEffectValueModifier(): EffectValueModifier =
        EffectValueModifier.Absolute(value, EffectKindSet.Singleton(kind))
}

infix fun Int.of(kind: EffectKind): EffectValueOrEffectModifier =
    EffectValueOrEffectModifier(this, kind)
