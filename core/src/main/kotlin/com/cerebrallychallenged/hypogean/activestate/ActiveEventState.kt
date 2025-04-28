package com.cerebrallychallenged.hypogean.activestate

import com.cerebrallychallenged.hypogean.model.ChangeScheduleDto
import com.cerebrallychallenged.hypogean.model.Event
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.cascade.executeCascade
import com.cerebrallychallenged.hypogean.model.modelError

data class ActiveEventState(val activeEvent: Event) : ActiveWorldState() {
    internal fun World.performEvent(): Sequence<ChangeScheduleDto> = sequence {
        var initiativeCost: InitiativeCost = InitiativeCost.KeepTurn
        executeCascade {
            initiativeCost = activeEvent.execute()
        }
        yield(flush())
        activeState = SimulationState
        when (initiativeCost) {
            is InitiativeCost.Delta -> {
                iniQueue.enqueueRelative(initiativeCost.rounds, activeEvent)
            }
            is InitiativeCost.KeepTurn -> {
                modelError("Events cannot keep turn")
            }
            is InitiativeCost.Destroy -> {
                activeEvent.remove()
            }
        }
        yield(flush())
    }
}
