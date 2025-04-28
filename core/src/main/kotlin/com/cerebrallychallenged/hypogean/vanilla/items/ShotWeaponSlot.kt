package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor

open class ShotWeaponSlot(initializer: Initializer) : WeaponSlot(initializer) {
    init {
        itemAcceptor = ShotWeaponAcceptor
    }
}

object ShotWeaponAcceptor : ItemAcceptor {
    override fun evaluatePlaceability(item: Item): Placeability {
        return if (item is DirectShotWeapon) {
            Placeability.Ok
        } else {
            Placeability.Unplaceable("Only ranged weapons can be placed here.")
        }
    }
}
