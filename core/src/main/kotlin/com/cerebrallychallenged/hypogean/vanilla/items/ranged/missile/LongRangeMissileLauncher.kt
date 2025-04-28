package com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.areaEffect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.effect.update
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.ExplosionDamage
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.RankBadge

open class LongRangeMissileLauncher(initializer: Initializer) : RocketLauncher(initializer) {
    init {
        name = "Long Range Missile Launcher"
        weight = 6.0f
        icon = Images.Minigun3
        initiativeCost = 4
        directEffect = Effect(1 of BluntDamage)
        areaEffect = areaEffect.update(30..40 of ExplosionDamage)
        range = 20.0f
    }
}

internal object LongRangeMissileLauncherAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<LongRangeMissileLauncher>()
    ) {
        icon = ActionIcons.Minigun
        badge(RankBadge, 3)
    }
})
