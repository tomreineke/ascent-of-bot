package com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.LaserDamage
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.RankBadge

open class SmallPulseLaser(initializer: Initializer) : PulseLaser(initializer) {

    init {
        name = "Small Pulse Laser"
        weight = 1.0f
        icon = Images.LaserBlast1
        cooldown = 4
        initiativeCost = 2
        directEffect = Effect(16..21 of LaserDamage)
    }
}

internal object SmallPulseLaserAppearance : ActionButtonStylings({
    defineStyling(
            category = ActionCategory.Attack,
            tool = entityTypeOf<SmallPulseLaser>()
    ) {
        icon = ActionIcons.LaserBlast
        badge(RankBadge, 1)
    }
})
