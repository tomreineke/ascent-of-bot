package com.cerebrallychallenged.hypogean.vanilla.refs

import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.mesh.USkeletalMesh
import com.cerebrallychallenged.jun.unreal.mesh.UStaticMesh

object ModularLowPolyRobots {

    // Bodies
    val SM_BodySmall_Type1 = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotBodies/SM_BodySmall_Type1.SM_BodySmall_Type1'")
    val SM_BodySmall_Type3 = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotBodies/SM_BodySmall_Type3.SM_BodySmall_Type3'")
    val SM_BodySmall_Type4 = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotBodies/SM_BodySmall_Type4.SM_BodySmall_Type4'")

    // Weapons
    val SM_HeavyRocket = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotWeapons/SM_HeavyRocket.SM_HeavyRocket'")
    val SM_HeavyRocketPlatform = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotWeapons/SM_HeavyRocketPlatform.SM_HeavyRocketPlatform'")
    val SM_LaserGun = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotWeapons/SM_LaserGun.SM_LaserGun'")
    val SM_LaserGunPlatform = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotWeapons/SM_LaserGunPlatform.SM_LaserGunPlatform'")
    val SM_MachineGun = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotWeapons/SM_MachineGun.SM_MachineGun'")
    val SM_MachineGunPlatform = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotWeapons/SM_MachineGunPlatform.SM_MachineGunPlatform'")
    val SM_PlasmaGun = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotWeapons/SM_PlasmaGun.SM_PlasmaGun'")
    val SM_PlasmaGunPlatform = UnrealRef<UStaticMesh>("StaticMesh'/Game/ModularLowPolyRobots/Meshes/RobotWeapons/SM_PlasmaGunPlatform.SM_PlasmaGunPlatform'")

    // Chassis
    val SM_TankChassis = UnrealRef<USkeletalMesh>("SkeletalMesh'/Game/ModularLowPolyRobots/Meshes/RobotChassis/TankChassis/SM_TankChassis.SM_TankChassis'")
    val SM_HoverChassis = UnrealRef<USkeletalMesh>("SkeletalMesh'/Game/ModularLowPolyRobots/Meshes/RobotChassis/HoverChassis/SM_HoverChassis.SM_HoverChassis'")
    val SM_WheelChassis = UnrealRef<USkeletalMesh>("SkeletalMesh'/Game/ModularLowPolyRobots/Meshes/RobotChassis/WheelChassis_W4/SM_WheelChassis_W4.SM_WheelChassis_W4'")
}