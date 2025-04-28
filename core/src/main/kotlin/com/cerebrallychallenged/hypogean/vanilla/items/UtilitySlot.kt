package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.base.EquipmentSlot
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor

open class UtilitySlot(initializer: Initializer) : EquipmentSlot(initializer) {
    init {
        itemAcceptor = UtilityAcceptor
    }
}

object UtilityAcceptor : ItemAcceptor {
    override fun evaluatePlaceability(item: Item): Placeability {
        return if (item is Utility) {
            Placeability.Ok
        } else {
            Placeability.Unplaceable("Only utility items can be placed here.")
        }
    }
}
