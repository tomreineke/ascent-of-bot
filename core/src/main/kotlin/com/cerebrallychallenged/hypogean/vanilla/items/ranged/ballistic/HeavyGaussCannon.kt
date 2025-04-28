package com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic

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
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.PiercingDamage
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.RankBadge

open class HeavyGaussCannon(initializer: Initializer) : GaussCannon(initializer) {
    init {
        name = "Heavy Gauss Cannon"
        weight = 18.0f
        icon = Images.CannonShot3
        cooldown = 10
        initiativeCost = 5
        directEffect = Effect(
            23..27 of PiercingDamage
        )
        range = 5.0f
    }
}

internal object HeavyGaussCannonAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<HeavyGaussCannon>()
    ) {
        icon = ActionIcons.CannonShot
        badge(RankBadge, 3)
    }
})
