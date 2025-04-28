package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionObstacle
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.nearActors
import com.cerebrallychallenged.hypogean.model.base.Equipment
import com.cerebrallychallenged.hypogean.model.base.remainingCooldown
import com.cerebrallychallenged.hypogean.model.base.remainingSetupTime
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.rays.SightQuery
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.range

class ActionInstanceChecker(private val action: Action, private val actionTable: MutableActionTable) {
    fun check(block: ActionInstanceChecker.() -> Boolean): Boolean {
        return block()
    }

    /**
     * We need the [equipment] here because otherwise an ActionObstacle with a null-tool might be added.
     * This would lead to strange effects like an empty action bar button.
     */
    fun SightQuery.canSee(equipment: Equipment, target: LocatedEntity): Boolean {
        val result = of(target) != null
        if (!result) {
            actionTable.addObstacle(ActionObstacle(action, "Cannot see target", equipment, target))
        }
        return result
    }

    /**
     * We need the [equipment] here because otherwise an ActionObstacle with a null-tool might be added.
     * This would lead to strange effects like an empty action bar button.
     */
    fun Actor.otherActorsInRange(range: Float, equipment: Equipment): Sequence<Actor> {
        val result = nearActors(range).filter { it != this }
        if (result.none()) {
            actionTable.addObstacle(ActionObstacle(action, "No target in range ${equipment.range}", equipment))
        }
        return result
    }

    val Item.isSetupReady: Boolean
        get() {
            return remainingSetupTime?.let { remainingSetupTime ->
                actionTable.addObstacle(ActionObstacle(
                        action,
                        "${this.name} needs another $remainingSetupTime rounds for setup.",
                        equipment = this
                ))
                false
            } ?: true
        }

    val Item.isCooldownReady: Boolean
        get() {
            return remainingCooldown?.let { remainingCooldown ->
                actionTable.addObstacle(ActionObstacle(
                        action,
                        "${this.name} needs another $remainingCooldown rounds for cooldown.",
                        equipment = this
                ))
                false
            } ?: true
        }

    // broken weapons can be repaired in certain ways
    val Item.isNotBroken: Boolean
        get() {
            val result = health > 0
            if (!result) {
                actionTable.addObstacle(ActionObstacle(
                        action,
                        "${this.name} is broken",
                        equipment = this
                ))
            }
            return result
        }
}
