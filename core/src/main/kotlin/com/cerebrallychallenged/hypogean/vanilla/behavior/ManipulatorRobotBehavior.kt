package com.cerebrallychallenged.hypogean.vanilla.behavior

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.ModelException
import com.cerebrallychallenged.hypogean.npc.Behavior
import com.cerebrallychallenged.hypogean.npc.NpcContext
import com.cerebrallychallenged.hypogean.npc.NpcInteractions
import com.cerebrallychallenged.hypogean.npc.skipTurn
import com.cerebrallychallenged.hypogean.roundToHeading
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.jun.math.Angle

data class MoveActorToPosition(val targetActor: Actor, val targetCell: Cell)

/**
 * Returns a {@code MoveActorToPosition} object if
 * - there is a target actor of size 1 in the cell at which the manipulator robot (MR) is looking
 * - there is space in the cell next to the MR which opposite of the MR's current looking direction
 * E. g.
 * Actor <-- Manipulator robot
 *           Manipulator robot --> Actor
 *
 * Returns null if one of the conditions is not fulfilled.
 */
//FIXME Model by `Action` rather than `NpcInteractions`.
fun Actor.moveTargetActorFrom(angle: Angle): MoveActorToPosition? {
    val actorHeading = angle.roundToHeading()

    val lookingPosition = this.position + actorHeading.delta

    val targetActor = world.cellAt(lookingPosition)?.presentActor

    val targetPosition = this.position + (actorHeading.opposite().delta)
    val targetCell = world.cellByPosition[targetPosition]

    if (targetActor != null && targetCell != null) {
        return MoveActorToPosition(targetActor, targetCell)
    }
    return null
}

/**
 * Changes the heading of a given actor to its opposite.
 */
//FIXME Model by `Action` rather than `NpcInteractions`.
private fun NpcInteractions.changeHeadingToOpposite(actor: Actor) {
    val actorHeading = actor.heading.roundToHeading()
    if (actorHeading != null) {
        setAttribute(actor, actor.rulebook.attributes[Actor::heading], actorHeading.opposite().angle)
    } else {
        throw ModelException("Actor $this doesn't look in one of the four primary directions.")
    }
}

/**
 * Moves an actor to a target location as specified in the given {@code MoveActorToPosition} parameter.
 */
//FIXME Model by `Action` rather than `NpcInteractions`.
private fun NpcInteractions.moveActor(move: MoveActorToPosition) {
    val actor = move.targetActor
    setAttribute(actor, actor.rulebook.attributes[Actor::location], move.targetCell)
}

object ManipulatorRobotBehavior : Behavior() {
    override suspend fun NpcContext.run() {
        while (true) {
//        val move = activeActor.moveTargetActorFrom(activeActor.heading)
//        if (move != null) {
//            interactions.moveActor(move)
//        }
//        interactions.changeHeadingToOpposite(activeActor)
            skipTurn()
        }
    }
}
