package com.cerebrallychallenged.hypogean.vanilla.items.utility

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.base.Tool
import com.cerebrallychallenged.hypogean.model.effect.CausedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.causedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.effect.percentOfMax
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.rounds
import com.cerebrallychallenged.hypogean.vanilla.actions.Utility
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.Health
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.remainingUseCount
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.Healing
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.HealingOverTime
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip

open class RepairArm(initializer: Initializer) : Tool(initializer) {
    init {
        name = "Repair Arm"
        weight = 3.0f
        icon = Images.Repair
        remainingUseCount = 1
        explanatoryTooltip = "Can only be used $remainingUseCount times."
        directEffect = Effect(
            20 percentOfMax Health of Healing
        )
        causedStatusEffects = CausedStatusEffects(
            5 of HealingOverTime over 1.rounds
        )
        cooldown = 5
        initiativeCost = 2
        range = 1.5f
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(2)
    }
}

internal object RepairArmAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Utility,
        tool = entityTypeOf<RepairArm>()
    ) {
        icon = ActionIcons.AutoRepair
    }
})
