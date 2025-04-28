package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.jun.util.findConsecutiveDuplicateBy

class Effect private constructor(val effects: List<EffectValue>) {
    companion object {
        val Zero = Effect(listOf())

        operator fun invoke(vararg effects: EffectValueExpression): Effect {
            val effectList = effects.mapTo(mutableListOf(), EffectValueExpression::asEffectValue)
            effectList.sortBy { it.kind }
            effectList.findConsecutiveDuplicateBy { it.kind }?.let {
                modelError("Effect contains multiple entries for $it")
            }
            return Effect(effectList)
        }
    }

    fun update(vararg effects: EffectValueExpression): Effect {
        val result = this.effects.toMutableList()
        for (expression in effects) {
            val effect = expression.asEffectValue()
            val index = result.binarySearchBy(effect.kind) { it.kind }
            if (index >= 0) {
                result[index] = effect
            } else {
                result.add(-index - 1, effect)
            }
        }
        return Effect(result)
    }

    fun remove(vararg kinds: EffectKind): Effect = Effect(effects.filterNot { it.kind in kinds })

    fun isEmpty(): Boolean = effects.isEmpty()
}

/**
 * Direct effect (in contrast to indirect from, e.g., explosions) inflicted on the target
 * by that weapon, tool, or status effect.
 */
var Entity.directEffect: Effect by attribute(Effect.Zero)
