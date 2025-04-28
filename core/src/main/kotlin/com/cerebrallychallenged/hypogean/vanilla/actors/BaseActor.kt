package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.vanilla.actions.quickMoveUsed
import com.cerebrallychallenged.hypogean.vanilla.attributes.isInfoViewAvailable
import com.cerebrallychallenged.hypogean.vanilla.cascade.showEnergyChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.cascade.showEnergyChangesWithOverheadText
import com.cerebrallychallenged.hypogean.vanilla.cascade.showHealthChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.cascade.showHealthChangesWithOverheadText
import com.cerebrallychallenged.hypogean.vanilla.cascade.showIniChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.cascade.showIniChangesWithOverheadText
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking

abstract class BaseActor(initializer: Initializer) : Actor(initializer) {
    init {
        quickMoveUsed = false
        showHealthChangesInDamageReport = true
        showHealthChangesWithOverheadText = true
        showEnergyChangesInDamageReport = true
        showEnergyChangesWithOverheadText = true
        showIniChangesInDamageReport = true
        showIniChangesWithOverheadText = true
        isInfoViewAvailable = true
        ballisticBlocking = BlockingValue { 1.0f }
        groundMovementBlocking = BlockingValue { 1.0f }
    }
}
