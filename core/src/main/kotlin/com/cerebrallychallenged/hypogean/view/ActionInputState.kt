package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionInstanceCompleteness
import com.cerebrallychallenged.hypogean.model.action.ActionTable

data class ActionInputState(
    val selected: ActionTable,
    val prefix: ActionInstance?,
    val originalPrefix: ActionInstance?,
    val hovered: ActionTable,
    val completeness: ActionInstanceCompleteness
) {
    companion object {
        val Empty = ActionInputState(
            ActionTable.Empty,
            null,
            null,
            ActionTable.Empty,
            ActionInstanceCompleteness.Complete
        )
    }

}
