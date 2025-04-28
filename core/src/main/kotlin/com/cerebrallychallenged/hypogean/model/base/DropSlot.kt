package com.cerebrallychallenged.hypogean.model.base

import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Slot
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.jun.math.geo.vec

open class DropSlot(initializer: Initializer) : Slot(initializer) {
    init {
        name = "Drop item"
        providedBoxes = vec(1, 1)
    }
}

fun FactionEntity.dropSlot() = slot("dropSlot") as DropSlot

context(WorldContext)
fun Faction.dropSlot(): DropSlot = entity.dropSlot()
