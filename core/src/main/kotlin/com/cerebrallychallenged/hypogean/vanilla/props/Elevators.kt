@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.props

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.refs.Bunker
import com.cerebrallychallenged.hypogean.vanilla.refs.ScienceLab
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.geo.vec

object Asset_Elevator : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Bunker.SM_platform_c)
        transform(scale = vec(0.385f, 0.333f, 1.0f))
    }
})

open class SimpleElevator(initializer: Initializer) : Prop(initializer) {
    init {
        name = "Elevator"
        asset = Asset_Elevator
        height = 0.1f
    }
}

object Asset_Button : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ScienceLab.SM_ControlPanel25)
    }
})

open class SimpleElevatorButton(initializer: Initializer) : Prop(initializer) {
    init {
        name = "Elevator button"
        asset = Asset_Button
        height = 0.2f
    }
}
