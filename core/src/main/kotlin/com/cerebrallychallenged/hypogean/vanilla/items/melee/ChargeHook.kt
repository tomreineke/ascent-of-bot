package com.cerebrallychallenged.hypogean.vanilla.items.melee

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.actions.GrapplingAttackAction
import com.cerebrallychallenged.hypogean.vanilla.actions.grapplingDirection
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.IniDamage
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip

open class ChargeHook(initializer: Initializer) : GrapplingHook(initializer) {

    init {
        name = "Charge Hook"
        icon = Images.HarpoonChain
        directEffect = Effect(
            1..4 of BluntDamage,
            3..5 of IniDamage
        )
        range = 3.0f
        explanatoryTooltip = "The $name pulls the player towards a target."
        grapplingDirection = GrapplingAttackAction.Direction.PULL_TO_TARGET
    }
}

internal object ChargeHookAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<ChargeHook>()
    ) {
        icon = ActionIcons.HarpoonChain
    }
})
