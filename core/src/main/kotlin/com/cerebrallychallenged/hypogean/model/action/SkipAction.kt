package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.view.report.report
import com.cerebrallychallenged.jun.log.log

object SkipAction : Action {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        addInstance(SkipActionInstance(activeActor))
    }

    override val category: ActionCategory = ActionCategory.Skip

    override val hint: String
        get() = "Skipping your turn will delay your next action by 1 round."
}

internal class SkipActionInstance(activeActor: Actor) : ActionInstance(SkipAction, activeActor) {
    override val equipment: Item = world.dummyEntity

    override val target: Entity = world.dummyEntity

    override val initiativeCost: InitiativeCost = InitiativeCost.Delta(1)

    context(CascadeContext)
    override suspend fun execute() {
        report(activeActor) {
            entityRef(activeActor)
            +" skips its turn."
        }
    }
}

val ActionTable.skipActionInstance: ActionInstance
    get() = groupedByAction[SkipAction].instances.firstOrNull() ?: modelError("Skip action not available")

fun ActionTable.warnAndSkip(warning: String): ActionInstance {
    log.warn { warning }
    return skipActionInstance
}
