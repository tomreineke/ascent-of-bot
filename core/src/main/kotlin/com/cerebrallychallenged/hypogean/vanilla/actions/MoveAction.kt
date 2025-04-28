package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionObstacle
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.equippedItems
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.executeCascade
import com.cerebrallychallenged.hypogean.model.diameter
import com.cerebrallychallenged.hypogean.model.maps.EntitySet
import com.cerebrallychallenged.hypogean.model.trigger.CurvedMoveSegment
import com.cerebrallychallenged.hypogean.model.trigger.intersectedCells
import com.cerebrallychallenged.hypogean.model.trigger.toSegments
import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.pathfinding.SimpleActorMovementGraph
import com.cerebrallychallenged.hypogean.pathfinding.mergePaths
import com.cerebrallychallenged.hypogean.util.collections.WorldStatistic
import com.cerebrallychallenged.hypogean.util.collections.WorldStatisticRecorder
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.energy
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.vanilla.attributes.ignoreLocationAndHeading
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

var Actor.quickMoveUsed: Boolean by attribute(false)
var Actor.canSideStep: Boolean by attribute(true)
fun Actor.rangeWith(chassis: Chassis) = when (val consumption = chassis.activeEnergyConsumption) {
    is ActiveEnergyConsumption.PerAction -> chassis.moveRange
    is ActiveEnergyConsumption.PerDistance ->
        min(chassis.moveRange, this.energy / consumption.perMeter)
}

object MoveAction : Action {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        if (activeActor.quickMoveUsed) {
            addObstacle(ActionObstacle(MoveAction, "Quickmove already used"))
            return
        }
        val equippedChassis = activeActor.equippedItems.filterIsInstance<Chassis>().toList()
        if (equippedChassis.isEmpty()) {
            addObstacle(ActionObstacle(MoveAction, "No chassis equipped"))
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
                addInstance(MoveActionInstance(
                    activeActor,
                    chassis,
                    listOf(path),
                    isQuickMove,
                    path.length.floorToInt()
                ))
            }
        }
    }

    override val category: ActionCategory = ActionCategory.Move

    override val hint: String = "Move"
}

interface ActionInstanceWithPaths {
    val activeActor: Actor
    val paths: List<CellPath>
}

internal class MoveActionInstance(
    activeActor: Actor,
    override val equipment: Chassis,
    override val paths: List<CellPath>,
    private val isQuickMove: Boolean,
    private val totalPathLength: Int
) : ActionInstance(MoveAction, activeActor), ActionInstanceWithPaths {
    override val target: Cell = paths.last().target

    /**
     * `initiativeCost` is independent of actual range of movement because otherwise the
     * sequencing for enemy actors could be strange (several small movements one after
     * another), while the hero has to wait for a long while until this is all done.
     */
    override val initiativeCost: InitiativeCost
            = if (isQuickMove) InitiativeCost.KeepTurn else InitiativeCost.Delta(equipment.initiativeCost)

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
            +" moves."
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
        addInstance(MoveActionInstance(
            activeActor,
            equipment,
            paths,
            false,
            totalPathLength
        ))
        // drop path when there is no move, i.e. start = target
        for (path in shortestPaths.allPaths(remainingRange.toFloat()).drop(1)) {
            val totalPathLength = totalPathLength + path.length
            addInstance(MoveActionInstance(
                activeActor,
                equipment,
                paths + path,
                totalPathLength <= quickMoveRange,
                totalPathLength.floorToInt()
            ))
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

context(CascadeContext)
suspend fun moveActorAlongPathSegments(
    activeActor: Actor,
    pathSegments: List<CurvedMoveSegment>,
    move: SimpleActorMovementGraph,
) {
    activeActor.ignoreLocationAndHeading = true
    var lastCompletedSegment: CurvedMoveSegment? = null
    val processedTriggeredStatusEffects = EntitySet<StatusEffect>(world)
    for (segment in pathSegments) {
        val maxBlocker = segment.blockers.maxByOrNull { it.obstruction }
        if ((maxBlocker?.obstruction ?: 0.0f) >= move.obstructionThreshold) {
            report(activeActor) {
                entityRef(activeActor)
                +" was stopped by ${maxBlocker?.cell?.presentActor ?: "something"}."
            }
            // Set flag interruptedMove
            break
        }

        if (activeActor.isOwnOrAlliedFor(ProtagonistFaction) || segment.isVisibleFor(ProtagonistFaction)) {
            world.notifyViewEvent(EntityMoveEvent(activeActor, segment.positionCurve, segment.rotationCurve))
            delay(segment.positionCurve.endTime)
        }
        lastCompletedSegment = segment
        activeActor.location = segment.target
        world.updateRecon()
        cascadeBlock {
            for (blocker in segment.blockers) {
                val cell = blocker.cell ?: continue
                val triggeredStatusEffects = cell.nearStatusEffects.filter {
                    it !in processedTriggeredStatusEffects && it.isTriggeredBy(activeActor)
                }
                for (statusEffect in triggeredStatusEffects) {
                    statusEffect.executeTrigger(activeActor)
                }
                processedTriggeredStatusEffects.addAll(triggeredStatusEffects)
            }
        }

        if (!activeActor.isAlive) {
            break
        }
    }
    if (lastCompletedSegment != null) {
        activeActor.heading = lastCompletedSegment.finalHeading
    }
    activeActor.ignoreLocationAndHeading = false
}
