package com.cerebrallychallenged.hypogean.vanilla.statuseffects

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.StatusEffectCompanion
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.vanilla.effects.Healing
import com.cerebrallychallenged.hypogean.vanilla.refs.SkillIcons

open class HealingOverTime(initializer: Initializer) : StatusEffect(initializer) {
    companion object : StatusEffectCompanion(::HealingOverTime, { intensity ->
        name = "Healing over time"
        icon = SkillIcons.Priestskill_23
        directEffect = Effect(
            intensity of Healing
        )
    })
}
