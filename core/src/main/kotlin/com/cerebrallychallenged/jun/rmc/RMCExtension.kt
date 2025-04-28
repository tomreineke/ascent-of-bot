package com.cerebrallychallenged.jun.rmc

import com.cerebrallychallenged.jun.JunManagerExtension
import com.cerebrallychallenged.jun.WrapperIncubator
import com.cerebrallychallenged.jun.unreal.rmc.*
import com.cerebrallychallenged.jun.unreal.rmc.provider.*

@Suppress("unused") // Referenced by C++ code.
object RMCExtension : JunManagerExtension {
    override fun WrapperIncubator.declareWrappers() {
        wrapper(::ARuntimeMeshActor)
        wrapper(::URuntimeMesh)
        wrapper(::URuntimeMeshComponent)
        wrapper(::URuntimeMeshComponentStatic)
        wrapper(::URuntimeMeshStaticMeshConverter)
        wrapper(::URuntimeMeshProvider)
        wrapper(::URuntimeMeshProviderBox)
        wrapper(::URuntimeMeshProviderCollisionFromRenderable)
        wrapper(::URuntimeMeshProviderMemoryCache)
        wrapper(::URuntimeMeshProviderNormals)
        wrapper(::URuntimeMeshProviderPlane)
        wrapper(::URuntimeMeshProviderSphere)
        wrapper(::URuntimeMeshProviderStatic)
        wrapper(::URuntimeMeshProviderStaticMesh)
    }
}