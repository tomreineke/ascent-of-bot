package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry

interface ActionMode {
    val id: String
        get() = this::class.qualifiedName!!
}

class ActionModes : SimpleObjectRegistry<ActionMode>()

object DefaultMode : ActionMode