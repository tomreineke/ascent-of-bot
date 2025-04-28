@file:Suppress("ClassName", "WrapUnaryOperator")

package com.cerebrallychallenged.hypogean.vanilla.props

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.Bunker
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.times
import com.cerebrallychallenged.jun.math.geo.vec

interface InventoryProp

object Asset_Chest : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Bunker.SM_box_medium)
        transform(scale = 0.5f * Vec3f.ONE)
    }
})

open class SimpleChest(initializer: Initializer) : Prop(initializer), InventoryProp {
    init {
        name = "Chest"
        asset = Asset_Chest
        height = 0.5f
        icon = Images.PortraitSimpleChest
        groundMovementBlocking = BlockingValue { 1.0f }
        providedBoxes = vec(5, 4)
    }
}
