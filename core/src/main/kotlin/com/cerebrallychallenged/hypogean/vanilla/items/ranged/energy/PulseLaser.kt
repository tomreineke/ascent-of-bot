package com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.accuracy
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.LaserDamage
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_PulseLaser
import com.cerebrallychallenged.hypogean.vanilla.items.DirectShotWeapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.RankBadge

open class PulseLaser(initializer: Initializer) : DirectShotWeapon(initializer) {
    init {
        name = "Pulse Laser"
        weight = 2.0f
        icon = Images.LaserBlast2
        asset = Asset_PulseLaser
        cooldown = 6
        initiativeCost = 3
        directEffect = Effect(21..26 of LaserDamage)
        range = 8.0f
        accuracy = 1.0f
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(8)
    }
}

internal object PulseLaserAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<PulseLaser>()
    ) {
        icon = ActionIcons.LaserBlast
        badge(RankBadge, 2)
    }
})
