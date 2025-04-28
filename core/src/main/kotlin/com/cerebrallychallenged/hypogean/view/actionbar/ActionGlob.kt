package com.cerebrallychallenged.hypogean.view.actionbar

import com.cerebrallychallenged.hypogean.model.EntityType
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionMode
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.singleAction
import com.cerebrallychallenged.hypogean.model.action.singleCategory
import com.cerebrallychallenged.hypogean.model.action.singleMode
import com.cerebrallychallenged.hypogean.model.action.singleToolType

private const val CATEGORY_WEIGHT = 8
private const val ACTION_WEIGHT = 4
private const val TOOL_WEIGHT = 2
private const val MODE_WEIGHT = 1

internal data class ActionGlob(
        val category: ActionCategory? = null,
        val action: Action? = null,
        val tool: EntityType<Item>? = null,
        val mode: ActionMode? = null
) {
    fun matchingScore(actualGlob: ActionGlob): Int = (
            scoreBy(actualGlob, CATEGORY_WEIGHT) { category }
            + scoreBy(actualGlob, ACTION_WEIGHT) { action }
            + scoreBy(actualGlob, TOOL_WEIGHT) { tool }
            + scoreBy(actualGlob, MODE_WEIGHT) { mode }
    )

    private fun <T> scoreBy(other: ActionGlob, weight: Int, fn: ActionGlob.() -> T): Int = when (this.fn()) {
        other.fn() -> weight
        null -> 0
        else -> -weight
    }
}

internal fun ActionTable.toGlob() : ActionGlob = ActionGlob(
        singleCategory,
        singleAction,
        singleToolType,
        singleMode
)
