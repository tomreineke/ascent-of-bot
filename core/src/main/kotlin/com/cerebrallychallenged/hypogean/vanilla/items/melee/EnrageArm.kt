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
import com.cerebrallychallenged.hypogean.vanilla.effects.IniDamage
import com.cerebrallychallenged.hypogean.vanilla.items.MeleeWeapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip

open class EnrageArm(initializer: Initializer) : MeleeWeapon(initializer) {

    init {
        name = "Enrage Arm"
        icon = Images.WolverineClaws
        weight = 3.0f
        cooldown = 1
        initiativeCost = 2
        directEffect = Effect(
            10..12 of BluntDamage,
            1 of IniDamage
        )

        explanatoryTooltip = "Every consecutive preceding strike with this weapon will reduce its initiative cost by 1."
        //FIXME reduce ini cost? How?
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(2)
    }
}

internal object EnrageArmAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<EnrageArm>()
    ) {
        icon = ActionIcons.WolverineClaws
    }
})
