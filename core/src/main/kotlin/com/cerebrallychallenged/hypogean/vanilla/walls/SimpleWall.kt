@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.walls

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.cascade.showHealthChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.visibilityBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean.SM_SimpleWall
import com.cerebrallychallenged.hypogean.vanilla.refs.StarterContent.M_Concrete_Poured
import com.cerebrallychallenged.hypogean.vanilla.refs.StarterContent.M_Concrete_Tiles
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent

object Asset_SimpleWall : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_SimpleWall)
        val material = load(M_Concrete_Tiles)
        materials[0] = material
        materials[1] = material
        materials[2] = load(M_Concrete_Poured)
    }
})

open class SimpleWall(initializer: Initializer) : Prop(initializer) {
    init {
        asset = Asset_SimpleWall
        height = 1.0f
        name = "wall"
        showHealthChangesInDamageReport = true
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 1.0f }
        visibilityBlocking = BlockingValue { 1.0f }
    }
}
