package com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.remainingUseCount
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.RankBadge

open class ReuseRocketLauncher(initializer: Initializer) : RocketLauncher(initializer) {

    init {
        name = "Reuse Rocket Launcher"
        icon = Images.Rocket2
        remainingUseCount = null
        cooldown = 3
        initiativeCost = 5
    }
}

internal object ReuseRocketLauncherAppearance : ActionButtonStylings({
    defineStyling(
            category = ActionCategory.Attack,
            tool = entityTypeOf<ReuseRocketLauncher>()
    ) {
        icon = ActionIcons.Rocket
        badge(RankBadge, 2)
    }
})
