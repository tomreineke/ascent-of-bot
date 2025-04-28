@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.ToolSlot
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.attributes.energy
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxEnergy
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxHealth
import com.cerebrallychallenged.hypogean.vanilla.behavior.StandardBehavior

abstract class Robot(initializer: Initializer) : BaseActor(initializer) {
    init {
        height = 1.0f
        health = 100
        maxHealth = 100
        energy = 100
        maxEnergy = 100
        behavior = StandardBehavior

        initializer.defineSlot<ToolSlot>("head") {}
    }
}
