package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.unreal.mesh.USkeletalMesh

interface SkeletalMeshComponentLike : SkinnedMeshComponentLike {
    fun setSkeletalMesh(mesh: USkeletalMesh, reinitPose: Boolean)
}