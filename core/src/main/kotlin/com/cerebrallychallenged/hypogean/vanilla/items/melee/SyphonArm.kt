package com.cerebrallychallenged.hypogean.vanilla.items.melee

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
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.SyphonDamage
import com.cerebrallychallenged.hypogean.vanilla.items.MeleeWeapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings

open class SyphonArm(initializer: Initializer) : MeleeWeapon(initializer) {

    init {
        name = "Syphon Arm"
        icon = Images.SinusoidalBeam
        weight = 3.0f
        cooldown = 3
        initiativeCost = 3
        directEffect = Effect(
            7..9 of BluntDamage,
            9..11 of SyphonDamage
        )
    }
}


internal object SyphonArmAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<SyphonArm>()
    ) {
        icon = ActionIcons.SinusoidalBeam
    }
})
