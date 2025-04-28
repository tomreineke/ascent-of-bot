package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.linguistics.Verb
import com.cerebrallychallenged.hypogean.linguistics.verb
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.vanilla.attributes.hasAdjustableRange

abstract class DirectShotWeapon(initializer: Initializer) : Weapon(initializer) {
    init {
        verb = Verb("shoot", "shoots", "shoot")
        hasAdjustableRange = true
    }
}
