package com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.CausedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.causedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.rounds
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.FireDamage
import com.cerebrallychallenged.hypogean.vanilla.items.DirectShotWeapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.Burning
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings

open class FlameThrower(initializer: Initializer) : DirectShotWeapon(initializer) {
    init {
        name = "Flame Thrower"
        weight = 1.0f
        icon = Images.FlameClaws
        cooldown = 5
        initiativeCost = 2
        directEffect = Effect(
            1..2 of FireDamage,
        )
        causedStatusEffects = CausedStatusEffects(
            5 of Burning over 5.rounds
        )
        range = 2.0f
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(1)
    }
}

internal object FlameThrowerAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<FlameThrower>()
    ) {
        icon = ActionIcons.FlameClaws
    }
})
