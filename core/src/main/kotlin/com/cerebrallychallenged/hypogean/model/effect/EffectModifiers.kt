package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.vanilla.cascade.effectImmunities
import com.cerebrallychallenged.hypogean.vanilla.cascade.providedEffectImmunities
import com.cerebrallychallenged.hypogean.vanilla.cascade.providingEntities
import java.util.Collections
import java.util.EnumMap

sealed class EffectModifiers : Iterable<Map.Entry<EffectModifiers.Phase, List<EffectModifiers.Entry>>> {
    object Empty: EffectModifiers() {
        override fun toMutable(): MutableEffectModifiers = MutableEffectModifiers()

        override fun iterator(): Iterator<Map.Entry<Phase, List<Entry>>> = Collections.emptyIterator()
    }

    enum class Phase(val displayName: String, val displayNameVariant: String? = null) {
        ProducersConsumers("Producers", "Consumers"),
        Active("Active Modifiers"),
        Cover("Cover"),
        Falloff("Falloff"),
        Immunity("Immunities"),
        Passive("Passive Modifiers"),
    }

    sealed class Reason {
        data class By(val entity: Entity) : Reason()
        object Cover : Reason()
        data class Distance(val distance: Float) : Reason()
    }

    data class Entry(val modifier: EffectModifier, val reason: Reason)

    abstract fun toMutable(): MutableEffectModifiers
}

class MutableEffectModifiers private constructor(
    private val map: EnumMap<Phase, MutableList<Entry>>
) : EffectModifiers() {
    constructor() : this(EnumMap(Phase::class.java))

    fun add(phase: Phase, modifier: EffectModifier, reason: Reason) {
        map.getOrPut(phase, ::mutableListOf).add(Entry(modifier, reason))
    }

    override fun toMutable(): MutableEffectModifiers = MutableEffectModifiers(
        EnumMap<Phase, MutableList<Entry>>(Phase::class.java).also {
            for ((phase, list) in map) {
                it[phase] = list.toMutableList()
            }
        }
    )

    override fun iterator(): Iterator<Map.Entry<Phase, List<Entry>>> = map.iterator()
}

private fun MutableEffectModifiers.addPassiveModifier(modifier: EffectModifier, reason: Entity) {
    if (!modifier.isEmpty()) {
        add(EffectModifiers.Phase.Passive, modifier, EffectModifiers.Reason.By(reason))
    }
}

private fun MutableEffectModifiers.addImmunities(immunities: EffectKindSet, reason: Entity) {
    if (!immunities.isEmpty()) {
        add(
            EffectModifiers.Phase.Immunity,
            EffectModifier(-100 percentOf immunities),
            EffectModifiers.Reason.By(reason)
        )
    }
}

fun MutableEffectModifiers.addPassiveModifiersAndImmunitiesForTarget(target: Entity) {
    addPassiveModifier(target.passiveEffectModifier, target)
    addImmunities(target.effectImmunities, target)
    for (providingEntity in target.providingEntities) {
        addPassiveModifier(providingEntity.providedPassiveEffectModifier, providingEntity)
        addImmunities(providingEntity.providedEffectImmunities, providingEntity)
    }
}
