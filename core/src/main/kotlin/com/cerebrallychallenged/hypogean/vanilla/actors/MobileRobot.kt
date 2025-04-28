package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.vanilla.attributes.numberWeaponSlots
import com.cerebrallychallenged.hypogean.vanilla.items.ChassisSlot

abstract class MobileRobot(initializer: Initializer) : Robot(initializer) {

    init {
        numberWeaponSlots = 2
        initializer.defineSlot<ChassisSlot>("chassis") { }
    }
}
