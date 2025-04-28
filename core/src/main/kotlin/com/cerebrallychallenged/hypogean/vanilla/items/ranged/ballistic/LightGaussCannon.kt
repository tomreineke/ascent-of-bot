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

open class LightGaussCannon(initializer: Initializer) : GaussCannon(initializer) {

    init {
        name = "Light Gauss Cannon"
        weight = 12.0f
        icon = Images.CannonShot1
        cooldown = 4
        initiativeCost = 2
        directEffect = Effect(
            7..9 of PiercingDamage
        )
        range = 10.0f
    }
}

internal object LightGaussCannonAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<LightGaussCannon>()
    ) {
        icon = ActionIcons.CannonShot
        badge(RankBadge, 1)
    }
})
