@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.props

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.effect.EffectKind
import com.cerebrallychallenged.hypogean.model.effect.any
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.visibleIfLostSight
import com.cerebrallychallenged.hypogean.vanilla.blocks.BlockMaterials
import com.cerebrallychallenged.hypogean.vanilla.cascade.effectImmunities
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.vanilla.refs.StarterContent
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

open class CellFloor(initializer: Initializer) : Prop(initializer) {
    init {
        visibleIfLostSight = true
        effectImmunities = any<EffectKind>()
    }
}

abstract class Asset_CellFloor(materialRef: UnrealRef<UMaterialInterface>) : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Hypogean.SM_CellFloor)
        materials[0] = load(materialRef)
    }
})

object Asset_ConcreteFloor : Asset_CellFloor(StarterContent.M_Concrete_Tiles)

open class CellFloor_Concrete(initializer: Initializer) : CellFloor(initializer) {
    init {
        asset = Asset_ConcreteFloor
    }
}

object Asset_DirtGroundFloor : Asset_CellFloor(BlockMaterials.MI_DirtGround)

open class CellFloor_DirtGround(initializer: Initializer) : CellFloor(initializer) {
    init {
        asset = Asset_DirtGroundFloor
    }
}

object Asset_SandstoneFloor : Asset_CellFloor(StarterContent.M_Rock_Sandstone)

open class CellFloor_Sandstone(initializer: Initializer) : CellFloor(initializer) {
    init {
        asset = Asset_SandstoneFloor
    }
}
