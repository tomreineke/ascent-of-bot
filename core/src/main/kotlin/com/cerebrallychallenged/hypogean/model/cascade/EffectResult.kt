package com.cerebrallychallenged.hypogean.model.cascade

import com.cerebrallychallenged.hypogean.model.effect.EffectKind
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.EffectValue
import com.cerebrallychallenged.hypogean.model.effect.EffectValueModifier

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
