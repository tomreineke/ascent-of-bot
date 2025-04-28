@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.diameter
import com.cerebrallychallenged.hypogean.model.effect.EffectKindSet
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.actions.hackingRange
import com.cerebrallychallenged.hypogean.vanilla.attributes.description
import com.cerebrallychallenged.hypogean.vanilla.attributes.energy
import com.cerebrallychallenged.hypogean.vanilla.attributes.flavorText
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxEnergy
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxHealth
import com.cerebrallychallenged.hypogean.vanilla.behavior.FirstBossBehavior
import com.cerebrallychallenged.hypogean.vanilla.cascade.effectImmunities
import com.cerebrallychallenged.hypogean.vanilla.cascade.showEnergyChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.ExplosionDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.FireDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.IniDamage
import com.cerebrallychallenged.hypogean.vanilla.items.ChassisSlot
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.IndestructibleChassis
import com.cerebrallychallenged.hypogean.vanilla.items.melee.GrapplingHook
import com.cerebrallychallenged.hypogean.vanilla.items.melee.SmashArm
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.LargeLaser
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.ReuseRocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.vanilla.refs.MediumMechStriker.MediumMechStrikerMasterPrefab_Satic
import com.cerebrallychallenged.hypogean.vanilla.triggers.ClosingDoorTrigger
import com.cerebrallychallenged.hypogean.view.map.SimpleActorAsset
import com.cerebrallychallenged.hypogean.view.map.asset
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.degrees
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec

object Asset_FirstBoss : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(MediumMechStrikerMasterPrefab_Satic)
        transform(scale = vec(0.1924f, 0.1924f, 0.0962f)) {
            rotate(Vec3f.UNIT_Z, -90.degrees)
        }
    }
})

object FirstBossAsset : SimpleActorAsset(Asset_FirstBoss)

/**
 * General: Boss draws enemies to it with a [GrapplingHook]. It generally uses melee attacks but has some special
 * abilities depending on its phase. It has a fire resistance of 40%. Its melee hits do 5 to 10 [BluntDamage].
 *
 * Phase 1 (100%):
 * With each successful melee attack the initiative cost for the following melee attack is decreased by 1 down to a
 * minimum of 1.
 *
 * Phase 2 (75%):
 * The boss receives a [ReuseRocketLauncher].
 *
 * Phase 3 (50%):
 * The boss loses the [ReuseRocketLauncher]. Then there is a sequence of the boss running to a terminal activating
 * a number fire cannons. These cannons appear all around the wall of the room. In a clockwise manner, four adjacent
 * cannons shoot each round inflicting 10 [FireDamage] to everything in range 2. Their health is 20, so they can be
 * destroyed. However, the hero can also go to the terminal and cause the cannons to shoot all at once each round.
 *
 * Phase 4 (25%):
 * The fire cannons explode dealing an 10 [ExplosionDamage] in a radius of 1 each.
 * The boss gains the ability to destroy every property it moves over (like conveyor belts). Also its [GrapplingHook]
 * now gains the ability to draw the boss towards the target doing 5 [BluntDamage] and 5 [IniDamage]. When the boss
 * is killed there is an explosion dealing 5 [ExplosionDamage] to everything in radius 1.
 */
open class FirstBoss(initializer: Initializer) : Robot(initializer) {

    init {
        diameter = 2
        name = "Cold Reaver"
        height = 2.0f
        icon = Images.PortraitFirstBoss
        showEnergyChangesInDamageReport = false
        asset = FirstBossAsset
        health = 120
        maxHealth = 120
        energy = 200
        maxEnergy = 200
        hackingRange = 3.0f
        behavior = FirstBossBehavior
        effectImmunities = EffectKindSet.of(FireDamage)
        description = "Cold Reaver is the last guardian of the mines. He will start using a rocket launcher for a while when " +
                "damaged enough. If he has taken serious damage he will enter an enrage mode."
        flavorText = "\"He needs to be swift and deadly, he needs to protect our interests in the Goria mine\", " +
                "CTO of Drilling Corp Inc."

        initializer.addStatusEffect<ClosingDoorTrigger>()

        initializer.defineSlot<WeaponSlot>("left_arm") {
            insert(world.create(::LargeLaser))
        }

        initializer.defineSlot<WeaponSlot>("right_arm") {
            insert(world.create(::SmashArm))
        }

        initializer.defineSlot<WeaponSlot>("left_shoulder") { }

        initializer.defineSlot<ChassisSlot>("chassis") {
            insert(world.create(::IndestructibleChassis))
        }
    }
}
