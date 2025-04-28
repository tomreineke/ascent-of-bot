package com.cerebrallychallenged.hypogean.model.base

import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Slot
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.jun.math.geo.vec

open class InventorySlot(initializer: Initializer) : Slot(initializer) {
    init {
        name = "Inventory"
        providedBoxes = vec(9, 3)
    }
}

fun FactionEntity.inventory() = slot("inventory") as InventorySlot

context(WorldContext)
fun Faction.inventory(): InventorySlot = entity.inventory()
