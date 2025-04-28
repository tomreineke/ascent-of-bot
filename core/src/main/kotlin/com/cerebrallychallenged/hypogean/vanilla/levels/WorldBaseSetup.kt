package com.cerebrallychallenged.hypogean.vanilla.levels

import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.base.DropSlot
import com.cerebrallychallenged.hypogean.model.base.InventorySlot
import com.cerebrallychallenged.hypogean.model.base.inventory
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.factionEntities
import com.cerebrallychallenged.hypogean.model.periodics
import com.cerebrallychallenged.hypogean.vanilla.factions.DeepDrillingCorpFaction
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.PowerMoveChassis
import com.cerebrallychallenged.hypogean.vanilla.items.melee.EnrageArm
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.ReuseRocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.periodic.EnergyPeriodic
import com.cerebrallychallenged.hypogean.vanilla.periodic.ResetQuickMoveUsed
import com.cerebrallychallenged.hypogean.vanilla.periodic.StatusEffectPeriodic
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.points
import com.cerebrallychallenged.jun.math.geo.vec

fun World.setupBase(initialCells: Bounds<Vec2i>) {
    setupCells(initialCells)
    for (factionEntity in factionEntities) {
        factionEntity.addSlot("inventory", create(::InventorySlot).apply {
            providedBoxes = vec(9, 3)
        })
        factionEntity.addSlot("dropSlot", create(::DropSlot).apply {
            providedBoxes = vec(1, 1)
        })
        if (factionEntity.faction == ProtagonistFaction) {
            factionEntity.inventory().apply {
//                insert(world.create(::Laser))
            }
        }
        if (factionEntity.faction == DeepDrillingCorpFaction) {
            factionEntity.inventory().apply {
                insert(world.create(::ReuseRocketLauncher))
                insert(world.create(::EnrageArm))
                insert(world.create(::PowerMoveChassis))
            }
        }
    }
    periodics += listOf(
        ResetQuickMoveUsed,
        StatusEffectPeriodic,
        EnergyPeriodic
    )
}

fun World.setupCells(bounds: Bounds<Vec2i>) {
    for (position in bounds.points) {
        if (cellAt(position) == null) {
            create(position)
        }
    }
}
