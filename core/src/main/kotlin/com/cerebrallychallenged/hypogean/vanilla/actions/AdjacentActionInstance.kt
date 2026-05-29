package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.base.Equipment
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealDirectEffect
import com.cerebrallychallenged.hypogean.vanilla.cascade.handleEquipment
import com.cerebrallychallenged.hypogean.vanilla.cascade.presentEntities
import com.cerebrallychallenged.hypogean.view.report.reportHeal
import com.cerebrallychallenged.hypogean.view.report.reportHit

abstract class AdjacentActionInstance (
    activeActor: Actor,
    action: Action,
    protected val assumedActiveActorLocation: Cell,
    private val isHealing: Boolean,
    final override val equipment: Equipment,
    final override val target: LocatedEntity,
): ActionInstance(action, activeActor) {
    override val initiativeCost: InitiativeCost = InitiativeCost.Delta(equipment.initiativeCost)

    context(CascadeContext)
    override suspend fun execute() = cascadeBlock {
        //FIXME[Anim] Emit MeleeAction
        val meleeAnimationTime = 0.0f
        delay(meleeAnimationTime)
        val actualTargets = if (target is Cell) {
            // presentEntities also returns the cell that was targeted
            val presentEntities = target.presentEntities.filter { it != target }
            // if no entities are present only return the cell
            if (presentEntities.none()) {
                sequenceOf(target)
            } else {
                presentEntities
            }
        } else {
            sequenceOf(target)
        }
        for (target in actualTargets) {
            dealDirectEffect(target, equipment.directEffect, EffectModifiers.Empty, EffectReason.ByEntity(equipment))
            if (isHealing) {
                reportHeal(activeActor, equipment, target)
            } else {
                reportHit(activeActor, equipment, target)
            }
        }
        handleEquipment(equipment)
    }
}
