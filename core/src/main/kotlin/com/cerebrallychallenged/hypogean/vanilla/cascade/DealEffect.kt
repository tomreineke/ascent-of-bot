package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.equippedItems
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.CausalChange
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.EffectConsequenceFactory
import com.cerebrallychallenged.hypogean.model.effect.EffectConsequences
import com.cerebrallychallenged.hypogean.model.effect.EffectKind
import com.cerebrallychallenged.hypogean.model.effect.EffectKindSet
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.EffectValue
import com.cerebrallychallenged.hypogean.model.effect.EffectValueModifier
import com.cerebrallychallenged.hypogean.model.effect.addPassiveModifiersAndImmunitiesForTarget
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.jun.math.truncateToInt
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap

/**
 * When `true`, this entity cannot take effect, i.e., is completely immune against all effect types.
 */
var Entity.isIndestructible: Boolean by attribute(false)

/**
 * Entities potentially protecting their bearer such as status effects and equipped items.
 */
val Entity.providingEntities: Sequence<Entity>
    get() = sequence {
        yieldAll(statusEffects)
        if (this@providingEntities is Actor) {
            yieldAll(equippedItems)
        }
    }

/**
 * The entity is completely immune against those effect types.
 * (protection for self)
 */
var Entity.effectImmunities: EffectKindSet by attribute(EffectKindSet.Empty)

/**
 * The entity (equipped item or status effect) provides complete immunity for its bearer against those effect types.
 * (protection for others)
 */
var Entity.providedEffectImmunities: EffectKindSet by attribute(EffectKindSet.Empty)

sealed class EffectReason {
    data class ByEntity(val entity: Entity): EffectReason()
    data class Named(val name: String): EffectReason()
}

class EffectResult(val tables: List<SignedTable>) : CausalChange {
    class ModifierEntry(val amount: Int, val modifier: EffectValueModifier, val reason: EffectModifiers.Reason)

    class Phase(val phase: EffectModifiers.Phase, val base: Int, val total: Int, val modifiers: List<ModifierEntry>)

    class Table(
        val kind: EffectKind,
        val effect: EffectValue,
        val sampledBase: Int,
        val phases: List<Phase>,
        val amount: Int,
        val reason: EffectReason
    )

    class SignedTable(
        val table: Table,
        val sign: Int
    )

    override val delta: Int = tables.sumOf { it.table.amount * it.sign }

    override val effectResult: EffectResult
        get() = this
}

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
