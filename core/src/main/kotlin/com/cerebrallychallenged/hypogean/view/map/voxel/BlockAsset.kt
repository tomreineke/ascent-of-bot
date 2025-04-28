package com.cerebrallychallenged.hypogean.view.map.voxel

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

abstract class BlockAsset(val roughness: Float, val subdivisionCount: Int, val material: UnrealRef<UMaterialInterface>)

class BlockAssets : SimpleObjectRegistry<BlockAsset>()

var Item.blockAsset: BlockAsset? by attribute(null)
