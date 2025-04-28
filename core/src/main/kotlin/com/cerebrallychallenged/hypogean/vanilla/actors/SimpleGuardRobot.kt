@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.attributes.description
import com.cerebrallychallenged.hypogean.vanilla.attributes.dialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxHealth
import com.cerebrallychallenged.hypogean.vanilla.behavior.StandardBehavior
import com.cerebrallychallenged.hypogean.vanilla.cascade.showEnergyChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.ChassisSlot
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.StandardChassis
import com.cerebrallychallenged.hypogean.vanilla.items.melee.SmashArm
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.Laser
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.map.asset

open class SimpleGuardRobot(initializer: Initializer) : MobileRobot(initializer) {
    init {
        name = "Simple Guard Robot"
        height = 1.0f
        health = 15
        maxHealth = 15
        icon = Images.PortraitSimpleGuardRobot
        showEnergyChangesInDamageReport = false
        asset = SmallModularLowPolyRobotAsset3
        behavior = StandardBehavior
        description = "The $name from the DeepDrillCorp. It has a laser weapon and a smash arm."

        initializer.defineSlot<WeaponSlot>("left_arm") {
            insert(world.create(::Laser))
        }

        initializer.defineSlot<WeaponSlot>("right_arm") {
            insert(world.create(::SmashArm))
        }

        initializer.defineSlot<ChassisSlot>("chassis") {
            insert(world.create(::StandardChassis))
        }
    }

    override fun remove() {
        val protagonist = world.actors.first { it.faction is ProtagonistFaction }
        // Remove initial dialog from protagonist.
        protagonist.dialog = null
        super.remove()
    }
}
