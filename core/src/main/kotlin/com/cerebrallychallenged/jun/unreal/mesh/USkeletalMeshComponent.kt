package com.cerebrallychallenged.jun.unreal.mesh

import com.cerebrallychallenged.jun.unreal.SkeletalMeshComponentLike
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class USkeletalMeshComponent(ptr: CPointer) : USkinnedMeshComponent(ptr), SkeletalMeshComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    override fun setSkeletalMesh(mesh: USkeletalMesh, reinitPose: Boolean) {
        setSkeletalMesh(ptr, mesh.ptr, reinitPose)
    }
}

private external fun setSkeletalMesh(ptr: CPointer, meshPtr: CPointer, reinitPose: Boolean)