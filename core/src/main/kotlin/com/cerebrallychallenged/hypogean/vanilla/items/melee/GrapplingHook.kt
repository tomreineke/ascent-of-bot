package com.cerebrallychallenged.hypogean.vanilla.items.melee

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.singleTool
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.actions.GrapplingAttackAction
import com.cerebrallychallenged.hypogean.vanilla.actions.grapplingDirection
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.BadgeGroup
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip

open class GrapplingHook(initializer: Initializer) : Weapon(initializer) {

    init {
        name = "Grappling Hook"
        icon = Images.GrapplingHook
        weight = 3.0f
        cooldown = 5
        initiativeCost = 1
        directEffect = Effect(3..7 of BluntDamage)
        range = 6.0f
        explanatoryTooltip = "The $name pulls the target next to the character."
        grapplingDirection = GrapplingAttackAction.Direction.PULL_TO_ACTOR
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(2)
    }
}

val GrapplingAttackDirectionBadge = BadgeGroup(
    "grapplingDirection",
    GrapplingAttackAction.Direction.PULL_TO_ACTOR to "In",
    GrapplingAttackAction.Direction.PULL_TO_TARGET to "Out"
)

internal object GrapplingHookAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<GrapplingHook>()
    ) {
        icon = ActionIcons.Grapple
        val tool = actionTable.singleTool
        if (tool is GrapplingHook) {
            badge(GrapplingAttackDirectionBadge, tool.grapplingDirection)
        }
    }
})
