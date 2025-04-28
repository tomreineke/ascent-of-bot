package com.cerebrallychallenged.hypogean.vanilla.items

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor

open class MeleeWeaponSlot(initializer: Initializer) : WeaponSlot(initializer) {
    init {
        itemAcceptor = MeleeWeaponAcceptor
    }
}

object MeleeWeaponAcceptor : ItemAcceptor {
    override fun evaluatePlaceability(item: Item): Placeability {
        return if (item is MeleeWeapon) {
            Placeability.Ok
        } else {
            Placeability.Unplaceable("Only melee weapons can be placed here.")
        }
    }
}