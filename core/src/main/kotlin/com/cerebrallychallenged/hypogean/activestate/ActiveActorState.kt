package com.cerebrallychallenged.hypogean.activestate

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.ChangeScheduleDto
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.action.ActionHistoryEntry
import com.cerebrallychallenged.hypogean.model.action.ActionInstanceId
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.actionHistory
import com.cerebrallychallenged.hypogean.model.action.maxIniDelta
import com.cerebrallychallenged.hypogean.model.cascade.executeCascade
import com.cerebrallychallenged.hypogean.model.modelError

data class ActiveActorState(
    val activeActor: Actor,
    val availableActions: ActionTable =
        activeActor.world.actions.computeAvailableActions(activeActor, activeActor.checkedLocation),
    val id: Long = activeActor.world.random.nextLong()
) : ActiveWorldState() {
    override fun activate() {
        // Create all slots where the active actor could land in after performing any of the available actions.
        // While not strictly necessary for the model,
        // this simplifies the code for views visualizing the InitiativeCost of action instances.
        activeActor.world.iniQueue.slot(availableActions.maxIniDelta)
    }

    fun recompute(): ActiveActorState = ActiveActorState(activeActor)

    /**
     * Executes the specified action instance.
     * Must only be called in the primary world.
     */
    internal fun World.performAction(selectedActionInstanceId: ActionInstanceId): Sequence<ChangeScheduleDto> = sequence {
        val activeActor = this@ActiveActorState.activeActor
        val actionInstance = availableActions.actionById(selectedActionInstanceId)
        if (!actionInstance.canBeComplete) {
            modelError("Performed action instance must be complete")
        }
        executeCascade {
            actionInstance.execute()
        }
        if (activeState is ActiveDialog) {
            yield(flush())
            return@sequence
        }
        updateRecon()
        activeActor.actionHistory += ActionHistoryEntry(
                currentIniTime,
                actionInstance.action,
                actionInstance.equipment,
                actionInstance.target
        )
        yield(flush())
        if (activeActor.isAlive) {
            activeState = SimulationState
            when (val initiativeCost = actionInstance.initiativeCost) {
                is InitiativeCost.Delta -> {
                    iniQueue.enqueueRelative(initiativeCost.rounds, activeActor)
                }
                is InitiativeCost.KeepTurn -> {
                    activeState = ActiveActorState(activeActor)
                }
                else -> {}
            }
            yield(flush())
        } else {
            assert(activeState == SimulationState)
        }
    }

    internal fun expandAction(selectedActionInstanceId: ActionInstanceId): ActionTable {
        val instance = availableActions.actionById(selectedActionInstanceId)
        if (!instance.canBePartial) {
            modelError("Expanded action instance must be partial")
        }
        return instance.expand()
    }
}
