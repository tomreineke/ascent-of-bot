package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.adjacentLocations
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.Equipment
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.model.diameter
import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.pathfinding.MovementGraph
import com.cerebrallychallenged.hypogean.pathfinding.canDirectlyMove
import com.cerebrallychallenged.hypogean.rays.sight
import com.cerebrallychallenged.hypogean.vanilla.ActionWithAccuracy
import com.cerebrallychallenged.hypogean.vanilla.angleDistribution
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealWeaponEffects
import com.cerebrallychallenged.hypogean.vanilla.cascade.handleEquipment
import com.cerebrallychallenged.hypogean.vanilla.computeAccuracy
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.melee.GrapplingHook
import com.cerebrallychallenged.hypogean.vanilla.rays.VisibilityExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovement
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovementExtractor
import com.cerebrallychallenged.hypogean.view.report.reportHit
import java.util.EnumMap
import java.util.EnumSet

object GrapplingAttackAction : Action, ActionWithAccuracy {
    enum class Direction {
        PULL_TO_ACTOR,
        PULL_TO_TARGET
    }

    internal class Move(
            val direction: Direction,
            val activeActor: Actor,
            val target: Actor,
            val movedActor: Actor,
            val fromCell: Cell,
            val toCell: Cell
    ) {
        private val fromPosition = fromCell.position

        private val toPosition = toCell.position

        val distance: Float = activeActor.position.distanceTo(target.position)

        val length: Float = fromPosition.distanceTo(toPosition)
    }

    private fun createMove(
            activeActor: Actor,
            target: Actor,
            direction: Direction,
            toCell: Cell,
            movementGraph: MovementGraph
    ): Move? {
        val activeActorCell = activeActor.location ?: return null
        val targetCell = target.location ?: return null
        val move = when (direction) {
            Direction.PULL_TO_ACTOR -> Move(
                    direction,
                    activeActor,
                    target,
                    movedActor = target,
                    fromCell = targetCell,
                    toCell = toCell
            )
            Direction.PULL_TO_TARGET -> Move(
                    direction,
                    activeActor,
                    target,
                    movedActor = activeActor,
                    fromCell = activeActorCell,
                    toCell = toCell
            )
        }
        if (move.fromCell == move.toCell) return null
        if (!movementGraph.canDirectlyMove(move.fromCell, move.toCell)) return null
        //FIXME[TA] Discuss condition for melee obstructions
        return move
    }

    private fun findMove(activeActor: Actor, target: Actor, direction: Direction, movementGraph: MovementGraph): Move? {
        val toCells = when (direction) {
            Direction.PULL_TO_ACTOR -> activeActor.adjacentLocations(target.diameter)
            Direction.PULL_TO_TARGET -> target.adjacentLocations(activeActor.diameter)
        }

        return toCells.mapNotNull { toCell ->
            createMove(activeActor, target, direction, toCell, movementGraph)
        }.minByOrNull { it.length }
    }

    private fun potentialMoves(
            activeActor: Actor,
            checker: ActionInstanceChecker,
            equipment: Equipment,
            maxRange: Float,
            directions: Set<Direction>
    ): EnumMap<Direction, MutableList<Move>> {
        val sightQuery
                = activeActor.sight(VisibilityExtractor, sideStepMovement = GroundMovement(activeActor))
        val result = EnumMap<Direction, MutableList<Move>>(Direction::class.java)
        val activeActorMovement = GroundMovement(activeActor)
        for (target in with(checker) { activeActor.otherActorsInRange(maxRange, equipment) }) {
            if (checker.check { sightQuery.canSee(equipment, target) }) {
                val targetMovement = GroundMovement(target)
                for (direction in directions) {
                    val movementGraph = when (direction) {
                        Direction.PULL_TO_ACTOR -> targetMovement
                        Direction.PULL_TO_TARGET -> activeActorMovement
                    }
                    findMove(activeActor, target, direction, movementGraph)?.let { move ->
                        result.computeIfAbsent(direction) { mutableListOf() }.add(move)
                    }
                }
            }
        }
        return result
    }

    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val checker = ActionInstanceChecker(this@GrapplingAttackAction, this)
        val potentialTools = equipmentFromSlotAndOfType<WeaponSlot, GrapplingHook>(activeActor, checker).toList()
        val grapplingDirections = EnumSet.noneOf(Direction::class.java)
        val maxRange = potentialTools.asSequence().map {
            grapplingDirections.add(it.grapplingDirection)
            it.range
        }.maxOrNull() ?: 0.0f

        for (tool in potentialTools) {
            val movesByDirection = potentialMoves(activeActor, checker, tool, maxRange, grapplingDirections)
            for (move in movesByDirection[tool.grapplingDirection] ?: continue) {
                if (move.distance <= tool.range) {
                    addInstance(GrapplingAttackActionInstance(activeActor, tool, move))
                }
            }
        }
    }

    override val category: ActionCategory = ActionCategory.Attack
}

internal class GrapplingAttackActionInstance(
    activeActor: Actor,
    override val equipment: GrapplingHook,
    private val move: GrapplingAttackAction.Move
) : ActionInstance(GrapplingAttackAction, activeActor) {
    override val target: Actor = move.target

    override val initiativeCost: InitiativeCost = InitiativeCost.Delta(equipment.initiativeCost)

    context(CascadeContext)
    override suspend fun execute() = cascadeBlock {
        val path = CellPath(move.fromCell, listOf(move.toCell))
        move.movedActor.location = move.toCell
        if (move.direction == GrapplingAttackAction.Direction.PULL_TO_TARGET) {
            move.movedActor.heading = path.finalHeading
        }
        val activeActorPosition = activeActor.position
        val targetPosition = target.position

        val accuracy = GrapplingAttackAction.computeAccuracy(activeActor, equipment)
        val angle = angleDistribution((targetPosition - activeActorPosition).toDouble().angle(), accuracy)(world.random)

        // Find out what actually gets hit.
        // maxDistance of 1.42 is chosen so that
        // - diagonal neighbor cells can be hit (distance sqrt(2) < 1.42), and
        // - cells two away cannot be hit (distance > 1.5).
        val hitResult = world.queryRays(activeActorPosition, GroundMovementExtractor, activeActor)
            .computeHit(angle, Float.POSITIVE_INFINITY, 0.5f, 1.42f)

        //FIXME[Anim] emit GroundMove
//        val groundMove = GroundMove(
//                move.movedActor,
//                move.movedActor.heading,
//                path,
//                move.direction == GrapplingAttackAction.Direction.PULL_TO_TARGET
//        )
        val groundMoveTime = 0.0f
        delay(groundMoveTime)
        //FIXME[Anim] emit MeleeAction
//        val meleeAction = MeleeAction(attack, activeActor, angle)
        val meleeActionTime = 0.0f
        delay(meleeActionTime)
        dealWeaponEffects(equipment, hitResult)
        reportHit(activeActor, equipment, target, hitResult)
        handleEquipment(equipment)
    }
}

/**
 * Direction of the grappling attack performed by this grappling hook.
 */
var GrapplingHook.grapplingDirection: GrapplingAttackAction.Direction
        by attribute(GrapplingAttackAction.Direction.PULL_TO_ACTOR)
