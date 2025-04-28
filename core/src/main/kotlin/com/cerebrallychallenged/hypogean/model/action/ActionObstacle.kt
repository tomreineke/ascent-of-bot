package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item

data class ActionObstacle(
    val action: Action,
    val description: String,
    val equipment: Item? = null,
    val target: Entity? = null,
    val mode: ActionMode? = null
)
