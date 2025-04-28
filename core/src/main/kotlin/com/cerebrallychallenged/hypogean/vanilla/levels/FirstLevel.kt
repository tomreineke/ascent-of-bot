package com.cerebrallychallenged.hypogean.vanilla.levels

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.EntityType
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldFactory
import com.cerebrallychallenged.hypogean.model.addItem
import com.cerebrallychallenged.hypogean.model.base.addProp
import com.cerebrallychallenged.hypogean.model.base.heading
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.create
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.transformWhenDropped
import com.cerebrallychallenged.hypogean.npc.SkipBehavior
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.actions.hackingDialog
import com.cerebrallychallenged.hypogean.vanilla.actors.FirstBoss
import com.cerebrallychallenged.hypogean.vanilla.actors.GreatAI
import com.cerebrallychallenged.hypogean.vanilla.actors.GreatAI.Companion.GREAT_AI_CELL
import com.cerebrallychallenged.hypogean.vanilla.actors.MiningRobot
import com.cerebrallychallenged.hypogean.vanilla.actors.NeutralMiningRobot
import com.cerebrallychallenged.hypogean.vanilla.actors.SimpleGuardRobot
import com.cerebrallychallenged.hypogean.vanilla.attributes.dialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.remainingUseCount
import com.cerebrallychallenged.hypogean.vanilla.attributes.removeOnDeath
import com.cerebrallychallenged.hypogean.vanilla.dialogs.ActivateFireFlaresDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.FirstLevelCompanionDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.FirstLevelElevatorDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.GreatAIDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.OpenGuardDoorDialog
import com.cerebrallychallenged.hypogean.vanilla.events.ConveyorBeltEvent
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.EmitFireFlares
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.FireTurret
import com.cerebrallychallenged.hypogean.vanilla.events.fixedCoordinate
import com.cerebrallychallenged.hypogean.vanilla.events.movingCoordinatesForActors
import com.cerebrallychallenged.hypogean.vanilla.events.movingCoordinatesForItems
import com.cerebrallychallenged.hypogean.vanilla.factions.DeepDrillingCorpFaction
import com.cerebrallychallenged.hypogean.vanilla.factions.MerchantFaction
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy.Laser
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.RocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.items.utility.BasicFireProtector
import com.cerebrallychallenged.hypogean.vanilla.items.utility.RepairArm
import com.cerebrallychallenged.hypogean.vanilla.props.Bag
import com.cerebrallychallenged.hypogean.vanilla.props.Barrel01
import com.cerebrallychallenged.hypogean.vanilla.props.BigDoor
import com.cerebrallychallenged.hypogean.vanilla.props.Blanket1
import com.cerebrallychallenged.hypogean.vanilla.props.Blanket2
import com.cerebrallychallenged.hypogean.vanilla.props.Cartonage3
import com.cerebrallychallenged.hypogean.vanilla.props.ChargingPlatform
import com.cerebrallychallenged.hypogean.vanilla.props.ConveyorBelt
import com.cerebrallychallenged.hypogean.vanilla.props.CoveredBox
import com.cerebrallychallenged.hypogean.vanilla.props.FirstBossTerminal
import com.cerebrallychallenged.hypogean.vanilla.props.HiddenDoor
import com.cerebrallychallenged.hypogean.vanilla.props.LandMine
import com.cerebrallychallenged.hypogean.vanilla.props.NeonLight
import com.cerebrallychallenged.hypogean.vanilla.props.Pallet
import com.cerebrallychallenged.hypogean.vanilla.props.Pipe
import com.cerebrallychallenged.hypogean.vanilla.props.Rack_3x1x1_20_50_80
import com.cerebrallychallenged.hypogean.vanilla.props.RectangularVent
import com.cerebrallychallenged.hypogean.vanilla.props.SimpleChest
import com.cerebrallychallenged.hypogean.vanilla.props.SimpleElevator
import com.cerebrallychallenged.hypogean.vanilla.props.SimpleElevatorButton
import com.cerebrallychallenged.hypogean.vanilla.props.Terminal
import com.cerebrallychallenged.hypogean.vanilla.props.UnitBunkBedDropped_20_80
import com.cerebrallychallenged.hypogean.vanilla.props.WoodenBox
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.EnergyOverTime
import com.cerebrallychallenged.hypogean.vanilla.triggers.ChargingPlatformHintTrigger
import com.cerebrallychallenged.hypogean.vanilla.walls.FenceResearchCenter6Prop
import com.cerebrallychallenged.hypogean.view.globalDirectionalBrightness
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.FLOAT_PI
import com.cerebrallychallenged.jun.math.degrees
import com.cerebrallychallenged.jun.math.geo.*
import com.cerebrallychallenged.jun.math.geo.Quaternion.Companion.fromNormalAxisAngle
import com.cerebrallychallenged.jun.math.geo.Vec3f.Companion.UNIT_Y

