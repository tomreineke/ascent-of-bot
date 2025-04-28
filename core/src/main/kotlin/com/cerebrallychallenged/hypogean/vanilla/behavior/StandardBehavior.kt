package com.cerebrallychallenged.hypogean.vanilla.behavior

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionMember
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.skipActionInstance
import com.cerebrallychallenged.hypogean.npc.Behavior
import com.cerebrallychallenged.hypogean.npc.NpcContext
import com.cerebrallychallenged.hypogean.npc.SingleActionBehavior
import com.cerebrallychallenged.hypogean.npc.consideredActions
import com.cerebrallychallenged.hypogean.npc.evaluate
import com.cerebrallychallenged.hypogean.npc.gatherActions
import com.cerebrallychallenged.hypogean.npc.maybeShoutStandard
import com.cerebrallychallenged.hypogean.server.measureSuspending
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack

sealed class ConsideredAction(
    val action: ActionInstance,

    /**
     * Exposure from hostile actors.
     */
    val ownExposure: Map<Actor, Float>
) {
    abstract val firstAction: ActionInstance

    /**
     * Delta of iniTime until `action` can actually be performed.
     */
    abstract val delayBeforeAction: Int

    class SimpleAction(
        action: ActionInstance,
        ownExposure: Map<Actor, Float>
    ) : ConsideredAction(action, ownExposure) {
        override val firstAction: ActionInstance
            get() = action

        override val delayBeforeAction: Int
            get() = 0
    }

    class CombinedAction(
        action: ActionInstance,
        val moveAction: ActionInstance,
        ownExposure: Map<Actor, Float>
    ) : ConsideredAction(action, ownExposure) {
        override val firstAction: ActionInstance
            get() = moveAction

        override val delayBeforeAction: Int
            get() = moveAction.initiativeCost.rounds
    }

    class TakeCoverAction(
        val moveAction: ActionInstance,
        ownExposure: Map<Actor, Float>
    ) : ConsideredAction(moveAction, ownExposure) {
        override val firstAction: ActionInstance
            get() = moveAction

        override val delayBeforeAction: Int
            get() = moveAction.initiativeCost.rounds
    }
}

data class VisibleTarget(
    val target: Actor,
    val quickMove: ActionInstance?,
    val targetExposure: Float,
    val attacks: ActionTable?,
    val ownExposure: Map<Actor, Float>
)

abstract class StandardBaseBehavior : Behavior(), SingleActionBehavior {
    override suspend fun NpcContext.run() {
        while (true) {
            submitBestAction()
        }
    }

    override suspend fun NpcContext.submitBestAction() = measureSuspending("submitBestAction") {
        maybeShoutStandard()
        submit(
            select(consideredActions(::isRelevantAction))?.firstAction ?: availableActions.skipActionInstance
        )
    }

    open fun isRelevantAction(action: Action): Boolean = action.category == ActionCategory.Attack

    abstract fun NpcContext.select(consideredActions: List<ConsideredAction>): ConsideredAction?
}

object StandardBehavior : StandardBaseBehavior() {
    override fun NpcContext.select(consideredActions: List<ConsideredAction>): ConsideredAction? {
        val chosenAction = gatherActions(consideredActions).maxByOrNull { evaluate(it) }
        return chosenAction
    }
}

fun Entity.isUsefulTargetFor(ownFaction: Faction) =
        ((this is FactionMember) && (this.factionRelationTo(ownFaction) == Faction.Relation.HOSTILE)) ||
        ((this is Cell) && (this.presentActor?.factionRelationTo(ownFaction) == Faction.Relation.HOSTILE))
