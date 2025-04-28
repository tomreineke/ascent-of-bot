package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.base.ToolSlot
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor

open class WeaponSlot(initializer: Initializer) : ToolSlot(initializer) {
    init {
        itemAcceptor = WeaponAcceptor
    }
}

object WeaponAcceptor : ItemAcceptor {
    override fun evaluatePlaceability(item: Item): Placeability {
        return if (item is Weapon) {
            Placeability.Ok
        } else {
            Placeability.Unplaceable("Only weapons can be placed here.")
        }
    }
}