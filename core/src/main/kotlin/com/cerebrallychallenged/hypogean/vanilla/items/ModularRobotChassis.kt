@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.vanilla.refs.ModularLowPolyRobots
import com.cerebrallychallenged.hypogean.view.FactionMaterial
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.skeletalMeshComponent
import com.cerebrallychallenged.jun.math.geo.Vec3f

object Asset_TankChassis : CompositeAsset({
    skeletalMeshComponent {
        setSkeletalMesh(load(ModularLowPolyRobots.SM_TankChassis), false)
        transform(scale = Vec3f.ONE * 0.2f)
        FactionMaterial.bind { materials[1] = it }
    }
})

object Asset_HoverChassis : CompositeAsset({
    skeletalMeshComponent {
        setSkeletalMesh(load(ModularLowPolyRobots.SM_HoverChassis), false)
        transform(scale = Vec3f.ONE * 0.2f)
        FactionMaterial.bind { materials[1] = it }
    }
})

object Asset_WheelChassis : CompositeAsset({
    skeletalMeshComponent {
        setSkeletalMesh(load(ModularLowPolyRobots.SM_WheelChassis), false)
        transform(scale = Vec3f.ONE * 0.2f)
        FactionMaterial.bind { materials[1] = it }
    }
})
