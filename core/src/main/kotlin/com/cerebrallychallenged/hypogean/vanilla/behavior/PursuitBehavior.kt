package com.cerebrallychallenged.hypogean.vanilla.behavior

import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.npc.NpcContext
import com.cerebrallychallenged.hypogean.npc.evaluate
import com.cerebrallychallenged.hypogean.npc.gatherActions
import com.cerebrallychallenged.hypogean.pathfinding.CellPath

object PursuitBehavior : StandardBaseBehavior() {

    override fun NpcContext.select(consideredActions: List<ConsideredAction>): ConsideredAction? {
        val chosenAction = gatherActions(consideredActions).maxByOrNull {
            evaluate(it) {
                var distanceScore = 0.0f
                for (actor in locatedActors) {
                    // minimize the distance to target actor
                    val distance = CellPath(activeActor.checkedLocation, listOf(actor.checkedLocation)).length
                    if (actor.factionRelation == Faction.Relation.HOSTILE) {
                        distanceScore -= distance
                    }
                }
                distanceScore * 100000f // heavy weight penalty
            }
        }

        return chosenAction
    }
}
