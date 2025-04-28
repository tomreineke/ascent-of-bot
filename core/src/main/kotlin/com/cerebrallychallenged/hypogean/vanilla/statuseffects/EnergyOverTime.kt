package com.cerebrallychallenged.hypogean.vanilla.statuseffects

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.StatusEffectCompanion
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.vanilla.effects.EnergyCharging
import com.cerebrallychallenged.hypogean.vanilla.refs.SkillIcons

class EnergyOverTime(initializer: Initializer) : StatusEffect(initializer) {
    companion object : StatusEffectCompanion(::EnergyOverTime, { intensity ->
        name = "Energy over Time"
        icon = SkillIcons.Priestskill_23
        directEffect = Effect(
            intensity of EnergyCharging
        )
    })
}
