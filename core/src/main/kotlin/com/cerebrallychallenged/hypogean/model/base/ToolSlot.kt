package com.cerebrallychallenged.hypogean.model.base

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor

open class ToolSlot(initializer: Initializer) : EquipmentSlot(initializer) {
    init {
        itemAcceptor = ToolAcceptor
    }
}

object ToolAcceptor : ItemAcceptor {
    override fun evaluatePlaceability(item: Item): Placeability {
        return if (item is Tool) {
            Placeability.Ok
        } else {
            Placeability.Unplaceable("Only tools can be placed here.")
        }
    }
}