package com.cerebrallychallenged.hypogean.vanilla

import com.cerebrallychallenged.hypogean.modding.CoreMod
import com.cerebrallychallenged.hypogean.modding.Features
import com.cerebrallychallenged.hypogean.modding.Mod
import com.cerebrallychallenged.hypogean.modding.ModContext
import com.cerebrallychallenged.hypogean.modding.ModDependencies
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.EntityTypes
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.Factions
import com.cerebrallychallenged.hypogean.model.IniHolder
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.Periodics
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.WorldFactories
import com.cerebrallychallenged.hypogean.model.action.ActionCategories
import com.cerebrallychallenged.hypogean.model.action.Actions
import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodecs
import com.cerebrallychallenged.hypogean.model.attribute.Attributes
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttributes
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptors
import com.cerebrallychallenged.hypogean.model.dialog.DialogRoles
import com.cerebrallychallenged.hypogean.model.dialog.Dialogs
import com.cerebrallychallenged.hypogean.model.effect.EffectConsequences
import com.cerebrallychallenged.hypogean.model.effect.EffectKinds
import com.cerebrallychallenged.hypogean.npc.Behaviors
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractors
import com.cerebrallychallenged.hypogean.util.collections.addAll
import com.cerebrallychallenged.hypogean.vanilla.actions.AttackCategory
import com.cerebrallychallenged.hypogean.vanilla.actions.CollectAllItemsAction
import com.cerebrallychallenged.hypogean.vanilla.actions.DefaultCategoryAppearance
import com.cerebrallychallenged.hypogean.vanilla.actions.DirectShotAction
import com.cerebrallychallenged.hypogean.vanilla.actions.EnrageAction
import com.cerebrallychallenged.hypogean.vanilla.actions.GrapplingAttackAction
import com.cerebrallychallenged.hypogean.vanilla.actions.HackingAction
import com.cerebrallychallenged.hypogean.vanilla.actions.HackingCategory
import com.cerebrallychallenged.hypogean.vanilla.actions.HomingShotAction
import com.cerebrallychallenged.hypogean.vanilla.actions.ItemSwapAction
import com.cerebrallychallenged.hypogean.vanilla.actions.ItemSwapCategory
import com.cerebrallychallenged.hypogean.vanilla.actions.MeleeAction
import com.cerebrallychallenged.hypogean.vanilla.actions.MoveAction
import com.cerebrallychallenged.hypogean.vanilla.actions.MoveCategory
import com.cerebrallychallenged.hypogean.vanilla.actions.OpenInventoryAction
import com.cerebrallychallenged.hypogean.vanilla.actions.PickupAction
import com.cerebrallychallenged.hypogean.vanilla.actions.RepairAction
import com.cerebrallychallenged.hypogean.vanilla.actions.RoundhouseKickAction
import com.cerebrallychallenged.hypogean.vanilla.actions.TalkAction
import com.cerebrallychallenged.hypogean.vanilla.actions.TalkCategory
import com.cerebrallychallenged.hypogean.vanilla.actions.UseCategory
import com.cerebrallychallenged.hypogean.vanilla.actions.UtilityCategory
import com.cerebrallychallenged.hypogean.vanilla.actions.canSideStep
import com.cerebrallychallenged.hypogean.vanilla.actions.grapplingDirection
import com.cerebrallychallenged.hypogean.vanilla.actions.hackingDialog
import com.cerebrallychallenged.hypogean.vanilla.actions.hackingRange
import com.cerebrallychallenged.hypogean.vanilla.actions.quickMoveUsed
import com.cerebrallychallenged.hypogean.vanilla.actions.talkingRange
import com.cerebrallychallenged.hypogean.vanilla.actions.transitItem
import com.cerebrallychallenged.hypogean.vanilla.actors.Asset_FirstBoss
import com.cerebrallychallenged.hypogean.vanilla.actors.Asset_ManipulatorRobot
import com.cerebrallychallenged.hypogean.vanilla.actors.Asset_MiningRobot
import com.cerebrallychallenged.hypogean.vanilla.actors.Asset_Robot_BodySmall_Type1
import com.cerebrallychallenged.hypogean.vanilla.actors.Asset_Robot_BodySmall_Type3
import com.cerebrallychallenged.hypogean.vanilla.actors.Asset_Robot_BodySmall_Type4
import com.cerebrallychallenged.hypogean.vanilla.actors.FirstBoss
import com.cerebrallychallenged.hypogean.vanilla.actors.FirstBossAsset
import com.cerebrallychallenged.hypogean.vanilla.actors.GreatAI
import com.cerebrallychallenged.hypogean.vanilla.actors.ManipulatorRobot
import com.cerebrallychallenged.hypogean.vanilla.actors.ManipulatorRobotActivation
import com.cerebrallychallenged.hypogean.vanilla.actors.Merchant
import com.cerebrallychallenged.hypogean.vanilla.actors.MiningRobot
import com.cerebrallychallenged.hypogean.vanilla.actors.MiningRobotAsset
import com.cerebrallychallenged.hypogean.vanilla.actors.NeutralMiningRobot
import com.cerebrallychallenged.hypogean.vanilla.actors.SimpleGuardRobot
import com.cerebrallychallenged.hypogean.vanilla.actors.SmallModularLowPolyRobotAsset1
import com.cerebrallychallenged.hypogean.vanilla.actors.SmallModularLowPolyRobotAsset3
import com.cerebrallychallenged.hypogean.vanilla.actors.SmallModularLowPolyRobotAsset4
import com.cerebrallychallenged.hypogean.vanilla.attackfx.attackFx
import com.cerebrallychallenged.hypogean.vanilla.attributes.Energy
import com.cerebrallychallenged.hypogean.vanilla.attributes.Health
import com.cerebrallychallenged.hypogean.vanilla.attributes.actionButtonIcon
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.assetParameterBindings
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.description
import com.cerebrallychallenged.hypogean.vanilla.attributes.dialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.energy
import com.cerebrallychallenged.hypogean.vanilla.attributes.energyCharging
import com.cerebrallychallenged.hypogean.vanilla.attributes.energyProduction
import com.cerebrallychallenged.hypogean.vanilla.attributes.flavorText
import com.cerebrallychallenged.hypogean.vanilla.attributes.hasAdjustableRange
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.ignoreLocationAndHeading
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.isInfoViewAvailable
import com.cerebrallychallenged.hypogean.vanilla.attributes.lastUseTime
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxEnergy
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxHealth
import com.cerebrallychallenged.hypogean.vanilla.attributes.passiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.remainingUseCount
import com.cerebrallychallenged.hypogean.vanilla.attributes.removeOnDeath
import com.cerebrallychallenged.hypogean.vanilla.attributes.selectedDialogOptions
import com.cerebrallychallenged.hypogean.vanilla.attributes.setupTime
import com.cerebrallychallenged.hypogean.vanilla.attributes.transform
import com.cerebrallychallenged.hypogean.vanilla.attributes.visibleIfLostSight
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.behavior.CheckPointAsset
import com.cerebrallychallenged.hypogean.vanilla.behavior.CheckPointMarker
import com.cerebrallychallenged.hypogean.vanilla.behavior.FirstBossBehavior
import com.cerebrallychallenged.hypogean.vanilla.behavior.GreatAIBehavior
import com.cerebrallychallenged.hypogean.vanilla.behavior.ManipulatorRobotBehavior
import com.cerebrallychallenged.hypogean.vanilla.behavior.PursuitBehavior
import com.cerebrallychallenged.hypogean.vanilla.behavior.RandomMoveBehavior
import com.cerebrallychallenged.hypogean.vanilla.behavior.StandardBehavior
import com.cerebrallychallenged.hypogean.vanilla.blocks.Asset_CaveWall
import com.cerebrallychallenged.hypogean.vanilla.cascade.ChangeEnergyConsequence
import com.cerebrallychallenged.hypogean.vanilla.cascade.ChangeHealthConsequence
import com.cerebrallychallenged.hypogean.vanilla.cascade.ChangeIniConsequence
import com.cerebrallychallenged.hypogean.vanilla.cascade.EnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.cascade.Falloffs
import com.cerebrallychallenged.hypogean.vanilla.cascade.FlatFalloff
import com.cerebrallychallenged.hypogean.vanilla.cascade.LinearFalloff
import com.cerebrallychallenged.hypogean.vanilla.cascade.SyphonEnergyConsequence
import com.cerebrallychallenged.hypogean.vanilla.cascade.effectImmunities
import com.cerebrallychallenged.hypogean.vanilla.cascade.isIndestructible
import com.cerebrallychallenged.hypogean.vanilla.cascade.needsEnergyForInitiative
import com.cerebrallychallenged.hypogean.vanilla.cascade.providedEffectImmunities
import com.cerebrallychallenged.hypogean.vanilla.cascade.showEnergyChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.cascade.showEnergyChangesWithOverheadText
import com.cerebrallychallenged.hypogean.vanilla.cascade.showHealthChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.cascade.showHealthChangesWithOverheadText
import com.cerebrallychallenged.hypogean.vanilla.cascade.showIniChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.cascade.showIniChangesWithOverheadText
import com.cerebrallychallenged.hypogean.vanilla.dialogs.ActivateFireFlaresDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.FirstLevelCompanionDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.FirstLevelCompanionDialogAfterInParty
import com.cerebrallychallenged.hypogean.vanilla.dialogs.FirstLevelElevatorDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.GreatAIDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.GreatAIDialog2
import com.cerebrallychallenged.hypogean.vanilla.dialogs.OpenGuardDoorDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.TestDialog
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.EnergyCharging
import com.cerebrallychallenged.hypogean.vanilla.effects.EnergyDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.ExplosionDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.FireDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.Healing
import com.cerebrallychallenged.hypogean.vanilla.effects.IniDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.LaserDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.PiercingDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.SyphonDamage
import com.cerebrallychallenged.hypogean.vanilla.events.ConveyorBeltEvent
import com.cerebrallychallenged.hypogean.vanilla.events.delta
import com.cerebrallychallenged.hypogean.vanilla.events.direction
import com.cerebrallychallenged.hypogean.vanilla.events.fixedCoordinate
import com.cerebrallychallenged.hypogean.vanilla.events.movingCoordinatesForItems
import com.cerebrallychallenged.hypogean.vanilla.events.movingCoordinatesForActors
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.Asset_FireFlare
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.Asset_FireTurret
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.EmitFireFlares
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.FireFlare
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.FireTurret
import com.cerebrallychallenged.hypogean.vanilla.factions.DeepDrillingCorpFaction
import com.cerebrallychallenged.hypogean.vanilla.factions.MerchantFaction
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_HoverChassis
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_Laser
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_MachineGun
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_PulseLaser
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_TankChassis
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_WheelChassis
import com.cerebrallychallenged.hypogean.vanilla.items.ChassisSlot
import com.cerebrallychallenged.hypogean.vanilla.items.EquipmentAcceptor
import com.cerebrallychallenged.hypogean.vanilla.items.MeleeWeaponAcceptor
import com.cerebrallychallenged.hypogean.vanilla.items.MeleeWeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.ShotWeaponAcceptor
import com.cerebrallychallenged.hypogean.vanilla.items.ShotWeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.UtilityAcceptor
import com.cerebrallychallenged.hypogean.vanilla.items.UtilitySlot
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponAcceptor
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.Chassis
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.HoverChassis
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.IndestructibleChassis
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.PowerMoveChassis
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.StandardChassis
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.WheelChassis
import com.cerebrallychallenged.hypogean.vanilla.items.melee.ChargeHook
import com.cerebrallychallenged.hypogean.vanilla.items.melee.ChargeHookAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.melee.EnrageArm
import com.cerebrallychallenged.hypogean.vanilla.items.melee.EnrageArmAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.melee.GrapplingHook
import com.cerebrallychallenged.hypogean.vanilla.items.melee.GrapplingHookAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.melee.SmashArm
import com.cerebrallychallenged.hypogean.vanilla.items.melee.SmashArmAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.melee.StunArm
import com.cerebrallychallenged.hypogean.vanilla.items.melee.StunArmAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.melee.SweepArm
import com.cerebrallychallenged.hypogean.vanilla.items.melee.SweepArmAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.melee.SyphonArm
import com.cerebrallychallenged.hypogean.vanilla.items.melee.SyphonArmAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.GaussCannon
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.GaussCannonAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.HeavyGaussCannon
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.HeavyGaussCannonAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.HeavyMachineGun
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.HeavyMachineGunAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.LightGaussCannon
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.LightGaussCannonAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.LightMachineGun
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.LightMachineGunAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.MachineGun
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.MachineGunAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.FlameThrower
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.FlameThrowerAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.HeavyParticleProjectileCannon
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.HeavyParticleProjectileCannonAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.LargeLaser
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.LargeLaserAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.LargePulseLaser
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.LargePulseLaserAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.Laser
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.LightParticleProjectileCannon
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.LightParticleProjectileCannonAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.MediumLaserAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.ParticleProjectileCannon
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.ParticleProjectileCannonAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.PulseLaser
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.PulseLaserAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.SmallLaser
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.SmallLaserAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.SmallPulseLaser
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.SmallPulseLaserAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.Asset_HomingMissile
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.Asset_Missile
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.HomingRocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.HomingRocketLauncherAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.LongRangeMissileLauncher
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.LongRangeMissileLauncherAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.MediumRangeMissileLauncher
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.MediumRangeMissileLauncherAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.ReuseRocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.ReuseRocketLauncherAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.RocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.RocketLauncherAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.ShortRangeMissileLauncher
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.ShortRangeMissileLauncherAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.utility.BasicExplosionProtector
import com.cerebrallychallenged.hypogean.vanilla.items.utility.BasicFireProtector
import com.cerebrallychallenged.hypogean.vanilla.items.utility.BluntDamageNegator
import com.cerebrallychallenged.hypogean.vanilla.items.utility.RepairArm
import com.cerebrallychallenged.hypogean.vanilla.items.utility.RepairArmAppearance
import com.cerebrallychallenged.hypogean.vanilla.items.utility.StandardEnergyShield
import com.cerebrallychallenged.hypogean.vanilla.items.utility.StandardEnergyShieldAppearance
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.ActionHistoryTestLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.DialogTestLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.InibarTestLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.MineTestLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.ReconTestLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.ShootingTestLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.TestLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.TrenchTestLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.VoxelTestLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.test.WallTestLevel
import com.cerebrallychallenged.hypogean.vanilla.periodic.EnergyPeriodic
import com.cerebrallychallenged.hypogean.vanilla.periodic.ResetQuickMoveUsed
import com.cerebrallychallenged.hypogean.vanilla.periodic.StatusEffectPeriodic
import com.cerebrallychallenged.hypogean.vanilla.procedural.Asset_RegionTile
import com.cerebrallychallenged.hypogean.vanilla.procedural.Asset_Trench
import com.cerebrallychallenged.hypogean.vanilla.procedural.DefaultTrenchSystem
import com.cerebrallychallenged.hypogean.vanilla.procedural.NeighborhoodAttributeCodec
import com.cerebrallychallenged.hypogean.vanilla.procedural.TrenchBorder
import com.cerebrallychallenged.hypogean.vanilla.procedural.TrenchFloor
import com.cerebrallychallenged.hypogean.vanilla.procedural.TrenchSystems
import com.cerebrallychallenged.hypogean.vanilla.procedural.trenchParameter
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Bag
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Barrel01
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_BigDoor
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_BrickWallArc_DirectionY_2
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_BrickWallBlock
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_BrickWallBlock_2
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Button
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_CartonBox03
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_CaveWallArc_DirectionX_2
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_CaveWallArc_DirectionY_2
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_ChargingPlatform
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Chest
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_ConcreteFloor
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Cyberpunk_Fence
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Cyberpunk_Pipe
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_DirtGroundFloor
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Elevator
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_HiddenBigDoor
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Hidden_Door_Cave_DirectionX
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Hidden_Door_Cave_DirectionY
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_LandMine
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Light
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_NeonLight
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Pallet
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Rack_3x1x1_20_50_80
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_RectangularVent
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_SandstoneBlock_2
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_SandstoneFloor
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Security_Door_Cave_DirectionX
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Security_Door_Cave_DirectionY
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Sparks
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_Sphere
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitBlanket1
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitBlanket2
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitBoard
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitBunkBedDropped_20_80
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitBunkBed_20_80
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitCartonage
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitConveyorBelt
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitCoveredBox
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitPillars
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitRack_20_50_80
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitTerminal
import com.cerebrallychallenged.hypogean.vanilla.props.Asset_UnitWoodenBox
import com.cerebrallychallenged.hypogean.vanilla.props.Bag
import com.cerebrallychallenged.hypogean.vanilla.props.Barrel01
import com.cerebrallychallenged.hypogean.vanilla.props.BigDoor
import com.cerebrallychallenged.hypogean.vanilla.props.Blanket1
import com.cerebrallychallenged.hypogean.vanilla.props.Blanket2
import com.cerebrallychallenged.hypogean.vanilla.props.CartonBox03
import com.cerebrallychallenged.hypogean.vanilla.props.Cartonage3
import com.cerebrallychallenged.hypogean.vanilla.props.CellArc_CaveWallArc_DirectionX_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellArc_CaveWallArc_DirectionY_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellBlock
import com.cerebrallychallenged.hypogean.vanilla.props.CellBlock_BrickWall_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellBlock_CaveWall_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellBlock_Sandstone_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellFloor
import com.cerebrallychallenged.hypogean.vanilla.props.CellFloor_Concrete
import com.cerebrallychallenged.hypogean.vanilla.props.CellFloor_DirtGround
import com.cerebrallychallenged.hypogean.vanilla.props.CellFloor_Sandstone
import com.cerebrallychallenged.hypogean.vanilla.props.ChargingPlatform
import com.cerebrallychallenged.hypogean.vanilla.props.ConveyorBelt
import com.cerebrallychallenged.hypogean.vanilla.props.CoveredBox
import com.cerebrallychallenged.hypogean.vanilla.props.FirstBossTerminal
import com.cerebrallychallenged.hypogean.vanilla.props.HiddenDoor
import com.cerebrallychallenged.hypogean.vanilla.props.LandMine
import com.cerebrallychallenged.hypogean.vanilla.props.Light
import com.cerebrallychallenged.hypogean.vanilla.props.NeonLight
import com.cerebrallychallenged.hypogean.vanilla.props.Pallet
import com.cerebrallychallenged.hypogean.vanilla.props.Pipe
import com.cerebrallychallenged.hypogean.vanilla.props.Rack
import com.cerebrallychallenged.hypogean.vanilla.props.RackBoard
import com.cerebrallychallenged.hypogean.vanilla.props.RackPillars
import com.cerebrallychallenged.hypogean.vanilla.props.Rack_3x1x1_20_50_80
import com.cerebrallychallenged.hypogean.vanilla.props.RectangularVent
import com.cerebrallychallenged.hypogean.vanilla.props.SimpleChest
import com.cerebrallychallenged.hypogean.vanilla.props.SimpleElevator
import com.cerebrallychallenged.hypogean.vanilla.props.SimpleElevatorButton
import com.cerebrallychallenged.hypogean.vanilla.props.Sparks
import com.cerebrallychallenged.hypogean.vanilla.props.SphereProp
import com.cerebrallychallenged.hypogean.vanilla.props.Terminal
import com.cerebrallychallenged.hypogean.vanilla.props.UnitBunkBedDropped_20_80
import com.cerebrallychallenged.hypogean.vanilla.props.WoodenBox
import com.cerebrallychallenged.hypogean.vanilla.rays.BallisticExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValueCodec
import com.cerebrallychallenged.hypogean.vanilla.rays.VisibilityExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovementExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.visibilityBlocking
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.Burning
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.EnergyOverTime
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.HealingOverTime
import com.cerebrallychallenged.hypogean.vanilla.triggers.ChargingPlatformHintTrigger
import com.cerebrallychallenged.hypogean.vanilla.triggers.ClosingDoorTrigger
import com.cerebrallychallenged.hypogean.vanilla.triggers.GreatAiApprovalTrigger
import com.cerebrallychallenged.hypogean.vanilla.triggers.MineTrigger
import com.cerebrallychallenged.hypogean.vanilla.walls.Asset_ResearchCenter_Fence5
import com.cerebrallychallenged.hypogean.vanilla.walls.Asset_ResearchCenter_Fence6
import com.cerebrallychallenged.hypogean.vanilla.walls.Asset_SimpleWall
import com.cerebrallychallenged.hypogean.vanilla.walls.FenceResearchCenter5Prop
import com.cerebrallychallenged.hypogean.vanilla.walls.FenceResearchCenter6Prop
import com.cerebrallychallenged.hypogean.vanilla.walls.SimpleWall
import com.cerebrallychallenged.hypogean.view.CompositeAssets
import com.cerebrallychallenged.hypogean.view.CompositeParameters
import com.cerebrallychallenged.hypogean.view.ViewBaseMod
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonDefinitionsRegistry
import com.cerebrallychallenged.hypogean.view.conf.ViewsDefinitions
import com.cerebrallychallenged.hypogean.view.map.ActorAssets
import com.cerebrallychallenged.hypogean.view.map.events.StandardShouts
import com.cerebrallychallenged.hypogean.view.map.voxel.BlockAssets

