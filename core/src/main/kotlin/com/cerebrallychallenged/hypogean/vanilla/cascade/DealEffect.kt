package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.cascade.EffectResult
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.EffectConsequenceFactory
import com.cerebrallychallenged.hypogean.model.effect.EffectConsequences
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.EffectValueModifier
import com.cerebrallychallenged.hypogean.model.effect.addPassiveModifiersAndImmunitiesForTarget
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.jun.math.truncateToInt
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap

context(CascadeBlock)
fun dealEffect(target: Entity, effect: Effect, modifiers: EffectModifiers, effectReason: EffectReason) {
    val consequences = world.feature<EffectConsequences>()
    val random = world.random
    val aggregatedConsequences =
        Object2ObjectArrayMap<EffectConsequenceFactory, MutableList<EffectResult.SignedTable>>()
    for (effectValue in effect.effects) {
        val kind = effectValue.kind
        val sampledBase = effectValue.sample(target, random)
        var base = sampledBase
        val phaseResults = mutableListOf<EffectResult.Phase>()

        for ((phase, effectModifiers) in modifiers) {
            if (base < 0 && phase != EffectModifiers.Phase.ProducersConsumers) {
                break
            }
            var total = base
            val appliedModifiers = mutableListOf<EffectResult.ModifierEntry>()
            for ((modifier, reason) in effectModifiers) {
                for (effectValueModifier in modifier.modifiers) {
                    if (kind in effectValueModifier.kinds) {
                        val amount = when (effectValueModifier) {
                            is EffectValueModifier.Absolute -> effectValueModifier.value
                            is EffectValueModifier.Relative -> (base * effectValueModifier.percent * 0.01f).truncateToInt()
                        }
                        appliedModifiers.add(EffectResult.ModifierEntry(amount, effectValueModifier, reason))
                        total += amount
                    }
                }
            }
            phaseResults.add(EffectResult.Phase(phase, base, total, appliedModifiers))
            base = total
        }
        val table = EffectResult.Table(
            kind,
            effectValue,
            sampledBase,
            phaseResults,
            base.coerceAtLeast(0),
            effectReason
        )
        for (entry in consequences[kind]) {
            val signedTable = EffectResult.SignedTable(table, entry.intValue)
            aggregatedConsequences.computeIfAbsent(entry.key) { mutableListOf() }.add(signedTable)
        }
    }
    for ((entry, tables) in aggregatedConsequences) {
        schedule(entry.create(target, EffectResult(tables)))
    }
}

context(CascadeBlock)
fun dealDirectEffect(target: Entity, effect: Effect, baseModifiers: EffectModifiers, reason: EffectReason) {
    val modifiers = baseModifiers.toMutable()
    modifiers.addPassiveModifiersAndImmunitiesForTarget(target)
    dealEffect(target, effect, modifiers, reason)
}
