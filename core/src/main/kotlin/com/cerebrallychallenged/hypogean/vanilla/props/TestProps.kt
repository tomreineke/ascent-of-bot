@file:Suppress("ClassName", "WrapUnaryOperator")

package com.cerebrallychallenged.hypogean.vanilla.props

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.transform
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.unreal.mesh.UStaticMesh

object Foo {
    val SM_Fx_Sphere = UnrealRef<UStaticMesh>("StaticMesh'/Game/sA_ShootingVfxPack/Meshes/SM_Fx_Sphere.SM_Fx_Sphere'")

    val M_Metall = UnrealRef<UMaterialInterface>("Material'/Game/ManipulatorRobot/Materials/M_Metall.M_Metall'")
}

object Asset_Sphere : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Foo.SM_Fx_Sphere)
        materials[0] = load(Foo.M_Metall)
    }
})

class SphereProp(initializer: Initializer) : Prop(initializer) {
    init {
        asset = Asset_Sphere
        transform = Transform3f.translation(vec(0.0f, 0.0f, 200.0f))
    }
}
