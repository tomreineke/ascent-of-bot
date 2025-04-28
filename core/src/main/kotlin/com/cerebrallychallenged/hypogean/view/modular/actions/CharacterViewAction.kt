package com.cerebrallychallenged.hypogean.view.modular.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.ViewAction
import com.cerebrallychallenged.hypogean.model.action.ViewActionInstance

object CharacterViewAction : ViewAction() {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        with(activeActor.factionEntity ?: return) {
            for (actor in actors) {
                addInstance(CharacterViewActionInstance(activeActor, actor))
            }
        }
    }
}

class CharacterViewActionInstance(
    activeActor: Actor,
    override val target: Actor
) : ViewActionInstance(CharacterViewAction, activeActor)
