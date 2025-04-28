package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionObstacle
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.adjacentHittableActors
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.vanilla.ActionWithAccuracy
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealWeaponEffects
import com.cerebrallychallenged.hypogean.vanilla.computeAccuracy
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.melee.SweepArm
import com.cerebrallychallenged.hypogean.vanilla.pointBlankRangeProbability
import com.cerebrallychallenged.hypogean.vanilla.rays.BallisticExtractor
import com.cerebrallychallenged.hypogean.view.map.events.OverheadTextEvent
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

object RoundhouseKickAction : Action, ActionWithAccuracy {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val checker = ActionInstanceChecker(this@RoundhouseKickAction, this)
        val targets = activeActor.adjacentHittableActors(BallisticExtractor).filter {
            activeActor.factionRelationTo(it.faction) == Faction.Relation.HOSTILE
        }.toList()
        val potentialWeapons = equipmentFromSlotAndOfType<WeaponSlot, SweepArm>(activeActor, checker)
        if (potentialWeapons.any()) {
            if (targets.isNotEmpty()) {
                for (weapon in potentialWeapons) {
                    addInstance(RoundhouseKickActionInstance(activeActor, weapon, targets))
                }
            } else {
                addObstacle(ActionObstacle(RoundhouseKickAction, "No enemies nearby"))
            }
        }
    }

    override val category: ActionCategory = ActionCategory.Attack
}

class RoundhouseKickActionInstance(
    activeActor: Actor,
    override val equipment: SweepArm,
    private val targets: List<Actor>
) : ActionInstance(RoundhouseKickAction, activeActor) {
    override val initiativeCost: InitiativeCost = InitiativeCost.Delta(equipment.initiativeCost)

    override val target = equipment.world.dummyEntity

    context(CascadeContext)
    override suspend fun execute() = cascadeBlock {
        val random = world.random
        val accuracy = RoundhouseKickAction.computeAccuracy(activeActor, equipment)
        val hittingProbability = pointBlankRangeProbability(accuracy)

        //FIXME hitAnimation
        val hitAnimationTime = 0.0f
        delay(hitAnimationTime)
        for (target in targets) {
            val hit = random.nextDouble() < hittingProbability
            if (hit) {
                dealWeaponEffects(equipment, target.position, listOf(target))
            } else {
                if (ProtagonistFaction.reconOf(target) == Recon.Visible) {
                    world.notifyViewEvent(OverheadTextEvent(
                        target,
                        FLinearColor.Red,
                        "Miss"
                    ))
                }
            }
        }
    }
}
