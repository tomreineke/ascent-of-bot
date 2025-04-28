package com.cerebrallychallenged.hypogean.vanilla.levels.test

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldFactory
import com.cerebrallychallenged.hypogean.model.base.PropPlacement
import com.cerebrallychallenged.hypogean.model.base.addProp
import com.cerebrallychallenged.hypogean.model.base.heading
import com.cerebrallychallenged.hypogean.model.base.inventory
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.create
import com.cerebrallychallenged.hypogean.model.disguised
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.npc.MoveCirclesBehavior
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.actions.hackingDialog
import com.cerebrallychallenged.hypogean.vanilla.actors.FirstBoss
import com.cerebrallychallenged.hypogean.vanilla.actors.Merchant
import com.cerebrallychallenged.hypogean.vanilla.actors.MiningRobot
import com.cerebrallychallenged.hypogean.vanilla.actors.Robot
import com.cerebrallychallenged.hypogean.vanilla.attributes.dialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.behavior.CheckPointMarker
import com.cerebrallychallenged.hypogean.vanilla.behavior.CheckPointSeekingBehavior
import com.cerebrallychallenged.hypogean.vanilla.dialogs.ActivateFireFlaresDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.TestDialog
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.EmitFireFlares
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.FireTurret
import com.cerebrallychallenged.hypogean.vanilla.factions.DeepDrillingCorpFaction
import com.cerebrallychallenged.hypogean.vanilla.factions.MerchantFaction
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.Chassis
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic.MachineGun
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.RocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.levels.readLevel
import com.cerebrallychallenged.hypogean.vanilla.levels.setupBase
import com.cerebrallychallenged.hypogean.vanilla.procedural.DefaultTrenchSystem
import com.cerebrallychallenged.hypogean.vanilla.procedural.createTrench
import com.cerebrallychallenged.hypogean.vanilla.props.CellBlock_CaveWall_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellBlock_Sandstone_2
import com.cerebrallychallenged.hypogean.vanilla.props.CellFloor_Concrete
import com.cerebrallychallenged.hypogean.vanilla.props.CellFloor_Sandstone
import com.cerebrallychallenged.hypogean.vanilla.props.FirstBossTerminal
import com.cerebrallychallenged.hypogean.vanilla.props.LandMine
import com.cerebrallychallenged.hypogean.vanilla.props.Light
import com.cerebrallychallenged.hypogean.vanilla.props.SimpleChest
import com.cerebrallychallenged.hypogean.vanilla.props.SphereProp
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.StarterContent
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.Burning
import com.cerebrallychallenged.hypogean.vanilla.walls.FenceResearchCenter5Prop
import com.cerebrallychallenged.hypogean.view.globalDirectionalBrightness
import com.cerebrallychallenged.jun.math.floorMod
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.boundaryPoints
import com.cerebrallychallenged.jun.math.geo.interiorPoints
import com.cerebrallychallenged.jun.math.geo.vec

internal fun World.setupCheckerBoard(size: Vec2i, excludedPositions: Set<Vec2i> = setOf()) {
    val bounds = Bounds.centered(Vec2i.ZERO, size)
    for (pos in bounds.interiorPoints.filterNot { it in excludedPositions }) {
        val floorFactory = if ((pos.x + pos.y) and 1 == 0) {
            ::CellFloor_Sandstone
        } else {
            ::CellFloor_Concrete
        }
        addProp(floorFactory, pos)
        if (pos.x.floorMod(8) == 0 && pos.y.floorMod(8) == 0) {
            addProp(::Light, pos, transform = Transform3f.translation(vec(0.0f, 0.0f, 200.0f)))
        }
    }
    for (pos in bounds.boundaryPoints) {
        addProp(::CellFloor_Sandstone, pos)
        addProp(::CellBlock_CaveWall_2, pos)
    }
}

internal fun World.setupProtagonist(position: Vec2i): Robot =
        create(::MiningRobot, position, ProtagonistFaction, iniDelta = 0)

