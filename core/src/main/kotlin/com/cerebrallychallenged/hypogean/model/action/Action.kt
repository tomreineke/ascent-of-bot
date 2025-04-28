package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell

interface Action {
    fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell)

    val category: ActionCategory

    val hint: String?
        get() = null

    val id: String
        get() = this::class.qualifiedName!!
}

class Actions : SimpleObjectRegistry<Action>()
