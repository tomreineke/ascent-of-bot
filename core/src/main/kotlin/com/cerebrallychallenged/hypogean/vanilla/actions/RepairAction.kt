package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.model.base.Equipment
import com.cerebrallychallenged.hypogean.model.base.ToolSlot
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.executeCascade
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.causedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.util.collections.WorldStatistic
import com.cerebrallychallenged.hypogean.util.collections.WorldStatisticRecorder
import com.cerebrallychallenged.hypogean.vanilla.actors.Robot
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.cascade.causeStatusEffects
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealDirectEffect
import com.cerebrallychallenged.hypogean.vanilla.cascade.handleEquipment
import com.cerebrallychallenged.hypogean.vanilla.items.utility.RepairArm
import com.cerebrallychallenged.hypogean.view.report.reportHeal

object RepairAction : Action {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        createInstance<ToolSlot, RepairArm>(
            activeActor,
            assumedActiveActorLocation,
            this@RepairAction,
            ::RepairActionInstance,
            true
        ) {
            this is Robot
        }
    }

    override val category: ActionCategory = ActionCategory.Utility
}

class RepairActionInstance(
    activeActor: Actor,
    assumedActiveActorLocation: Cell,
    override val equipment: Equipment,
    override val target: LocatedEntity
) : ActionInstance(RepairAction, activeActor) {
    override val initiativeCost: InitiativeCost
        get() = InitiativeCost.Delta(equipment.initiativeCost)

    context(CascadeContext)
    override suspend fun execute() = cascadeBlock {
        // TODO wield repair arm
        reportHeal(activeActor, equipment, target)
        dealDirectEffect(target, equipment.directEffect, EffectModifiers.Empty, EffectReason.ByEntity(equipment))
        causeStatusEffects(target, equipment.causedStatusEffects)
        handleEquipment(equipment)
    }

    override fun estimateConsequences(): WorldStatistic<IntProperty> {
        val recorder = WorldStatisticRecorder<IntProperty>(world)
        recorder.incCount()
        executeCascade(recorder) {
            cascadeBlock {
                dealDirectEffect(target, equipment.directEffect, EffectModifiers.Empty, EffectReason.ByEntity(equipment))
                causeStatusEffects(target, equipment.causedStatusEffects)
                handleEquipment(equipment)
            }
        }
        return recorder.toWorldStatistic()
    }
}
