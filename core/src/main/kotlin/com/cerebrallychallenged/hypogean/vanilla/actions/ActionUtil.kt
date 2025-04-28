package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.Slot
import com.cerebrallychallenged.hypogean.model.action.*
import com.cerebrallychallenged.hypogean.model.base.Equipment
import com.cerebrallychallenged.hypogean.pathfinding.MovementGraph
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor
import com.cerebrallychallenged.hypogean.rays.sight
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.rays.VisibilityExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovement


/**
 * Returns a sequence of potentially available items to be used for this action by the specified actor [activeActor].
 * The condition is that they are equipped in a specified slot type [S] and belong to a specified tool type [T]
 * Whether they are actually applicable on a specific target, is yet to be determined by the caller.
 * Tools unfit for any target are added to the action table as [ActionObstacle]s by the [checker].
 */
inline fun <reified S : Slot, reified E : Equipment> equipmentFromSlotAndOfType(
        activeActor: Actor,
        checker: ActionInstanceChecker
): Sequence<E> {
    return activeActor.slots.asSequence()
            .filterIsInstance<S>()
            .flatMap { it.containedItems.asSequence() }
            .filterIsInstance<E>()
            .filter { tool ->
                checker.check {
                    tool.isSetupReady && tool.isCooldownReady && tool.isNotBroken
                }
            }
}

/**
 * Lists potential targets for [activeActor] in range [range], assuming he can do a side step [sideStep] (can be null),
 * and also a blocker value [extractor] that is in the way.
 */
fun potentialTargets(
        activeActor: Actor,
        assumedActiveActorLocation: Cell,
        checker: ActionInstanceChecker,
        tool: Equipment,
        range: Float,
        extractor: BlockerValueExtractor,
        sideStep: MovementGraph?,
        mayUseOnActiveActor: Boolean = false
): Sequence<LocatedEntity> {
    val sightQuery = activeActor.sight(extractor, sideStep, assumedActiveActorLocation)
    return (assumedActiveActorLocation.nearProps(range) // allow destruction of props, e.g. LandMine
                + assumedActiveActorLocation.nearCells(range)
                + assumedActiveActorLocation.nearActors(range)
    ).filter { target ->
        if (!mayUseOnActiveActor && (target == activeActor
            || target is Cell && target.position in activeActor.occupiedPositions(assumedActiveActorLocation)
        )) return@filter false
        // Large actors can be present in multiple cells at once.
        // An actor is a potential target, if it can be seen in any cell.
        // Otherwise, generate obstacle.
        checker.check {
            sightQuery.canSee(tool, target)
        }
    }
}

/**
 * Creates action instances that use tools in a given slot and adds them to [actionTable] assuming the methods
 * [equipmentFromSlotAndOfType] and [potentialTargets] are used for evaluation.
 */
inline fun <reified S : Slot, reified E : Equipment> MutableActionTable.createInstance(
    activeActor: Actor,
    assumedActiveActorLocation: Cell,
    action: Action,
    executableActionFactory:
        (activeActor: Actor, assumedActiveActorLocation: Cell, equipment: E, target: LocatedEntity) -> ActionInstance,
    mayUseOnActiveActor: Boolean = false,
    mayExecuteOn: LocatedEntity.() -> Boolean = { true }
){
    val checker = ActionInstanceChecker(action, this)
    val actorPosition = assumedActiveActorLocation.position
    val potentialTools = equipmentFromSlotAndOfType<S, E>(activeActor, checker).toList()
    if (potentialTools.isEmpty()) return
    val maxRange = potentialTools.asSequence().map { it.range }.maxOrNull() ?: 0.0f
    for (tool in potentialTools) {
        val potentialTargets = potentialTargets(
            activeActor,
            assumedActiveActorLocation,
            checker,
            tool,
            maxRange,
            VisibilityExtractor,
            if (activeActor.canSideStep) { GroundMovement(activeActor) } else null,
            mayUseOnActiveActor
        )
        for (target in potentialTargets) {
            if (target.mayExecuteOn()) {
                val distance = actorPosition.distanceTo(target.position)
                if (distance <= tool.range) {
                    addInstance(executableActionFactory(activeActor, assumedActiveActorLocation, tool, target))
                }
            }
        }
    }
}
