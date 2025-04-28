package com.cerebrallychallenged.hypogean.vanilla.refs

import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.UParticleSystem
import com.cerebrallychallenged.jun.unreal.font.UFont
import com.cerebrallychallenged.jun.unreal.material.UMaterial
import com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceConstant
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.unreal.mesh.UStaticMesh

object Hypogean {
    val M_AttackRegion = UnrealRef<UMaterialInterface>("Material'/Game/Hypogean/Materials/Region/M_AttackRegion.M_AttackRegion'")

    val M_ArrowFlow = UnrealRef<UMaterialInterface>("Material'/Game/Hypogean/Materials/M_ArrowFlow.M_ArrowFlow'")

    val M_IndicatorMaterial = UnrealRef<UMaterialInterface>("Material'/Game/Hypogean/Materials/M_IndicatorMaterial.M_IndicatorMaterial'")

    val M_Outlines = UnrealRef<UMaterialInterface>("Material'/Game/Hypogean/Materials/Outlines/M_Outlines.M_Outlines'")

    val M_PulsatingIndicator = UnrealRef<UMaterialInterface>("Material'/Game/Hypogean/Materials/M_PulsatingIndicator.M_PulsatingIndicator'")

    /**
     * Used for a region on top of another region, e.g., quick move or area of effect.
     */
    val M_ExtraRegion = UnrealRef<UMaterialInterface>("/Script/Engine.Material'/Game/Hypogean/Materials/Region/M_ExtraRegion.M_ExtraRegion'")

    val M_Ray = UnrealRef<UMaterial>("/Script/Engine.Material'/Game/Hypogean/Materials/M_Ray.M_Ray'")

    val M_Region = UnrealRef<UMaterialInterface>("Material'/Game/Hypogean/Materials/Region/M_Region.M_Region'")

    val M_UnknownGrid = UnrealRef<UMaterialInterface>("Material'/Game/Hypogean/Materials/M_UnknownGrid.M_UnknownGrid'")

    val MI_Laser = UnrealRef<UMaterialInstanceConstant>("/Script/Engine.MaterialInstanceConstant'/Game/Hypogean/Materials/MI_Laser.MI_Laser'")

    val MI_PulsatingAction = UnrealRef<UMaterialInterface>("MaterialInstanceConstant'/Game/Hypogean/Materials/MI_PulsatingAction.MI_PulsatingAction'")

    val MI_PulsatingAttack = UnrealRef<UMaterialInterface>("MaterialInstanceConstant'/Game/Hypogean/Materials/MI_PulsatingAttack.MI_PulsatingAttack'")

    val MatineeCam_SM = UnrealRef<UStaticMesh>("StaticMesh'/Engine/EditorMeshes/MatineeCam_SM.MatineeCam_SM'")

    val P_1_TorchFire_pt = UnrealRef<UParticleSystem>("ParticleSystem'/Game/Hypogean/Particles/P_1_TorchFire_pt.P_1_TorchFire_pt'")

    val P_Explosion = UnrealRef<UParticleSystem>("ParticleSystem'/Game/StarterContent/Particles/P_Explosion.P_Explosion'")

    val SM_ActiveActorIndicator = UnrealRef<UStaticMesh>("StaticMesh'/Game/Hypogean/Models/SM_ActiveActorIndicator.SM_ActiveActorIndicator'")

    val SM_BoxFrame = UnrealRef<UStaticMesh>("StaticMesh'/Game/Hypogean/Models/SM_BoxFrame.SM_BoxFrame'")

    val SM_CellBlock = UnrealRef<UStaticMesh>("StaticMesh'/Game/Hypogean/Models/SM_CellBlock.SM_CellBlock'")

    val SM_ArcPart = UnrealRef<UStaticMesh>("StaticMesh'/Game/Hypogean/Models/SM_ArcPart.SM_ArcPart'")

    val SM_CellFloor = UnrealRef<UStaticMesh>("StaticMesh'/Game/Hypogean/Models/SM_CellFloor.SM_CellFloor'")

    val SM_CineCam = UnrealRef<UStaticMesh>("StaticMesh'/Engine/EditorMeshes/Camera/SM_CineCam.SM_CineCam'")

    val SM_ConveyorBelt = UnrealRef<UStaticMesh>("StaticMesh'/Game/Hypogean/Models/SM_ConveyorBelt.SM_ConveyorBelt'")

    val SM_Robo = UnrealRef<UStaticMesh>("StaticMesh'/Game/Hypogean/Models/Robo/SM_Robo.SM_Robo'")

    val SM_SimpleWall = UnrealRef<UStaticMesh>("StaticMesh'/Game/Hypogean/Models/SM_SimpleWall.SM_SimpleWall'")

    val SM_Tube = UnrealRef<UStaticMesh>("StaticMesh'/Game/Hypogean/Models/SM_Tube.SM_Tube'")

    val OverheadFont = UnrealRef<UFont>("Font'/Game/Hypogean/Fonts/OverHeadFont.OverHeadFont'")
}
