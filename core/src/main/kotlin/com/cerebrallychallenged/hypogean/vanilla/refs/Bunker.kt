package com.cerebrallychallenged.hypogean.vanilla.refs

import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.unreal.mesh.UStaticMesh

object Bunker {
    val SM_box_medium = UnrealRef<UStaticMesh>("StaticMesh'/Game/Bunker/Meshes/SM_box_medium.SM_box_medium'")
    val SM_pipe_staff_coupling_small = UnrealRef<UStaticMesh>("StaticMesh'/Game/Bunker/Meshes/SM_pipe_staff_coupling_small.SM_pipe_staff_coupling_small'")
    val SM_platform_c = UnrealRef<UStaticMesh>("StaticMesh'/Game/Bunker/Meshes/SM_platform_c.SM_platform_c'")
    val M_brick = UnrealRef<UMaterialInterface>("Material'/Game/Bunker/Materials/M_brick.M_brick'")
}
