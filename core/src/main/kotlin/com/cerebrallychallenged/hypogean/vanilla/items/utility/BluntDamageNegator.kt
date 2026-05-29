package com.cerebrallychallenged.hypogean.vanilla.items.utility

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.effect.EffectKindSet
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.providedEffectImmunities
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.items.Utility
import com.cerebrallychallenged.hypogean.vanilla.refs.Images

open class BluntDamageNegator(initializer: Initializer) : Utility(initializer) {

    init {
        name = "Blunt Damage Negator"
        icon = Images.EnergyShield // FIXME[T] use proper image
        providedEffectImmunities = EffectKindSet.of(BluntDamage)
    }
}
