@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.vanilla.refs.ModularLowPolyRobots
import com.cerebrallychallenged.hypogean.view.FactionMaterial
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.geo.Vec3f

object Asset_Robot_BodySmall_Type1 : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ModularLowPolyRobots.SM_BodySmall_Type1)
        transform(scale = Vec3f.ONE * 0.2f)
        FactionMaterial.bind { materials[1] = it }
    }
})

object Asset_Robot_BodySmall_Type3 : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ModularLowPolyRobots.SM_BodySmall_Type3)
        transform(scale = Vec3f.ONE * 0.2f)
        FactionMaterial.bind { materials[1] = it }
    }
})

object Asset_Robot_BodySmall_Type4 : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ModularLowPolyRobots.SM_BodySmall_Type4)
        transform(scale = Vec3f.ONE * 0.2f)
        FactionMaterial.bind { materials[1] = it }
    }
})
