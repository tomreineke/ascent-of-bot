package com.cerebrallychallenged.hypogean.vanilla.behavior

import com.cerebrallychallenged.hypogean.model.action.completeInstances
import com.cerebrallychallenged.hypogean.model.action.skipActionInstance
import com.cerebrallychallenged.hypogean.npc.Behavior
import com.cerebrallychallenged.hypogean.npc.NpcContext
import com.cerebrallychallenged.hypogean.util.math.probability.uniformDistribution
import com.cerebrallychallenged.hypogean.vanilla.actions.MoveAction

object RandomMoveBehavior : Behavior() {
    override suspend fun NpcContext.run() {
        while (true) {
            val groupedActions = availableActions.groupedByAction
            val instances = groupedActions[MoveAction].completeInstances.toList()
            submit(if (instances.isEmpty()) {
                availableActions.skipActionInstance
            } else {
                uniformDistribution(instances)(random)
            })
        }
    }
}
