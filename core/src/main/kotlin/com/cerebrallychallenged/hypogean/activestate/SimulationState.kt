package com.cerebrallychallenged.hypogean.activestate

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.ChangeScheduleDto
import com.cerebrallychallenged.hypogean.model.Event
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.executeCascade
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.model.periodics

object SimulationState : ActiveWorldState() {
    internal fun World.simulateNextStep(): Sequence<ChangeScheduleDto> = sequence {
        if (iniQueue.isCurrentSlotEmpty) {
            executeCascade {
                for (entity in entities) {
                    for (periodic in entity.periodics) {
                        cascadeBlock {
                            periodic.execute(entity)
                        }
                    }
                }
            }
            iniQueue.incTime()
        } else {
            when(val iniHolder = iniQueue.dequeue()) {
                is Actor -> {
                    activeState = ActiveActorState(iniHolder)
                }
                is Event -> {
                    with(ActiveEventState(iniHolder)) {
                        activeState = this
                        yield(flush())
                        yieldAll(performEvent())
                    }
                }
                else -> modelError("Cannot handle iniHolder class ${iniHolder.javaClass}")
            }
        }
        yield(flush())
    }
}
