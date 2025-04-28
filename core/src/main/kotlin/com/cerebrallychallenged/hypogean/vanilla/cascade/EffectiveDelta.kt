package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute

sealed class EffectiveDelta {
    companion object {
        fun <E : Entity> compute(delta: Int, entity: E, attribute: SimpleIntAttribute<E>): EffectiveDelta {
            val current = attribute.current.get(entity)
            val max = attribute.max.get(entity)
            val newValue = current + delta
            return when {
                newValue < 0 -> NonNegative(delta, current)
                newValue > max -> Capped(delta, current, max)
                else -> Regular(delta)
            }
        }
    }

    data class Regular(override val originalDelta: Int) : EffectiveDelta() {
        override val effectiveDelta: Int
            get() = originalDelta
    }

    data class Capped(override val originalDelta: Int, val currentAttributeValue: Int, val maxAttributeValue: Int) : EffectiveDelta() {
        override val effectiveDelta: Int = maxAttributeValue - currentAttributeValue
    }

    data class NonNegative(override val originalDelta: Int, val oldAttributeValue: Int) : EffectiveDelta() {
        override val effectiveDelta: Int = -oldAttributeValue
    }

    abstract val originalDelta: Int

    abstract val effectiveDelta: Int
}
