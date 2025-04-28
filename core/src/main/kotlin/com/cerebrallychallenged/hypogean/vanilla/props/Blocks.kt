@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.props

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.cellFilling
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.visibleIfLostSight
import com.cerebrallychallenged.hypogean.vanilla.blocks.Asset_CaveWall
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.visibilityBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.Bunker
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.vanilla.refs.StarterContent
import com.cerebrallychallenged.hypogean.view.map.voxel.blockAsset
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.sceneComponent
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

open class CellBlock(initializer: Initializer) : Prop(initializer) {
    init {
        visibleIfLostSight = true
        cellFilling = true
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 1.0f }
        visibilityBlocking = BlockingValue { 1.0f }
    }
}

open class Asset_BaseBlock(materialRef: UnrealRef<UMaterialInterface>) : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Hypogean.SM_CellBlock)
        val material = load(materialRef)
        materials[0] = material
        materials[1] = material
    }
})

open class Asset_BaseBlocks(materialRef: UnrealRef<UMaterialInterface>, val height: Int) : CompositeAsset({
    val block = Asset_BaseBlock(materialRef)
    sceneComponent {
        for (i in 0 until height) {
            link(block) {
                transform(translation = vec(0.0f, 0.0f, 100.0f * i))
            }
        }
    }
})

object Asset_SandstoneBlock_2 : Asset_BaseBlocks(StarterContent.M_Rock_Sandstone, 2)

open class CellBlock_Sandstone_2(initializer: Initializer) : CellBlock(initializer) {
    init {
        name = "sandstone wall"
        height = 2.0f
        asset = Asset_SandstoneBlock_2
    }
}

open class CellBlock_CaveWall_2(initializer: Initializer) : CellBlock(initializer) {
    init {
        name = "cave wall"
        height = 2.0f
        blockAsset = Asset_CaveWall
    }
}

object Asset_BrickWallBlock : Asset_BaseBlock(Bunker.M_brick)

object Asset_BrickWallBlock_2 : Asset_BaseBlocks(Bunker.M_brick, 2)

open class CellBlock_BrickWall_2(initializer: Initializer) : CellBlock(initializer) {
    init {
        name = "brick wall"
        height = 2.0f
        asset = Asset_BrickWallBlock_2
    }
}
