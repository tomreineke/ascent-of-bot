package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.pathfinding.toCurve
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealWeaponEffects
import com.cerebrallychallenged.hypogean.vanilla.cascade.handleEquipment
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.HomingRocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.rays.HomingObjectMovement
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.curve.toFollowCurve

object HomingShotAction : Action {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val checker = ActionInstanceChecker(this@HomingShotAction, this)
        val world = activeActor.world
        val source = activeActor.location ?: return
        val shortestPaths = world.shortestPath(HomingObjectMovement).from(source)
        for (weapon in equipmentFromSlotAndOfType<WeaponSlot, HomingRocketLauncher>(activeActor, checker)) {
            for (path in shortestPaths.allPaths(maxLength = weapon.range).drop(1)) {
                addInstance(HomingShotActionInstance(activeActor, weapon, path))
            }
        }
    }

    override val category: ActionCategory = ActionCategory.Attack
}

internal class HomingShotActionInstance(
    activeActor: Actor,
    override val equipment: HomingRocketLauncher,
    private val trajectory: CellPath
) : ActionInstance(HomingShotAction, activeActor) {
    override val target: Cell = trajectory.target

    override val initiativeCost: InitiativeCost = InitiativeCost.Delta(equipment.initiativeCost)

    context(CascadeContext)
    override suspend fun execute() = cascadeBlock {
        val shootingPosition = activeActor.position2f
        val targetPosition = trajectory.waypoints[0].position2f
        activeActor.heading = (targetPosition - shootingPosition).angle()
        //FIXME[Anim] emit ActorAim
//        val actorAim = ActorAim(activeActor, shootingPosition, targetPosition)
        val actorAimTime = 0.0f
        delay(actorAimTime)
        val positionCurve = trajectory.toCurve().reparameterizeAtUnitSpeed(0.01f, 1.0f)
        val rotationCurve = positionCurve.toFollowCurve(Vec3f.UNIT_Z).flatten(0.01f)
        //FIXME[Anim] emit ProjectileMove
//        world.notifyViewEvent(ProjectileMove(shot, positionCurve, rotationCurve))
        delay(positionCurve.endTime)
        dealWeaponEffects(equipment, target.position, listOf(target))
        handleEquipment(equipment)
    }
}
