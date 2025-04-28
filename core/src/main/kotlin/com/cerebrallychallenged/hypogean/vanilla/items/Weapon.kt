package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.linguistics.Verb
import com.cerebrallychallenged.hypogean.linguistics.verb
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.Equipment

abstract class Weapon(initializer: Initializer) : Equipment(initializer) {
    init {
        verb = Verb("attack", "attacks", "attack")
    }
}
