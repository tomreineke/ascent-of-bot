package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.activeActor
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.CausalChange
import com.cerebrallychallenged.hypogean.model.cascade.EffectConsequence
import com.cerebrallychallenged.hypogean.model.cascade.EffectResult
import com.cerebrallychallenged.hypogean.vanilla.attributes.energy
import kotlin.math.min

class SyphoningLoss(override val effectResult: EffectResult, override val delta: Int) : CausalChange
class SyphoningGain(override val effectResult: EffectResult, override val delta: Int) : CausalChange

class SyphonEnergyConsequence(target: Entity, private val result: EffectResult) : EffectConsequence(target, result) {
    context(CascadeBlock)
    override suspend fun execute() {
        val effectiveAmount = min(causalChange.delta, target.energy)
        if (effectiveAmount > 0) {
            schedule(ChangeEnergyConsequence(target, SyphoningLoss(result, -effectiveAmount)))
            world.activeActor?.let { activeActor ->
                if (activeActor != target) {
                    schedule(ChangeEnergyConsequence(target, SyphoningGain(result, effectiveAmount)))
                }
            }
        }
    }
}
