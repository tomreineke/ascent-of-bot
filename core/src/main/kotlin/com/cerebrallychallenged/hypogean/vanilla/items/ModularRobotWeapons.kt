@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.vanilla.refs.ModularLowPolyRobots
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.geo.Vec3f

object Asset_MachineGun : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ModularLowPolyRobots.SM_MachineGunPlatform)
        staticMeshComponent("gunConnector") {
            declareSocket("muzzle")
            staticMesh = load(ModularLowPolyRobots.SM_MachineGun)
        }
        transform(scale = Vec3f.ONE * 0.4f)
    }
})

object Asset_Laser : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ModularLowPolyRobots.SM_LaserGunPlatform)
        staticMeshComponent("gunConnector") {
            declareSocket("muzzle")
            staticMesh = load(ModularLowPolyRobots.SM_LaserGun)
        }
        transform(scale = Vec3f.ONE * 0.4f)
    }
})

object Asset_PulseLaser : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ModularLowPolyRobots.SM_PlasmaGunPlatform)
        staticMeshComponent("gunConnector") {
            staticMesh = load(ModularLowPolyRobots.SM_PlasmaGun)
        }
        transform(scale = Vec3f.ONE * 0.4f)
    }
})
