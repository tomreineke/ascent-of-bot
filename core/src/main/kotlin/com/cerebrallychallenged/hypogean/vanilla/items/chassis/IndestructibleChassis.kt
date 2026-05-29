package com.cerebrallychallenged.hypogean.vanilla.items.chassis

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.effect.EffectKind
import com.cerebrallychallenged.hypogean.model.effect.any
import com.cerebrallychallenged.hypogean.model.effectImmunities
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight

open class IndestructibleChassis(initializer: Initializer) : Chassis(initializer) {
    init {
        name = "Indestructible Chassis"
        weight = 10.0f
        effectImmunities = any<EffectKind>()
    }
}
