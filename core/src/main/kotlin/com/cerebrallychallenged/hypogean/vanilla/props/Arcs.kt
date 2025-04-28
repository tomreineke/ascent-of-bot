@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.props

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.cellFilling
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.blocks.BlockMaterials
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.visibilityBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.Bunker
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

class Asset_ArcPart(materialRef: UnrealRef<UMaterialInterface>) : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Hypogean.SM_ArcPart)
        val material = load(materialRef)
        materials[0] = material
    }
})

open class Asset_Arc_DirectionY(
    materialRef: UnrealRef<UMaterialInterface>,
    freeBlocks: Int,
    topBlocks: Int
) : CompositeAsset({
    val part = Asset_ArcPart(materialRef)
    staticMeshComponent {
        val baseZ = 100.0f * freeBlocks + 70.0f
        link(part) {
            transform(translation = vec(0.0f, 5.0f, baseZ))
        }
        link(part) {
            transform(translation = vec(0.0f, -7.0f, baseZ)) { rotate(Vec3f.UNIT_Z, Angle.DEGREE_180) }
        }
        if (topBlocks > 0) {
            link(Asset_BaseBlocks(materialRef, topBlocks)) {
                transform(translation = vec(0.0f, 0.0f, 100.0f * (freeBlocks + 1)))
            }
        }
    }
})

open class Asset_Rotated90(base: CompositeAsset): CompositeAsset({
    link(base) {
        transform {
            rotate(Vec3f.UNIT_Z, Angle.DEGREE_90)
        }
    }
})

object Asset_BrickWallArc_DirectionY_2 : Asset_Arc_DirectionY(Bunker.M_brick, 0, 1)

object Asset_CaveWallArc_DirectionY_2 : Asset_Arc_DirectionY(BlockMaterials.MI_CaveWall, 0, 1)

object Asset_CaveWallArc_DirectionX_2 : Asset_Rotated90(Asset_CaveWallArc_DirectionY_2)

open class CellArc_CaveWallArc_DirectionX_2(initializer: Initializer) : CellBlock(initializer) {
    init {
        height = 2.0f
        asset = Asset_CaveWallArc_DirectionX_2
        cellFilling = false
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 0.2f }
        visibilityBlocking = BlockingValue { 0.2f }
    }
}

open class CellArc_CaveWallArc_DirectionY_2(initializer: Initializer) : CellBlock(initializer) {
    init {
        asset = Asset_CaveWallArc_DirectionY_2
        cellFilling = false
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 0.2f }
        visibilityBlocking = BlockingValue { 0.2f }
    }
}
