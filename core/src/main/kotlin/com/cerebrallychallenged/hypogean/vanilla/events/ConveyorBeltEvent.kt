@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.events

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Event
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.PROP_SLOT_NAME
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.map.events.EntityMoveEvent
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.Vec3f.Companion.ONE
import com.cerebrallychallenged.jun.math.geo.Vec3f.Companion.UNIT_X
import com.cerebrallychallenged.jun.math.geo.Vec3f.Companion.UNIT_Y
import com.cerebrallychallenged.jun.math.geo.vec
import kotlin.math.max

/**
 * movingCoordinatesForItems and movingCoordinatesForActors may be different, e.g. when some items shall disappear
 * inside a tunnel, whereas actors are supposed to be blocked there.
 */
var ConveyorBeltEvent.movingCoordinatesForItems: IntRange by attribute(0 .. 0)
var ConveyorBeltEvent.movingCoordinatesForActors: IntRange by attribute(0 .. 0)
var ConveyorBeltEvent.fixedCoordinate: Int by attribute(0)

/**
 * Can only be UNIT_X, -UNIT_X, UNIT_Y, and -UNIT_Y
 */
var ConveyorBeltEvent.direction: Vec3f by attribute(UNIT_X)
/**
 * delta distance covered per round.
 */
var ConveyorBeltEvent.delta: Int by attribute(1)

/**
 * Moves items on the conveyor belt in the starting room.
 */
open class ConveyorBeltEvent(initializer: Initializer) : Event(initializer) {
    init {
        name = "Conveyor Belt"
        icon = Images.ConveyorBelt
    }

    companion object {
        const val CONVEYOR_BELT_HEIGHT = 0.2f
        const val INI_DELTA = 1
        val excludedEntities: Array<String> = arrayOf("<unnamed>", "conveyor belt", "neon light", "Fire turret")
    }

    /**
     * Used to gather insert commands to be executed at a given time, in order to avoid runtime errors due to concurrent
     * modifications of cells.
     */
    private data class InsertItemCommand(
        val targetPosition: Vec2i,
        val item: Item
    )

    /**
     * Used to gather move commands to be executed at a given time, in order to avoid runtime errors due to concurrent
     * modifications of cells.
     */
    private data class MoveActorCommand(
        val targetPosition: Vec2i,
        val actor: Actor
    )

    context(CascadeContext)
    override suspend fun execute(): InitiativeCost {
        val moveEvents = mutableListOf<EntityMoveEvent?>()
        val insertItemCommands = mutableListOf<InsertItemCommand>()
        val moveActorCommands = mutableListOf<MoveActorCommand>()
        val moveVector = direction * delta
        // Depending on the move vector, which is either in x or in y direction, deltaComponent is the
        // length of the move vector in this direction.
        val deltaComponent = moveVector.dot(ONE).toInt()

        fun startPosition(i: Int): Vec2i =
            when (direction) {
                UNIT_X, -UNIT_X -> {
                    vec(i, fixedCoordinate)
                }
                UNIT_Y, -UNIT_Y -> {
                    vec(fixedCoordinate, i)
                }
                else -> {
                    error("Only directions x and y are supported!")
                }
            }

        fun targetPosition(i: Int): Vec2i =
            when (direction) {
                UNIT_X, -UNIT_X -> {
                    vec(i + deltaComponent, fixedCoordinate)
                }
                UNIT_Y, -UNIT_Y -> {
                    vec(fixedCoordinate, i + deltaComponent)
                }
                else -> {
                    error("Only directions x and y are supported!")
                }
            }

        // These loops gather all the commands and events to be executed after the loop has finished.
        // No immediate execution inside the loop to avoid runtime errors.
        for (i in movingCoordinatesForItems) {
            // create commands and events for items on conveyor belt
            world.cellAt(startPosition(i))?.slot(PROP_SLOT_NAME)?.containedItems?.forEach {
                if (!excludedEntities.contains(it.name)) {
                    moveEvents.add(createMoveEvent(it, moveVector))
                    insertItemCommands.add(InsertItemCommand(
                        targetPosition(i),
                        it
                    ))
                }
            }
        }
        for (i in movingCoordinatesForActors) {
            // create commands and events for actors on conveyor belt
            world.cellAt(startPosition(i))?.presentActor?.let {
                if (!excludedEntities.contains(it.name)) {
                    moveEvents.add(createMoveEvent(it, moveVector, it.heading, CONVEYOR_BELT_HEIGHT))
                    moveActorCommands.add(MoveActorCommand(
                        targetPosition(i),
                        it
                    ))
                }
            }
        }

        var duration = 0.0f
        for (insert in insertItemCommands) {
            world.cellAt(insert.targetPosition)?.slot(PROP_SLOT_NAME)?.insert(insert.item)
        }
        for (move in moveActorCommands) {
            move.actor.location = world.cellAt(move.targetPosition)
        }
        for (event in moveEvents.filterNotNull()) {
            world.notifyViewEvent(event)
            duration = max(duration, event.duration)
        }
        delay(duration)
        return InitiativeCost.Delta(INI_DELTA)
    }

    private fun createMoveEvent(
        it: LocatedEntity,
        direction: Vec3f,
        angle: Angle? = null,
        zShift: Float = 0.0f
    ): EntityMoveEvent? {
        val startingPosition = it.position.toFloat().append(zShift)
        val endPosition = startingPosition.plus(direction)
        return EntityMoveEvent.linearMove(
            it,
            startingPosition,
            endPosition,
            angle,
            speed = 0.5f
        )
    }
}