object TestLevel : WorldFactory {
    override fun World.setup() {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        setupCheckerBoard(vec(9, 9))

//        addProp(::LandMine, vec(2, 2))
        for (x in 2..14) {
            for (y in 2..14) {
                addProp(::LandMine, vec(x, y))
            }
        }

        val robot = setupProtagonist(vec(1, 1))
        robot.addStatusEffect(5 of Burning)

        val merchant = create(::Merchant, vec(-3, 0), MerchantFaction, iniDelta = 1).apply {
            dialog = TestDialog
        }

        val fireTurret = addProp(::FireTurret, vec(2, 4)).also {
            it.heading = Heading.SOUTH_WEST
        }
//        val flare = addProp(::HorizontalFireFlare, vec(2, 4), Heading.SOUTH_WEST.rotation)
        val fireEvent = create(::EmitFireFlares).apply {
            iniQueue.enqueueRelative(0, this)
            fireTurretGroup = listOf(fireTurret)
        }
    }
}

object DialogTestLevel : WorldFactory {
    override fun World.setup() {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        setupCheckerBoard(vec(9, 9))

        setupProtagonist(vec(1, 1))
        create(::Merchant, vec(-3, 0), MerchantFaction, iniDelta = 1).apply {
            dialog = TestDialog
        }
        create(::FirstBoss, vec(4, 1), DeepDrillingCorpFaction, iniDelta = 1).apply {
            health = 10
        }
        addProp(::FirstBossTerminal, vec(2, 1)).apply {
            hackingDialog = ActivateFireFlaresDialog
        }
        addProp(::SimpleChest, vec(1, -4))
        val chest = addProp(::SimpleChest, vec(1, 0))
        chest.insert(create(::RocketLauncher))

        addProp(::SphereProp, vec(-4, -4))
    }
}

object InibarTestLevel : WorldFactory {
    override fun World.setup() {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        readLevel("Levels/inibarTestLevel.txt", vec(-13, -10), vec(12, 10))

        globalDirectionalBrightness = 0.5f

        val robot = setupProtagonist(vec(1, 1))
        robot.addStatusEffect(5 of Burning)

        val merchant = create(::Merchant, vec(-3, 0), MerchantFaction, iniDelta = 1).apply {
            dialog = TestDialog
        }

        create(::MiningRobot, vec(-7, 3), DeepDrillingCorpFaction, Heading.NORTH_EAST, iniDelta = 0)
        create(::MiningRobot, vec(6, -8), DeepDrillingCorpFaction, Heading.SOUTH_EAST, iniDelta = 0)
        create(::MiningRobot, vec(6, -7), DeepDrillingCorpFaction, Heading.SOUTH_EAST, iniDelta = 0)
        create(::MiningRobot, vec(6, -2), DeepDrillingCorpFaction, Heading.NORTH_WEST, iniDelta = 0)
        create(::MiningRobot, vec(5, -8), DeepDrillingCorpFaction, Heading.SOUTH_EAST, iniDelta = 0)
        create(::MiningRobot, vec(5, -7), DeepDrillingCorpFaction, Heading.SOUTH_EAST, iniDelta = 0)
        create(::MiningRobot, vec(-12, -1), DeepDrillingCorpFaction, Heading.NORTH_WEST, iniDelta = 0)
        create(::MiningRobot, vec(11, -6), DeepDrillingCorpFaction, Heading.SOUTH_WEST, iniDelta = 0)
        create(::MiningRobot, vec(0, -2), DeepDrillingCorpFaction, Heading.NORTH_WEST, iniDelta = 0)

        val fireTurret = addProp(::FireTurret, vec(2, 4)).also {
            it.heading = Heading.SOUTH_WEST
        }
//        val flare = addProp(::HorizontalFireFlare, vec(2, 4), Heading.SOUTH_WEST.rotation)
        val fireEvent = create(::EmitFireFlares).apply {
            iniQueue.enqueueRelative(0, this)
            fireTurretGroup = listOf(fireTurret)
        }
    }
}

object ReconTestLevel : WorldFactory {
    override fun World.setup() {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        setupCheckerBoard(vec(9, 9))
        setupProtagonist(vec(0, 0))

        for (y in -2..2) {
            addProp(::CellBlock_CaveWall_2, vec(2, y)).apply {
                disguised = true
            }
        }

    }
}

