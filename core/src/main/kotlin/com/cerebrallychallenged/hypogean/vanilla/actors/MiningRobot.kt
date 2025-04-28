@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.ToolSlot
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.hackingRange
import com.cerebrallychallenged.hypogean.vanilla.actions.talkingRange
import com.cerebrallychallenged.hypogean.vanilla.attributes.description
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.remainingUseCount
import com.cerebrallychallenged.hypogean.vanilla.items.ChassisSlot
import com.cerebrallychallenged.hypogean.vanilla.items.UtilitySlot
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.WheelChassis
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.Laser
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.RocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.items.utility.BasicFireProtector
import com.cerebrallychallenged.hypogean.vanilla.items.utility.RepairArm
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean.SM_Robo
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.view.map.SimpleActorAsset
import com.cerebrallychallenged.hypogean.view.map.asset
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.spotLightComponent
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.degrees
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.light.lumen

object Asset_MiningRobot : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Robo)
        spotLightComponent {
            lightIntensity = 500.lumen
            transform(translation = vec(20.0f, 0.0f, 50.0f)) {
                rotate(Vec3f.UNIT_Y, 30.degrees)
            }
        }
    }
})

object MiningRobotAsset : SimpleActorAsset(Asset_MiningRobot)

open class MiningRobot(initializer: Initializer) : MobileRobot(initializer) {
    init {
        name = "Mining Robot"
        height = 1.0f
        health = 100
        icon = Images.PortraitMiningRobot
        asset = SmallModularLowPolyRobotAsset1
        description = "The Mining Robot from the DeepDrillCorp. It usually doesn't have any weapons.\n" +
                "Hacking range: ${hackingRange}\n" +
                "Talking range: $talkingRange"

        initializer.defineSlot<WeaponSlot>("left_arm") { }

        initializer.defineSlot<WeaponSlot>("right_arm") { }

        initializer.defineSlot<ToolSlot>("head") { }

        initializer.defineSlot<UtilitySlot>("torso") { }

//      with all level 1 gear
//        initializer.defineSlot<WeaponSlot>("left_arm") {
//            insert(world.create(::Laser))
//        }
//
//        initializer.defineSlot<WeaponSlot>("right_arm") {
//            insert(world.create(::RocketLauncher).apply {
//                remainingUseCount = 5
//                explanatoryTooltip = "Can only be used $remainingUseCount times."
//            })
//        }
//
//        initializer.defineSlot<ToolSlot>("head") {
//            insert(world.create(::RepairArm))
//        }
//
//        initializer.defineSlot<UtilitySlot>("torso") {
//            insert(world.create(::BasicFireProtector))
//        }

        initializer.defineSlot<ChassisSlot>("chassis") {
            insert(world.create(::WheelChassis))
        }
    }
}
