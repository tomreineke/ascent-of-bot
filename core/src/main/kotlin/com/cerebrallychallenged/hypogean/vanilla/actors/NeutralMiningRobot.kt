package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.npc.SkipBehavior
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.items.ChassisSlot
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.HoverChassis
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.MachineGun
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.map.asset

open class NeutralMiningRobot(initializer: Initializer) : MobileRobot(initializer) {
    init {
        name = "Guard Unit 4BCi"
        icon = Images.PortraitFirstCompanion
        behavior = SkipBehavior
        asset = SmallModularLowPolyRobotAsset4

        initializer.defineSlot<WeaponSlot>("left_arm") {
            insert(world.create(::MachineGun))
        }

        initializer.defineSlot<ChassisSlot>("chassis") {
            insert(world.create(::HoverChassis))
        }
    }
}
