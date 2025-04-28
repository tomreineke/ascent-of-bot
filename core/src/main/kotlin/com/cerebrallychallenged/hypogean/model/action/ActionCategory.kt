package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry

interface ActionCategory {
    companion object

    /**
     * Category only for [SkipAction].
     */
    object Skip : ActionCategory

    /**
     * Category only for [NullAction].
     */
    object Null : ActionCategory

    val id: String
        get() = this::class.qualifiedName!!
}

class ActionCategories : SimpleObjectRegistry<ActionCategory>()
