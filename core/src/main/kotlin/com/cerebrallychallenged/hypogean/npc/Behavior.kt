package com.cerebrallychallenged.hypogean.npc

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.containment.transitivelyContainedItems
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.vanilla.behavior.MineAvoidingGroundMovement
import com.cerebrallychallenged.hypogean.vanilla.behavior.moveTo
import com.cerebrallychallenged.hypogean.vanilla.levels.test.ShootingTestLevel
import com.cerebrallychallenged.jun.log.log

var Actor.behavior: Behavior by attribute(SkipBehavior)

var Actor.behaviorBytes: ByteArray? by attribute(null)

abstract class Behavior {
    abstract suspend fun NpcContext.run()

    open suspend fun select(dialogSelection: Dialog.Select): Dialog.Select.Option {
        modelError("Behavior $this does not support dialog selection")
    }
}

class Behaviors : SimpleObjectRegistry<Behavior>()

object SkipBehavior : Behavior() {
    override suspend fun NpcContext.run() {
        while (true) {
            skipTurn()
        }
    }
}

interface SingleActionBehavior {
    suspend fun NpcContext.submitBestAction()
}

suspend fun NpcContext.submitBestActionUsing(behavior: SingleActionBehavior) {
    with(behavior) { submitBestAction() }
}

object MoveCirclesBehavior : Behavior() {
    override suspend fun NpcContext.run() {
        log.info { "Contained:" }
        for (item in activeActor.transitivelyContainedItems) {
            log.info { "- $item" }
        }

        while (true) {
            for (point in ShootingTestLevel.TestWayPoints) {
                while (!moveTo(world.cellAt(point)!!, MineAvoidingGroundMovement(activeActor))) {
                    skipTurn()
                }
            }
        }
    }
}
