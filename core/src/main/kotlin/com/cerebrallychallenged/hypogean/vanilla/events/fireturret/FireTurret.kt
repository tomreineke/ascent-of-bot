@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.events.fireturret

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.HeadedProp
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.base.heading
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.assetParameterBindings
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.effects.FireDamage
import com.cerebrallychallenged.hypogean.vanilla.refs.ScienceLab.SM_Ventilation18
import com.cerebrallychallenged.hypogean.view.map.bindAssetParameters
import com.cerebrallychallenged.hypogean.view.map.boundTo
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeParameter
import com.cerebrallychallenged.jun.asset.sceneComponent
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.degrees
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec

object Asset_FireTurret : CompositeAsset({
    sceneComponent {
        staticMeshComponent {
            staticMesh = load(SM_Ventilation18)
            transform(translation = vec(-50.0f, 0.0f, 50.0f), scale = vec(1.0f, 0.15f, 0.5f)) {
                rotate(Vec3f.UNIT_Z, -90.degrees)
            }
        }
        Heading.bind {
            transform {
                rotate(Vec3f.UNIT_Z, it.angle)
            }
        }
    }
}) {
    object Heading : CompositeParameter<com.cerebrallychallenged.hypogean.Heading>
}

//object Asset_RoundVentilation : CompositeAsset({
//    staticMeshComponent {
//        staticMesh = load(SM_Ventilation23)
//        transform(translation = vec(-50.0, 0.0, 50.0), scale = vec(0.5, 0.02, 0.5)) {
//            rotate(Vec3d.UNIT_Z, (90).degrees)
//        }
//    }
//})

open class FireTurret(initializer: Initializer) : Prop(initializer), HeadedProp {
    init {
        name = "Fire turret"
        asset = Asset_FireTurret
        assetParameterBindings = bindAssetParameters(
                Asset_FireTurret.Heading boundTo FireTurret::heading
        )
        directEffect = Effect(
            10..15 of FireDamage
        )
        range = 6.0f
        height = 1.0f
    }
}
