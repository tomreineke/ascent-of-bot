package com.cerebrallychallenged.hypogean.vanilla.items.utility

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.effect.EffectModifier
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.effect.providedPassiveEffectModifier
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.effects.ExplosionDamage
import com.cerebrallychallenged.hypogean.vanilla.items.Utility
import com.cerebrallychallenged.hypogean.vanilla.refs.Images

open class BasicExplosionProtector(initializer: Initializer) : Utility(initializer) {
    init {
        name = "Basic Explosion Protector"
        icon = Images.EnergyShield // FIXME[T] use proper image
        providedPassiveEffectModifier = EffectModifier(
            -10 of ExplosionDamage
        )
    }
}
