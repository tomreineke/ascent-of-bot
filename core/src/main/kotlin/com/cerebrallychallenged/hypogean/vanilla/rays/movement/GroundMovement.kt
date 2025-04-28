package com.cerebrallychallenged.hypogean.vanilla.rays.movement

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.pathfinding.SimpleActorMovementGraph
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.SimpleBlockerValueExtractor

/**
 * How much does that entity impede the movement of objects passing on the ground?
 *
 * For instance, an actor cannot move through a solid wall.
 * Intermediate values model rough terrain, e.g., it is hard to move out of a pit.
 *
 * @see GroundMovementExtractor
 */
var Entity.groundMovementBlocking: BlockingValue by attribute(BlockingValue { 0.0f })

//TODO Maybe some actors can be run over?
//TODO Higher default value if we implement rough terrain.
object GroundMovementExtractor : SimpleBlockerValueExtractor(Entity::groundMovementBlocking)

data class GroundMovement(override val actor: Actor) : SimpleActorMovementGraph(actor, GroundMovementExtractor)
