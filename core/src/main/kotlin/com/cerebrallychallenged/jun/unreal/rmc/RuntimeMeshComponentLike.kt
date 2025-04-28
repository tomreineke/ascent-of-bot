package com.cerebrallychallenged.jun.unreal.rmc

import com.cerebrallychallenged.jun.unreal.mesh.MeshComponentLike
import com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProvider

interface RuntimeMeshComponentLike : MeshComponentLike {
    fun getOrCreateRuntimeMesh(): URuntimeMesh

    fun initialize(provider: URuntimeMeshProvider)

    val provider: URuntimeMeshProvider?

    var runtimeMesh: URuntimeMesh?

    var runtimeMeshMobility: ERuntimeMeshMobility
}