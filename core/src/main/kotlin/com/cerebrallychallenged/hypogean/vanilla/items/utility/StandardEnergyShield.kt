package com.cerebrallychallenged.hypogean.vanilla.items.utility

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.EffectModifier
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.effect.providedPassiveEffectModifier
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.Utility
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.passiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.PiercingDamage
import com.cerebrallychallenged.hypogean.vanilla.items.Utility
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings

open class StandardEnergyShield(initializer: Initializer) : Utility(initializer) {
    init {
        name = "Standard Energy Shield"
        icon = Images.EnergyShield
        passiveEnergyConsumption = 2
        providedPassiveEffectModifier = EffectModifier(
            -5 of BluntDamage,
            -5 of PiercingDamage
        )
    }
}

internal object StandardEnergyShieldAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Utility,
        tool = entityTypeOf<StandardEnergyShield>()
    ) {
        icon = ActionIcons.EnergyShield
    }
})