data object FirstLevel : WorldFactory {

    fun transformBagWhenDropped(x: Int, y: Int) =
        if (x in CONVEYOR_BELT_X_RANGE && y == CONVEYOR_BELT_Y) { // if dropped on conveyor belt
            Transform3f.translation(vec(0f, 0f, 10f))
        } else {
            Transform3f.IDENTITY
        }

    private val CONVEYOR_BELT_X_RANGE: IntRange = -12..-6
    const val CONVEYOR_BELT_Y = 9
    val LAND_MINE_POS = vec(3, -1)
    val DOOR_POS = vec(-2, -7)
    val AMBUSH_PARTY_POS = listOf(vec(7, -9), vec(7, -7), vec(5, -9), vec(5, -7))
    val HIDDEN_BOSS_DOOR_POS = vec(10, 2)
    const val FIRST_FIRE_FLARE_DIRECTION = "east-to-west"
    const val SECOND_FIRE_FLARE_DIRECTION = "west-to-east"

    override fun World.setup() {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        readLevel("Levels/firstLevel.txt", vec(-13, -10), vec(12, 10))
        globalDirectionalBrightness = 0.5f

        val event = create(::ConveyorBeltEvent).apply {
            enqueueRelative(0)
            activate()
            movingCoordinatesForItems = CONVEYOR_BELT_X_RANGE
            movingCoordinatesForActors = -10 .. -9
            fixedCoordinate = CONVEYOR_BELT_Y
        }

        create(::GreatAI, GREAT_AI_CELL, DeepDrillingCorpFaction, Heading.NORTH_EAST, iniDelta = 0)

        /////////////////////////////////////////////////// starting room /////////////////////////////////////////////
        // hero is first in order of action
        // actual start vec(-9, 7)
        // tool room vec(-3, -7)
        // boss room vec(6, 3)
        // elevator room vec(11, -7)
        create(::MiningRobot, vec(-9, 7), ProtagonistFaction, Heading.NORTH_EAST, iniDelta = 0).apply {
            dialog = GreatAIDialog
        }

        for (i in CONVEYOR_BELT_X_RANGE) {
            addProp(::ConveyorBelt, vec(i, CONVEYOR_BELT_Y))
        }
        addProp(::NeonLight, vec(-9, 9), Heading.SOUTH_WEST)
        addProp(::NeonLight, vec(-10, 7), Heading.NORTH_WEST)

        addItem(::Laser, vec(-8, 6))

        for (i in 5..8) {
            addProp(::Bag, vec(-8, i), Heading.NORTH_WEST).apply {
                transformWhenDropped = FirstLevel::transformBagWhenDropped
            }
            addProp(::Bag, vec(-10, i), Heading.NORTH_WEST).apply {
                transformWhenDropped = FirstLevel::transformBagWhenDropped
            }
        }

        addProp(::FenceResearchCenter6Prop, vec(-9, 3)).apply {
            removeOnDeath = listOf(event)
        }

        /////////////////////////////////////////////////// charging room /////////////////////////////////////////////
        addProp(::ChargingPlatform, vec(-10, -2)).apply {
            createStatusEffect(EntityType(ChargingPlatformHintTrigger::class) { ChargingPlatformHintTrigger(it) })
        }
        addItem(::RocketLauncher, vec(-12, -1))
        addProp(::NeonLight, vec(-11, 2), Heading.SOUTH_WEST)

        addProp(::Pipe, vec(-10, -6), Heading.NORTH_WEST)
        addProp(::Pipe, vec(-10, -5), Heading.NORTH_WEST)
        addProp(::Pipe, vec(-10, -4), Heading.NORTH_WEST)

        //////////////////////////////////////// first encounter room /////////////////////////////////////////////
        addProp(::NeonLight, vec(-11, -9), Heading.NORTH_EAST)

        addProp(::SimpleChest, vec(-11, -9)).apply {
            insert(world.create(::RepairArm))
        }

        create(::SimpleGuardRobot, vec(-12, -6), DeepDrillingCorpFaction, Heading.SOUTH_EAST, iniDelta = 1)

        /////////////////////////////////////////////////// tool room /////////////////////////////////////////////
        addProp(::RectangularVent, vec(-7, -6))
        addProp(::Rack_3x1x1_20_50_80, vec(-6, -4))

        create(::SimpleGuardRobot, vec(-7, -6), DeepDrillingCorpFaction, Heading.SOUTH_EAST, iniDelta = 1)

        // 2x2 pallet
        addProp(::Pallet, vec(-7, -9), Transform3f.scale(vec(2.0f, 2.0f, 1.0f)))
        // put barrels slightly up because underneath there is a pallet
        addProp(::Barrel01, vec(-7, -9), Transform3f.translation(vec(0.0f, 0.0f, 14.0f)))
        addProp(::Barrel01, vec(-6, -9), Transform3f.translation(vec(0.0f, 0.0f, 14.0f)))
        addProp(::Barrel01, vec(-7, -8), Transform3f.translation(vec(0.0f, 0.0f, 14.0f)))

        addProp(::CoveredBox, vec(-3, -9))
        addProp(::Cartonage3, vec(-5, -5))
        addProp(::BigDoor, DOOR_POS, Heading.SOUTH_EAST)

        addProp(
            ::NeonLight,
            vec(-3, -9),
            Transform3f.translation(vec(0.0f, 0.0f, 70.0f)).withRotation(fromNormalAxisAngle(Vec3f.UNIT_Z, 180.degrees))
        )
        addProp(::Terminal, vec(-5, -9)).apply { hackingDialog = OpenGuardDoorDialog }

        /////////////////////////////////////////////////// guard room /////////////////////////////////////////////
        addProp(::NeonLight, vec(1, -7), Heading.SOUTH_WEST)

        for (position in AMBUSH_PARTY_POS) {
            addProp(::ChargingPlatform, position)
            create(::SimpleGuardRobot, position, DeepDrillingCorpFaction, Heading.SOUTH_EAST, iniDelta = 1).apply {
                addStatusEffect(5 of EnergyOverTime)
                behavior = SkipBehavior
            }
        }

        addProp(
            ::SimpleChest,
            vec(7, -8),
            Transform3f.rotation(fromNormalAxisAngle(Vec3f.UNIT_Z, FLOAT_PI / 2))
        ).apply {
            insert(world.create(::RocketLauncher).apply {
                remainingUseCount = 2
                explanatoryTooltip = "Can only be used $remainingUseCount times."
            })
            insert(world.create(::BasicFireProtector))
        }

        /////////////////////////////////////////////////// sleeping room /////////////////////////////////////////////
        addProp(::NeonLight, vec(1, -5), Heading.NORTH_EAST)
        addProp(::LandMine, LAND_MINE_POS, Heading.NORTH_EAST)

        // two beds...
        val zRotation = FLOAT_PI * 0.1f
        addProp(
            ::UnitBunkBedDropped_20_80,
            vec(0, -3),
            Transform3f.rotation(fromNormalAxisAngle(Vec3f.UNIT_Z, zRotation))
        )
        addProp(
            ::UnitBunkBedDropped_20_80,
            vec(2, -3),
            Transform3f.rotation(fromNormalAxisAngle(Vec3f.UNIT_Z, -zRotation))
        )
        addProp(
            ::Blanket1,
            vec(3, -3),
            Transform3f.rotation(fromNormalAxisAngle(Vec3f.UNIT_Z, FLOAT_PI * 0.2f))
        )
        addProp(::Blanket2, vec(0, -4))

        create(::NeutralMiningRobot, vec(1, -5), MerchantFaction, iniDelta = 1).apply {
            health = 35
            dialog = FirstLevelCompanionDialog
        }

        /////////////////////////////////////////////////// right room /////////////////////////////////////////////
        addProp(::NeonLight, vec(2, 5), Heading.SOUTH_WEST)
        addProp(::NeonLight, vec(9, 3), Heading.SOUTH_EAST)
        addProp(::ChargingPlatform, vec(3, 5))
        addProp(::WoodenBox, vec(1, 0))

        /////////////////////////////////////////////////// eating room /////////////////////////////////////////////
        val hiddenEntryDoor = addProp(::HiddenDoor, HIDDEN_BOSS_DOOR_POS, Heading.NORTH_EAST)
        val exitDoor = addProp(::BigDoor, vec(11, -6), Heading.NORTH_EAST)

        create(::FirstBoss, vec(6, 0), DeepDrillingCorpFaction, Heading.SOUTH_EAST, iniDelta = 1).apply {
            addStatusEffect(5 of EnergyOverTime)
            removeOnDeath = listOf(hiddenEntryDoor, exitDoor)
            behavior = SkipBehavior
        }
        addProp(
            ::NeonLight,
            vec(5, -2),
            Transform3f.translation(vec(0.0f, 0.0f, 100.0f))
        )
        addProp(::FirstBossTerminal, vec(9, -5)).apply {
            hackingDialog = ActivateFireFlaresDialog
        }

        fun createFireTurrets(specs: List<Pair<Vec2i, Heading>>): MutableList<FireTurret> = specs.map { (position, heading) ->
            addProp(::FireTurret, position).also {
                it.heading = heading
            }
        }.toMutableList()

        val yCoordsWithRange6 = -5 .. -1
        val yCoordsWithRange3 = 0 .. 1
        val firstFireFlare = createFireTurrets(yCoordsWithRange6.map { Pair(vec(5, it), Heading.NORTH_WEST) })
        for (y in yCoordsWithRange3) {
            firstFireFlare.add(
                // Smaller range to make up for the walls on (8, 1) and (8, 0) that should block fire
                // It could be done by checking the BlockingValue, but this is simpler.
                addProp(::FireTurret, vec(5, y)).also {
                    it.heading = Heading.NORTH_WEST
                }.apply {
                    this.range = 3.0f
                }
            )
        }

        val secondFireFlare = createFireTurrets(yCoordsWithRange6.map { Pair(vec(11, it), Heading.SOUTH_EAST) })
        for (y in yCoordsWithRange3) {
            secondFireFlare.add(
                // Smaller range to make up for the walls on (8, 1) and (8, 0) that should block fire
                // It could be done by checking the BlockingValue, but this is simpler.
                addProp(::FireTurret, vec(11, y)).also {
                    it.heading = Heading.SOUTH_EAST
                }.apply {
                    this.range = 3.0f
                }
            )
        }

        create(::EmitFireFlares).apply {
            name = FIRST_FIRE_FLARE_DIRECTION
            fireTurretGroup = firstFireFlare
        }

        create(::EmitFireFlares).apply {
            name = SECOND_FIRE_FLARE_DIRECTION
            fireTurretGroup = secondFireFlare
        }

        /////////////////////////////////////////////////// elevator room /////////////////////////////////////////////
        addProp(::NeonLight, vec(11, -9), Heading.NORTH_EAST)
        for (point in Bounds.of(vec(11, -7), vec(9, -9)).points) {
            addProp(
                ::SimpleElevator,
                point,
                Transform3f.scale(vec(1.0f, 1.0f, 1.0f)).withTranslation(vec(50.0f, 50.0f, 10.0f))
            )
        }

        addProp(
            ::SimpleElevatorButton,
            vec(11, -8),
            Transform3f.scale(vec(1.0f, 1.0f, 1.0f))
                .withTranslation(vec(45.0f, 0.0f, 60.0f))
                .withRotation(fromNormalAxisAngle(Vec3f.UNIT_Z, Angle.DEGREE_90))
        ).apply {
            hackingDialog = FirstLevelElevatorDialog
        }
    }
}
