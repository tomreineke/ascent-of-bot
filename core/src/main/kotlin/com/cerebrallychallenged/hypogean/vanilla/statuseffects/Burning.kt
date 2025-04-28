package com.cerebrallychallenged.hypogean.vanilla.statuseffects

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.StatusEffectCompanion
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.vanilla.Asset_Fire
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.effects.FireDamage
import com.cerebrallychallenged.hypogean.vanilla.refs.SkillIcons

open class Burning(initializer: Initializer) : StatusEffect(initializer) {
    companion object : StatusEffectCompanion(::Burning, { intensity ->
        name = "Burning"
        icon = SkillIcons.Mageskill_18
        directEffect = Effect(
            intensity of FireDamage
        )
    })

    init {
        asset = Asset_Fire
    }
}
