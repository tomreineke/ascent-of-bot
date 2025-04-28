package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.npc.SkipBehavior
import com.cerebrallychallenged.hypogean.npc.behavior

open class Merchant(initializer: Initializer) : MiningRobot(initializer) {
    init {
        name = "Merchant"
        behavior = SkipBehavior
    }
}
