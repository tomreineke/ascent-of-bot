package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.vanilla.attributes.range

abstract class MeleeWeapon(initializer: Initializer) : Weapon(initializer) {
    init {
        range = 1.5f
    }
}

