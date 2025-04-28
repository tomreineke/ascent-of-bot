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
import com.cerebrallychallenged.hypogean.view.actionbar.TimeBadge

open class StunArm(initializer: Initializer) : MeleeWeapon(initializer) {
    init {
        name = "Stun Arm"
        icon = Images.ArmorPunchSandwatch
        weight = 3.0f
        cooldown = 5
        initiativeCost = 1
        directEffect = Effect(
            1..3 of BluntDamage,
            3 of IniDamage
        )
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(2)
    }
}


internal object StunArmAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<StunArm>()
    ) {
        icon = ActionIcons.ArmorPunch
        badge(TimeBadge)
    }
})
