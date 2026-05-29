package com.cerebrallychallenged.hypogean.vanilla.events.fireturret

import com.cerebrallychallenged.hypogean.model.Event
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.heading
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealDirectEffect
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.vanilla.refs.SkillIcons
import com.cerebrallychallenged.hypogean.view.map.events.ParticleEvent
import com.cerebrallychallenged.jun.math.degrees
import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Vec3f.Companion.UNIT_Y
import com.cerebrallychallenged.jun.math.geo.Vec3f.Companion.UNIT_Z
import com.cerebrallychallenged.jun.math.geo.times
import com.cerebrallychallenged.jun.math.geo.vec

class EmitFireFlares(initializer: Initializer) : Event(initializer) {
    init {
        name = "Fire flame event"
        icon = SkillIcons.Shamanskill_07
    }

    var fireTurretGroup: List<FireTurret> by attribute(listOf())

    context(CascadeContext)
    override suspend fun execute(): InitiativeCost = cascadeBlock {
        for (turret in fireTurretGroup) {
            val position = turret.position
            val heading = turret.heading
            val turretRange = turret.range
            for (actor in (0 until turretRange.floorToInt()).asSequence()
                .mapNotNull { world.cellAt(position + it * heading.delta)?.presentActor }
                .distinct()
            ) {
                dealDirectEffect(actor, turret.directEffect, EffectModifiers.Empty, EffectReason.ByEntity(turret))
            }
            val rotation = (
                    Quaternion.fromAxisAngle(UNIT_Y, -90.degrees)
                    * Quaternion.fromAxisAngle(UNIT_Z, 180.degrees)
                    * heading.rotation
            )
            world.notifyViewEvent(ParticleEvent(
                Hypogean.P_1_TorchFire_pt,
                duration = 2.0f,
                position = turret.centerPoint,
                rotation = rotation,
                scale = vec(5.5f, 8.0f * turretRange / 3.0f, 1.0f)
            ))
        }
        InitiativeCost.Delta(4)
    }
}
