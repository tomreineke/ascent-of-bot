@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean.SM_CellFloor
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeParameter
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

object Material : CompositeParameter<UMaterialInterface?>

object MaterialAsset : CompositeParameter<UnrealRef<UMaterialInterface>?>

object FactionMaterial : CompositeParameter<UMaterialInterface?>

object Time : CompositeParameter<Float>

object Visibility : CompositeParameter<Boolean>

object Asset_CellFloor : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_CellFloor)
        Material.bind { materials[0] = it }
    }
})

var LocatedEntity.materialAsset: UnrealRef<UMaterialInterface>? by attribute(null)

var World.globalDirectionalBrightness by attribute(0.5f)
