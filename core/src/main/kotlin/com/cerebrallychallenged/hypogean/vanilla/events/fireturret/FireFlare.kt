@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.events.fireturret

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.assetParameterBindings
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.view.map.bindAssetParameters
import com.cerebrallychallenged.hypogean.view.map.boundTo
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeParameter
import com.cerebrallychallenged.jun.asset.particleSystemComponent
import com.cerebrallychallenged.jun.math.degrees
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec

object Asset_FireFlare : CompositeAsset({
    particleSystemComponent {
        template = load(Hypogean.P_1_TorchFire_pt)
        Range.bind {
            transform(translation = vec(0.0f, 0.0f, 50.0f), scale = vec(5.5f, 8.0f * it / 3.0f, 1.0f)) {
                rotate(Vec3f.UNIT_Y, -90.degrees)
                rotate(Vec3f.UNIT_Z, 180.degrees)
            }
        }
    }
}) {
    object Range : CompositeParameter<Float>
}

open class FireFlare(initializer: Initializer) : Prop(initializer) {
    init {
        name = "sparks"
        asset = Asset_FireFlare
        assetParameterBindings = bindAssetParameters(
                Asset_FireFlare.Range boundTo Item::range
        )
    }
}
