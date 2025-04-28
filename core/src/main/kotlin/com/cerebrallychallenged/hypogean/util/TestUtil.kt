package com.cerebrallychallenged.hypogean.util

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.activestate.SimulationState
import com.cerebrallychallenged.hypogean.activestate.SimulationState.simulateNextStep
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.jun.util.consume

object TestContext {
    fun World.simulateUntilActivation() {
        while (activeState is SimulationState) {
            simulateNextStep().consume()
        }
    }

    fun World.executeAction(f: (ActiveActorState) -> ActionInstance) {
        with (activeState as ActiveActorState) {
            performAction(f(this).id).consume()
        }
    }
}

fun withTestingContext(f: TestContext.() -> Unit) {
    TestContext.f()
}