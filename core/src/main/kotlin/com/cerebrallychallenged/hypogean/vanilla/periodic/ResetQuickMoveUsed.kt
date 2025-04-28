package com.cerebrallychallenged.hypogean.vanilla.periodic

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Periodic
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.vanilla.actions.quickMoveUsed

object ResetQuickMoveUsed : Periodic {
    context(CascadeBlock)
    override suspend fun execute(bearer: Entity) {
        for (actor in world.actors) {
            actor.quickMoveUsed = false
        }
    }
}
