@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.view.map.visualizers

import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.pathfinding.toCurve
import com.cerebrallychallenged.hypogean.vanilla.refs.BasicShapes
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeParameter
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.runtimeMeshComponentStatic
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.clipper.triangulate
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.curve.times
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.ECollisionEnabled
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionProperties
import com.cerebrallychallenged.jun.unreal.rmc.URuntimeMesh
import com.cerebrallychallenged.jun.unreal.rmc.castsShadow
import com.cerebrallychallenged.jun.unreal.rmc.isVisible
import com.cerebrallychallenged.jun.unreal.rmc.materialSlot
import com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStatic
import com.cerebrallychallenged.jun.unreal.rmc.wants32BitIndices

object Asset_Path : CompositeAsset({
    runtimeMeshComponentStatic {
        PathParameter.bind { (cellPath, materialRef) ->
            val curve = (cellPath.toCurve() * 100.0f).flatten(0.1f)
            val provider = newObject<URuntimeMeshProviderStatic>()
            runtimeMesh = newObject<URuntimeMesh>().apply {
                initialize(provider)
            }
            val meshSectionProperties = FRuntimeMeshSectionProperties.makeShared().apply {
                castsShadow = false
                isVisible = true
                materialSlot = 0
                wants32BitIndices = true
            }
            val meshData = curve.triangulate(7.0f, meshSectionProperties)
            provider.setupMaterialSlot(0, "Material", load(materialRef))
            provider.createSection(0, 0, meshSectionProperties, meshData)
        }
        relativeLocation = vec(0.0f, 0.0f, 10.0f)
        castShadow = false
        collisionEnabled = ECollisionEnabled.NoCollision
    }
}) {
    object PathParameter : CompositeParameter<Pair<CellPath, UnrealRef<UMaterialInterface>>>
}

object Asset_WaypointCone : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(BasicShapes.Cone)
        relativeScale3D = Vec3f.ONE * 0.60f
        relativeLocation = Vec3f.UNIT_Z * 30.0f
        castShadow = false
        collisionEnabled = ECollisionEnabled.NoCollision
    }
})

object Asset_BoxFrame : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Hypogean.SM_BoxFrame)
        relativeScale3D = vec(0.9f, 0.9f, 1.05f)
        castShadow = false
        collisionEnabled = ECollisionEnabled.NoCollision
    }
})
