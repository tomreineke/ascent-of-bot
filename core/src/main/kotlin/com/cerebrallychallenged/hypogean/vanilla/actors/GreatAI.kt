package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.actions.talkingRange
import com.cerebrallychallenged.hypogean.vanilla.attributes.energy
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxEnergy
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxHealth
import com.cerebrallychallenged.hypogean.vanilla.behavior.GreatAIBehavior
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.jun.math.geo.vec

class GreatAI (initializer: Initializer) : Actor(initializer) {
    init {
        name = "Great AI"
        health = 200
        maxHealth = 200
        energy = 200
        maxEnergy = 200
        icon = Images.PortraitGreatAI
        behavior = GreatAIBehavior
        talkingRange = Float.MAX_VALUE // can talk with everyone everywhere
    }

    companion object {
        val GREAT_AI_CELL = vec(15, 15) // somewhere out of reach
    }
}