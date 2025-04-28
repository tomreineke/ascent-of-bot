package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.nearActors
import com.cerebrallychallenged.hypogean.model.base.Equipment
import com.cerebrallychallenged.hypogean.rays.sight
import com.cerebrallychallenged.hypogean.vanilla.ActionWithAccuracy
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.items.MeleeWeapon
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.melee.EnrageArm
import com.cerebrallychallenged.hypogean.vanilla.rays.VisibilityExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovement

// These melee weapons have a dedication action like EnrageAction in order to avoid
// that both MeleeAction and another dedicated action are associated to the weapon.
// Then we would need a radial menu for this, which makes no sense.
private val MELEE_WEAPONS_WITH_DEDICATED_ACTION = listOf(EnrageArm::class)

object MeleeAction : Action, ActionWithAccuracy {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val checker = ActionInstanceChecker(this@MeleeAction, this)
        val potentialTools = equipmentFromSlotAndOfType<WeaponSlot, MeleeWeapon>(activeActor, checker)
                .filter { !MELEE_WEAPONS_WITH_DEDICATED_ACTION.contains(it::class) }
                .toList()
        val sightQuery = activeActor.sight(VisibilityExtractor, sideStepMovement = GroundMovement(activeActor))

        for (tool in potentialTools) {
            for (target in activeActor.nearActors(tool.range)) {
                if (checker.check { sightQuery.canSee(tool, target) }) {
                    addInstance(MeleeActionInstance(activeActor, assumedActiveActorLocation, tool, target))
                }
            }
        }
    }

    override val category: ActionCategory = ActionCategory.Attack
}

class MeleeActionInstance(
    activeActor: Actor,
    assumedActiveActorLocation: Cell,
    equipment: Equipment,
    target: LocatedEntity
) : AdjacentActionInstance(activeActor, MeleeAction, assumedActiveActorLocation, false, equipment, target)
