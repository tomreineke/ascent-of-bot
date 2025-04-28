package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.base.EquipmentSlot
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.Chassis

open class ChassisSlot(initializer: Initializer) : EquipmentSlot(initializer) {
    init {
        itemAcceptor = EquipmentAcceptor
    }
}

object EquipmentAcceptor : ItemAcceptor {
    override fun evaluatePlaceability(item: Item): Placeability {
        return if (item is Chassis) {
            Placeability.Ok
        } else {
            Placeability.Unplaceable("Only chassis can be placed here.")
        }
    }
}
