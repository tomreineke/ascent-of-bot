package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.attribute

class EffectModifier private constructor(val modifiers: List<EffectValueModifier>) {
    companion object {
        val Zero = EffectModifier(listOf())

        operator fun invoke(vararg modifiers: EffectValueModifierExpression): EffectModifier = Zero.update(*modifiers)
    }

    private fun updated(key: EffectKindSet, newValue: EffectValueModifier?): EffectModifier {
        val resultModifiers = modifiers.mapNotNullTo(mutableListOf()) { oldValue ->
            val newSet = oldValue.kinds - key
            if (newSet == oldValue.kinds) {
                oldValue
            } else if (newSet.isEmpty()) {
                null
            } else {
                oldValue.updateKinds(newSet)
            }
        }
        newValue?.let {
            resultModifiers.add(it)
        }
        return EffectModifier(resultModifiers)
    }

    fun update(vararg modifiers: EffectValueModifierExpression): EffectModifier {
        var result = this
        for (expression in modifiers) {
            val modifier = expression.asEffectValueModifier()
            result = result.updated(modifier.kinds, modifier)
        }
        return result
    }

    fun remove(effectKindSet: EffectKindSet): EffectModifier = updated(effectKindSet, null)

    fun isEmpty(): Boolean = modifiers.isEmpty()
}

/**
 * Reduction of effects if this `Entity` is the target.
 */
var Entity.passiveEffectModifier: EffectModifier by attribute(EffectModifier.Zero)

/**
 * Reduction of effects provided to entities carrying this `Entity`.
 */
var Entity.providedPassiveEffectModifier: EffectModifier by attribute(EffectModifier.Zero)