object VoxelTestLevel : WorldFactory {
    override fun World.setup() {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        setupCheckerBoard(vec(9, 9))
        addProp(::CellBlock_CaveWall_2, vec(1, 1)).apply {
        }
        addProp(::CellBlock_Sandstone_2, vec(2, 1)).apply {
            height = 60.0f
        }
        addProp(::CellBlock_Sandstone_2, vec(3, 1))
        setupProtagonist(vec(0, 0))
    }
}

object TrenchTestLevel : WorldFactory {
    override fun World.setup() {
        val trenchPositions = sequence {
            for (i in -3 .. 5) {
                yield(vec(2, i))
            }
            yield(vec(1, 4))
            yield(vec(3, 5))
        }.toSet()
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        setupCheckerBoard(vec(9, 9), excludedPositions = trenchPositions)
        val trenchMaterial = StarterContent.M_Rock_Sandstone
        createTrench(DefaultTrenchSystem, trenchPositions) { trenchMaterial }
        setupProtagonist(vec(0, 0))
    }
}

object WallTestLevel : WorldFactory {
    override fun World.setup() {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        setupCheckerBoard(vec(9, 9))
//        val wall = addProp(::FenceProp, vec(3, 0), placement = PropPlacement.Center)
        val wall = addProp(::FenceResearchCenter5Prop, vec(3, 0), placement = PropPlacement.Border(Heading.SOUTH_WEST))
//        val wall = addProp(::SimpleWallProp, vec(3, 0), placement = PropPlacement.Border(Heading.SOUTH_WEST))
        wall.groundMovementBlocking = BlockingValue { 1.0f }
//        wall.groundMovementBlocking = BlockingValue {
//            when (heading) {
//                Heading.SOUTH_WEST -> 0.0f
//                else -> 1.0f
//            }
//        }
//        wall.groundMovementBlocking = BlockingValue {
//            when (orientation) {
//                RayOrientation.Inbound -> 0.0f
//                else -> 1.0f
//            }
//        }
        setupProtagonist(vec(0, 0))
    }
}

object MineTestLevel : WorldFactory {
    override fun World.setup() {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        setupCheckerBoard(vec(15, 15))
        readLevel("Levels/mineTestLevel.txt", vec(-13, -10), vec(12, 10)) { position, code ->
            when (code) {
                " MM " -> {
                    addProp(::LandMine, position)
                }
                " TA " -> {
                    addProp(::CheckPointMarker, position)
                }
            }
        }
        setupProtagonist(vec(0, 0))
        val miningRobot = create(::MiningRobot, vec(-2, -3), DeepDrillingCorpFaction, Heading.NORTH_EAST, iniDelta = 0).apply {
            behavior = CheckPointSeekingBehavior
        }
        for (item in miningRobot.slot("chassis").containedItems) {
            if (item is Chassis) {
                item.moveRange = 64
            }
        }
    }
}

object ShootingTestLevel : WorldFactory {
    val TestWayPoints = listOf(vec(1, 1), vec(8, -3), vec(6, 4), vec(0, 5))



    override fun World.setup() {
        setupBase(Bounds.of(vec(-15, -15), vec(15, 15)))
        setupCheckerBoard(vec(15, 15))

        setupProtagonist(vec(0, 0))

        for (point in TestWayPoints) {
            addProp(::CheckPointMarker, point)
        }

        val miningRobot = create(::MiningRobot, vec(10, -1), DeepDrillingCorpFaction, Heading.NORTH_EAST, iniDelta = 0).apply {
            behavior = MoveCirclesBehavior
//            faction?.inventory()?.insert(world.create(::MachineGun))
//            behavior = StandardBehavior
        }

        ProtagonistFaction.inventory().insert(world.create(::MachineGun))
//        create(::TemporaryShield).apply {
//            insertIn(miningRobot.slot("torso"))
//        }
        addProp(::CellBlock_CaveWall_2, vec(7, -2))
    }
}