@ModDependencies(CoreMod::class, ViewBaseMod::class)
//@AutoMod("com.cerebrallychallenged.hypogean.vanilla")
//@AutoMod("com.cerebrallychallenged.hypogean.vanilla")
object VanillaMod : Mod {
    override fun ModContext.setupFeatureDiscovery() {
        configure<Features> {
            register(EffectKinds())
            register(Falloffs())
            register(TrenchSystems())
        }
    }

    override fun ModContext.installCodecsAndStreaming() {
        configure<AttributeCodecs> {
            register<BlockingValueCodec>()
            register<NeighborhoodAttributeCodec>()
        }
    }

    override fun ModContext.install() {
        configure<ActionButtonDefinitionsRegistry> {
            register(ChargeHookAppearance)
            register(DefaultCategoryAppearance)
            register(EnrageArmAppearance)
            register(FlameThrowerAppearance)
            register(GaussCannonAppearance)
            register(GrapplingHookAppearance)
            register(HeavyGaussCannonAppearance)
            register(HeavyMachineGunAppearance)
            register(HeavyParticleProjectileCannonAppearance)
            register(HomingRocketLauncherAppearance)
            register(LargeLaserAppearance)
            register(LargePulseLaserAppearance)
            register(LightGaussCannonAppearance)
            register(LightMachineGunAppearance)
            register(LightParticleProjectileCannonAppearance)
            register(LongRangeMissileLauncherAppearance)
            register(MachineGunAppearance)
            register(MediumLaserAppearance)
            register(MediumRangeMissileLauncherAppearance)
            register(ParticleProjectileCannonAppearance)
            register(PulseLaserAppearance)
            register(RepairArmAppearance)
            register(ReuseRocketLauncherAppearance)
            register(RocketLauncherAppearance)
            register(ShortRangeMissileLauncherAppearance)
            register(SmallLaserAppearance)
            register(SmallPulseLaserAppearance)
            register(SmashArmAppearance)
            register(StandardEnergyShieldAppearance)
            register(StunArmAppearance)
            register(SweepArmAppearance)
            register(SyphonArmAppearance)
        }
        configure<ActionCategories> {
            register(AttackCategory)
            register(HackingCategory)
            register(ItemSwapCategory)
            register(MoveCategory)
            register(TalkCategory)
            register(UseCategory)
            register(UtilityCategory)
        }
        configure<Actions> {
            register(DirectShotAction)
            register(EnrageAction)
            register(GrapplingAttackAction)
            register(HackingAction)
            register(HomingShotAction)
            register(ItemSwapAction)
            register(CollectAllItemsAction)
            register(MeleeAction)
            register(MoveAction)
            register(PickupAction)
            register(OpenInventoryAction)
            register(RoundhouseKickAction)
            register(RepairAction)
            register(TalkAction)
        }
        configure<ActorAssets> {
            register(FirstBossAsset)
            register(MiningRobotAsset)
            register(SmallModularLowPolyRobotAsset1)
            register(SmallModularLowPolyRobotAsset3)
            register(SmallModularLowPolyRobotAsset4)
        }
        configure<Attributes> {
            register(Actor::dialog)
            register(Actor::hackingRange)
            register(Actor::heading)
            register(Actor::quickMoveUsed)
            register(Actor::canSideStep)
            register(Actor::selectedDialogOptions)
            register(Actor::talkingRange)
            register(Actor::transitItem)
            register(Chassis::maxTonnage)
            register(Chassis::moveRange)
            register(Chassis::quickMoveRange)
            register(ConveyorBeltEvent::delta)
            register(ConveyorBeltEvent::direction)
            register(ConveyorBeltEvent::fixedCoordinate)
            register(ConveyorBeltEvent::movingCoordinatesForActors)
            register(ConveyorBeltEvent::movingCoordinatesForItems)
            register(EmitFireFlares::fireTurretGroup)
            register(Entity::accuracy)
            register(Entity::accuracyFactor)
            register(Entity::activeEnergyConsumption)
            register(Entity::assetParameterBindings)
            register(Entity::ballisticBlocking)
            register(Entity::passiveEnergyConsumption)
            register(Entity::description)
            register(Entity::effectImmunities)
            register(Entity::energy)
            register(Entity::energyCharging)
            register(Entity::energyProduction)
            register(Entity::flavorText)
            register(Entity::groundMovementBlocking)
            register(Entity::health)
            register(Entity::icon)
            register(Entity::ignoreLocationAndHeading)
            register(Entity::isIndestructible)
            register(Entity::isInfoViewAvailable)
            register(Entity::maxEnergy)
            register(Entity::maxHealth)
            register(Entity::providedEffectImmunities)
            register(Entity::removeOnDeath)
            register(Entity::showEnergyChangesInDamageReport)
            register(Entity::showEnergyChangesWithOverheadText)
            register(Entity::showHealthChangesInDamageReport)
            register(Entity::showHealthChangesWithOverheadText)
            register(Entity::showIniChangesInDamageReport)
            register(Entity::showIniChangesWithOverheadText)
            register(Entity::transform)
            register(Entity::visibilityBlocking)
            register(Entity::visibleIfLostSight)
            register(GrapplingHook::grapplingDirection)
            register(IniHolder::needsEnergyForInitiative)
            register(Item::actionButtonIcon)
            register(Item::asset)
            register(Item::cooldown)
            register(Item::hackingDialog)
            register(Item::initiativeCost)
            register(Item::lastUseTime)
            register(Item::range)
            register(Item::remainingUseCount)
            register(Item::setupTime)
            register(Item::attackFx)
            register(Item::weight)
            register(ManipulatorRobotActivation::manipulatorRobotGroup)
            register(StatusEffect::asset)
            register(TrenchFloor::trenchParameter)
            register(Weapon::hasAdjustableRange)
        }
        configure<Behaviors> {
            register(FirstBossBehavior)
            register(GreatAIBehavior)
            register(ManipulatorRobotBehavior)
            register(RandomMoveBehavior)
            register(StandardBehavior)
            register(PursuitBehavior)
        }
        configure<BlockAssets> {
            register(Asset_CaveWall)
        }
        configure<BlockerValueExtractors> {
            register(BallisticExtractor)
            register(GroundMovementExtractor)
            register(VisibilityExtractor)
        }
        configure<CompositeAssets> {
            register(Asset_Barrel01)
            register(Asset_BigDoor)
            register(Asset_HiddenBigDoor)
            register(Asset_Hidden_Door_Cave_DirectionX)
            register(Asset_Hidden_Door_Cave_DirectionY)
            register(Asset_Security_Door_Cave_DirectionX)
            register(Asset_Security_Door_Cave_DirectionY)
            register(Asset_Bag)
            register(Asset_BrickWallBlock)
            register(Asset_BrickWallBlock_2)
            register(Asset_BrickWallArc_DirectionY_2)
            register(Asset_Button)
            register(Asset_CartonBox03)
            register(Asset_CaveWallArc_DirectionX_2)
            register(Asset_CaveWallArc_DirectionY_2)
            register(Asset_ChargingPlatform)
            register(Asset_Chest)
            register(Asset_ConcreteFloor)
            register(Asset_Cyberpunk_Fence)
            register(Asset_DirtGroundFloor)
            register(Asset_Elevator)
            register(Asset_Fire)
            register(Asset_FireFlare)
            register(Asset_FireTurret)
            register(Asset_FirstBoss)
            register(Asset_HomingMissile)
            register(Asset_HoverChassis)
            register(Asset_LandMine)
            register(Asset_Laser)
            register(Asset_Light)
            register(Asset_MachineGun)
            register(Asset_ManipulatorRobot)
            register(Asset_MiningRobot)
            register(Asset_Missile)
            register(Asset_NeonLight)
            register(Asset_Pallet)
            register(Asset_Cyberpunk_Pipe)
            register(Asset_PulseLaser)
            register(Asset_Rack_3x1x1_20_50_80)
            register(Asset_RectangularVent)
            register(Asset_ResearchCenter_Fence5)
            register(Asset_ResearchCenter_Fence6)
            register(Asset_RegionTile)
            register(Asset_Robot_BodySmall_Type1)
            register(Asset_Robot_BodySmall_Type3)
            register(Asset_Robot_BodySmall_Type4)
            register(Asset_SandstoneBlock_2)
            register(Asset_SandstoneFloor)
            register(Asset_SimpleWall)
            register(Asset_Sparks)
            register(Asset_Sphere)
            register(Asset_TankChassis)
            register(Asset_Trench)
            register(Asset_UnitBlanket1)
            register(Asset_UnitBlanket2)
            register(Asset_UnitBoard)
            register(Asset_UnitBunkBedDropped_20_80)
            register(Asset_UnitBunkBed_20_80)
            register(Asset_UnitCartonage)
            register(Asset_UnitConveyorBelt)
            register(Asset_UnitCoveredBox)
            register(Asset_UnitPillars)
            register(Asset_UnitRack_20_50_80)
            register(Asset_UnitTerminal)
            register(Asset_UnitWoodenBox)
            register(Asset_WheelChassis)
            register(CheckPointAsset)
        }
        configure<CompositeParameters> {
            register(Asset_FireFlare.Range)
            register(Asset_FireTurret.Heading)
            register(Asset_RegionTile.RegionParameter)
            register(Asset_Trench.TrenchParameter)
        }
        configure<Dialogs> {
            register(ActivateFireFlaresDialog)
            register(FirstLevelCompanionDialog)
            register(FirstLevelCompanionDialogAfterInParty)
            register(FirstLevelElevatorDialog)
            register(GreatAIDialog)
            register(GreatAIDialog2)
            register(OpenGuardDoorDialog)
            register(TestDialog)
        }
        configure<DialogRoles> {
            register(ActivateFireFlaresDialog.Computer)
            register(ActivateFireFlaresDialog.User)
            register(FirstLevelCompanionDialog.Protagonist)
            register(FirstLevelCompanionDialog.NeutralMiningRobot)
            register(FirstLevelCompanionDialogAfterInParty.Protagonist)
            register(FirstLevelCompanionDialogAfterInParty.FirstCompanion)
            register(FirstLevelElevatorDialog.ElevatorButton)
            register(FirstLevelElevatorDialog.User)
            register(GreatAIDialog.Protagonist)
            register(GreatAIDialog.GreatAI)
            register(GreatAIDialog2.Protagonist)
            register(GreatAIDialog2.GreatAI)
            register(OpenGuardDoorDialog.Computer)
            register(OpenGuardDoorDialog.User)
            register(TestDialog.Merchant)
            register(TestDialog.Protagonist)
        }
        configure<EffectConsequences> {
            register(IniDamage, ::ChangeIniConsequence, 1)
            register(ExplosionDamage, ::ChangeHealthConsequence, -1)
            register(LaserDamage, ::ChangeHealthConsequence, -1)
            register(FireDamage, ::ChangeHealthConsequence, -1)
            register(PiercingDamage, ::ChangeHealthConsequence, -1)
            register(BluntDamage, ::ChangeHealthConsequence, -1)
            register(SyphonDamage, ::SyphonEnergyConsequence, 1)
            register(EnergyDamage, ::ChangeEnergyConsequence, -1)
            register(EnergyConsumption, ::ChangeEnergyConsequence, -1)
            register(EnergyCharging, ::ChangeEnergyConsequence, 1)
            register(Healing, ::ChangeHealthConsequence, 1)
        }
        configure<EffectKinds> {
            register(BluntDamage)
            register(EnergyDamage)
            register(EnergyCharging)
            register(ExplosionDamage)
            register(FireDamage)
            register(Healing)
            register(IniDamage)
            register(LaserDamage)
            register(PiercingDamage)
            register(SyphonDamage)
        }
        configure<EntityTypes> {
            register(::Bag)
            register(::Barrel01)
            register(::BasicExplosionProtector)
            register(::BasicFireProtector)
            register(::BigDoor)
            register(::Blanket1)
            register(::Blanket2)
            register(::BluntDamageNegator)
            register(::Burning)
            register(::CartonBox03)
            register(::Cartonage3)
            register(::CellArc_CaveWallArc_DirectionX_2)
            register(::CellArc_CaveWallArc_DirectionY_2)
            register(::CellBlock)
            register(::CellBlock_BrickWall_2)
            register(::CellBlock_CaveWall_2)
            register(::CellBlock_Sandstone_2)
            register(::CellFloor)
            register(::CellFloor_Concrete)
            register(::CellFloor_DirtGround)
            register(::CellFloor_Sandstone)
            register(::ChargeHook)
            register(::ChargingPlatform)
            register(::ChargingPlatformHintTrigger)
            register(::ChassisSlot)
            register(::CheckPointMarker)
            register(::ClosingDoorTrigger)
            register(::ConveyorBelt)
            register(::ConveyorBeltEvent)
            register(::CoveredBox)
            register(::EmitFireFlares)
            register(::EnergyOverTime)
            register(::EnrageArm)
            register(::FenceResearchCenter5Prop)
            register(::FenceResearchCenter6Prop)
            register(::FireFlare)
            register(::FireTurret)
            register(::FirstBoss)
            register(::FirstBossTerminal)
            register(::FlameThrower)
            register(::GaussCannon)
            register(::GrapplingHook)
            register(::GreatAI)
            register(::GreatAiApprovalTrigger)
            register(::HealingOverTime)
            register(::HeavyGaussCannon)
            register(::HeavyMachineGun)
            register(::HeavyParticleProjectileCannon)
            register(::HiddenDoor)
            register(::HomingRocketLauncher)
            register(::HoverChassis)
            register(::IndestructibleChassis)
            register(::LandMine)
            register(::LargeLaser)
            register(::LargePulseLaser)
            register(::Laser)
            register(::Light)
            register(::LightGaussCannon)
            register(::LightMachineGun)
            register(::LightParticleProjectileCannon)
            register(::LongRangeMissileLauncher)
            register(::MachineGun)
            register(::ManipulatorRobot)
            register(::ManipulatorRobotActivation)
            register(::MediumRangeMissileLauncher)
            register(::MeleeWeaponSlot)
            register(::Merchant)
            register(::MineTrigger)
            register(::MiningRobot)
            register(::NeonLight)
            register(::NeutralMiningRobot)
            register(::Pallet)
            register(::ParticleProjectileCannon)
            register(::Pipe)
            register(::PowerMoveChassis)
            register(::PulseLaser)
            register(::Rack)
            register(::RackBoard)
            register(::RackPillars)
            register(::Rack_3x1x1_20_50_80)
            register(::RectangularVent)
            register(::RepairArm)
            register(::ReuseRocketLauncher)
            register(::RocketLauncher)
            register(::ShortRangeMissileLauncher)
            register(::ShotWeaponSlot)
            register(::SimpleChest)
            register(::SimpleElevator)
            register(::SimpleElevatorButton)
            register(::SimpleGuardRobot)
            register(::SimpleWall)
            register(::SmallLaser)
            register(::SmallPulseLaser)
            register(::SmashArm)
            register(::Sparks)
            register(::SphereProp)
            register(::StandardChassis)
            register(::StandardEnergyShield)
            register(::StunArm)
            register(::SweepArm)
            register(::SyphonArm)
            register(::Terminal)
            register(::TrenchBorder)
            register(::TrenchFloor)
            register(::UnitBunkBedDropped_20_80)
            register(::UtilitySlot)
            register(::WeaponSlot)
            register(::WheelChassis)
            register(::WoodenBox)
        }
        configure<Factions> {
            register(DeepDrillingCorpFaction)
            register(MerchantFaction)
            register(ProtagonistFaction)
        }
        configure<Falloffs> {
            register(FlatFalloff)
            register(LinearFalloff)
        }
        configure<ItemAcceptors> {
            register(EquipmentAcceptor)
            register(MeleeWeaponAcceptor)
            register(ShotWeaponAcceptor)
            register(UtilityAcceptor)
            register(WeaponAcceptor)
        }
        configure<Periodics> {
            register(EnergyPeriodic)
            register(ResetQuickMoveUsed)
            register(StatusEffectPeriodic)
        }
        configure<SimpleIntAttributes> {
            register(Health)
            register(Energy)
        }
        configure<TrenchSystems> {
            register(DefaultTrenchSystem)
        }
        configure<ViewsDefinitions> {
            register(DefaultViews)
        }
        configure<WorldFactories> {
            register(ActionHistoryTestLevel)
            register(DialogTestLevel)
            register(FirstLevel)
            register(InibarTestLevel)
            register(MineTestLevel)
            register(ReconTestLevel)
            register(ShootingTestLevel)
            register(TestLevel)
            register(TrenchTestLevel)
            register(VoxelTestLevel)
            register(WallTestLevel)
        }

        StandardShouts[Faction.Relation.SAME].addAll(
            "What's next?"
        )
        StandardShouts[Faction.Relation.ALLIED].addAll(
            "What's up, my friend?"
        )
        StandardShouts[Faction.Relation.NEUTRAL].addAll(
            "What a wonderful day."
        )
        StandardShouts[Faction.Relation.HOSTILE].addAll(
            "Intruder detected.",
            "You're not going to escape!",
        )
    }
}
