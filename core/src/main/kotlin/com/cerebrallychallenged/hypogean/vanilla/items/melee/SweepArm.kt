package com.cerebrallychallenged.hypogean.vanilla.items.melee

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.items.MeleeWeapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip

open class SweepArm(initializer: Initializer) : MeleeWeapon(initializer) {
    init {
        name = "Sweep Arm"
        icon = Images.CircleClaws
        weight = 3.0f
        cooldown = 7
        initiativeCost = 2
        directEffect = Effect(13..17 of BluntDamage)
        explanatoryTooltip = "Damages everything within a radius of 1m around the character."
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(3)
    }
}

internal object SweepArmAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<SweepArm>()
    ) {
        icon = ActionIcons.CircleClaws
    }
})
