package com.cerebrallychallenged.hypogean.vanilla.items.utility

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.effect.EffectModifier
import com.cerebrallychallenged.hypogean.model.effect.percentOf
import com.cerebrallychallenged.hypogean.model.effect.providedPassiveEffectModifier
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.passiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.effects.FireDamage
import com.cerebrallychallenged.hypogean.vanilla.items.Utility
import com.cerebrallychallenged.hypogean.vanilla.refs.Images

open class BasicFireProtector(initializer: Initializer) : Utility(initializer) {
    init {
        name = "Basic Fire Protector"
        icon = Images.EnergyShield // FIXME[T] use proper image
        passiveEnergyConsumption = 1
        providedPassiveEffectModifier = EffectModifier(
            -40 percentOf FireDamage
        )
    }
}
