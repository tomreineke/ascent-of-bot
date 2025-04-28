package com.cerebrallychallenged.hypogean.vanilla.refs

import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.UParticleSystem
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

object StarterContent {
    val M_Concrete_Poured = UnrealRef<UMaterialInterface>("Material'/Game/StarterContent/Materials/M_Concrete_Poured.M_Concrete_Poured'")
    val M_Concrete_Tiles = UnrealRef<UMaterialInterface>("Material'/Game/StarterContent/Materials/M_Concrete_Tiles.M_Concrete_Tiles'")
    val M_Rock_Sandstone = UnrealRef<UMaterialInterface>("Material'/Game/StarterContent/Materials/M_Rock_Sandstone.M_Rock_Sandstone'")
    val P_Fire = UnrealRef<UParticleSystem>("ParticleSystem'/Game/StarterContent/Particles/P_Fire.P_Fire'")
    val P_Sparks = UnrealRef<UParticleSystem>("ParticleSystem'/Game/StarterContent/Particles/P_Sparks.P_Sparks'")
}
