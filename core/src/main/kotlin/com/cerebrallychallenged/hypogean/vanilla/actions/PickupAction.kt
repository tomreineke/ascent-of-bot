package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionObstacle
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.model.base.PROP_SLOT_NAME
import com.cerebrallychallenged.hypogean.model.base.equippedItems
import com.cerebrallychallenged.hypogean.model.base.inventory
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.executeCascade
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.containment.transitivelyContainedItems
import com.cerebrallychallenged.hypogean.model.diameter
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.pickupAble
import com.cerebrallychallenged.hypogean.model.trigger.intersectedCells
import com.cerebrallychallenged.hypogean.model.trigger.toSegments
import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.pathfinding.mergePaths
import com.cerebrallychallenged.hypogean.util.collections.WorldStatistic
import com.cerebrallychallenged.hypogean.util.collections.WorldStatisticRecorder
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.energy
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.cascade.handleEquipment
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.Chassis
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovement
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.OptimisticGroundMovement
import com.cerebrallychallenged.hypogean.view.map.events.EntityMoveEvent
import com.cerebrallychallenged.hypogean.view.report.report
import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.curve.Polyline
import kotlin.math.min

object PickupAction : Action {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        if (activeActor.quickMoveUsed) {
            addObstacle(ActionObstacle(PickupAction, "Quickmove already used"))
            return
        }
        val equippedChassis = activeActor.equippedItems.filterIsInstance<Chassis>().toList()
        if (equippedChassis.isEmpty()) {
            addObstacle(ActionObstacle(PickupAction, "No chassis equipped"))
            return
        }
        val world = activeActor.world
        val source = activeActor.location ?: return
        for (chassis in equippedChassis) {
            val actualRange = activeActor.rangeWith(chassis)
            val quickMoveRange = chassis.quickMoveRange
            val shortestPaths = world.shortestPath(OptimisticGroundMovement(activeActor)).from(source)
            // drop path when there is no move, i.e. start = target
            for (path in shortestPaths.allPaths(actualRange.toFloat()).drop(1)) {
                val isQuickMove = path.length < quickMoveRange
                val pickupAbleItems = path.target.slot(PROP_SLOT_NAME).containedItems.filter { it.pickupAble }
                for (item in pickupAbleItems) {
                    addInstance(PickupActionInstance(
                        activeActor,
                        chassis,
                        listOf(path),
                        item,
                        isQuickMove,
                        path.length.floorToInt()
                    ))
                }
            }
        }
    }

    override val category: ActionCategory = ActionCategory.PickUp

    override val hint: String = "Pick up"
}

internal class PickupActionInstance(
    activeActor: Actor,
    override val equipment: Chassis,
    override val paths: List<CellPath>,
    val itemToPickUp: Item,
    private val isQuickMove: Boolean,
    private val totalPathLength: Int
) : ActionInstance(PickupAction, activeActor), ActionInstanceWithPaths {
    override val target: Cell = paths.last().target

    /**
     * `initiativeCost` is independent of actual range of movement because otherwise the
     * sequencing for enemy actors could be strange (several small movements one after
     * another), while the hero has to wait for a long while until this is all done.
     */
    override val initiativeCost: InitiativeCost
            = if (isQuickMove) InitiativeCost.Delta(1) else InitiativeCost.Delta(equipment.initiativeCost)

    override val canBePartial: Boolean
        get() = true


    context(CascadeContext)
    override suspend fun execute() {
        val move = GroundMovement(activeActor)
        val mergedPath = paths.mergePaths()
        val pathSegments = mergedPath.toSegments(move.blockerValueExtractor, activeActor.diameter, activeActor).toList()
        require(pathSegments.isNotEmpty())

        val affectedEntities = mutableListOf<Entity>(activeActor)
        affectedEntities.addAll(pathSegments.intersectedCells())
        report(affectedEntities) {
            entityRef(activeActor)
            +" picks up ${itemToPickUp.name}."
        }

        if (ProtagonistFaction.reconOf(activeActor) == Recon.Visible) {
            val firstSegment = pathSegments.first()
            val initialRotation = Quaternion.fromNormalAxisAngle(Vec3f.UNIT_Z, activeActor.heading)
            val preRotationCurve = Polyline
                .from(initialRotation)
                .apply { speed = /*ROTATION_SPEED*/ 6.0f }
                .lineTo(firstSegment.rotationCurve.startPoint)
                .build()
            val prePositionCurve = Polyline.constant(firstSegment.positionCurve.startPoint, preRotationCurve.endTime)
            world.notifyViewEvent(EntityMoveEvent(activeActor, prePositionCurve, preRotationCurve))
            delay(prePositionCurve.endTime)
        }

        moveActorAlongPathSegments(activeActor, pathSegments, move)

        if (isQuickMove) {
            activeActor.quickMoveUsed = true
        }
        if (activeActor.isAlive) {
            // TODO: consider tool handling after each path segment
            cascadeBlock {
                handleEquipment(equipment, totalPathLength)
            }
            activeActor.factionEntity?.inventory()?.insert(itemToPickUp)
        }
    }

    override fun MutableActionTable.createChildren(activeActor: Actor) {
        val actualRange = when (val consumption = equipment.activeEnergyConsumption) {
            is ActiveEnergyConsumption.PerAction -> equipment.moveRange
            is ActiveEnergyConsumption.PerDistance ->
                min(equipment.moveRange, activeActor.energy / consumption.perMeter)
        }
        val quickMoveRange = equipment.quickMoveRange
        val remainingRange = actualRange - totalPathLength
        if (remainingRange <= 0) {
            return
        }
        val world = activeActor.world
        val shortestPaths = world.shortestPath(OptimisticGroundMovement(activeActor)).from(target)
        // drop path when there is no move, i.e. start = target
        for (path in shortestPaths.allPaths(remainingRange.toFloat()).drop(1)) {
            val totalPathLength = totalPathLength + path.length
            val pickupAbleItems = path.target.transitivelyContainedItems.flatMap { it.containedItems }.filter { it.pickupAble }
            for (item in pickupAbleItems) {
                addInstance(PickupActionInstance(
                    activeActor,
                    equipment,
                    paths + path,
                    item,
                    totalPathLength <= quickMoveRange,
                    totalPathLength.floorToInt()
                ))
            }
        }
    }

    override fun estimateConsequences(): WorldStatistic<IntProperty> {
        val recorder = WorldStatisticRecorder<IntProperty>(world)
        recorder.incCount()
        executeCascade(recorder) {
            cascadeBlock {
                handleEquipment(equipment, totalPathLength)
            }
        }
        return recorder.toWorldStatistic()
    }
}
