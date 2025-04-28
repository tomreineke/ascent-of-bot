package com.cerebrallychallenged.hypogean.pathfinding

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.diameter
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor

/**
 * Simple movement for actors.
 *
 * Note that this class is abstract because it shall be subclassed by data classes to be used as key in caching.
 */
abstract class SimpleActorMovementGraph(
        open val actor: Actor,
        blockerValueExtractor: BlockerValueExtractor
) : SimpleMovementGraph(
        blockerValueExtractor,
        actor,
        actor.diameter,
        1.0f,
        false
)
