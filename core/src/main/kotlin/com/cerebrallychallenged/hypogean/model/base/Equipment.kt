package com.cerebrallychallenged.hypogean.model.base

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.Slot
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.pickupAble
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.lastUseTime
import com.cerebrallychallenged.hypogean.vanilla.attributes.setupTime
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking

abstract class Equipment(initializer: Initializer) : Item(initializer) {
    init {
        setupTime = 0
        pickupAble = true
        height = 0.2f
        ballisticBlocking = BlockingValue { 1.0f }
    }
}

open class EquipmentSlot(initializer: Initializer) : Slot(initializer) {
    init {
        itemAcceptor = EquipmentAcceptor
    }
}

object EquipmentAcceptor : ItemAcceptor {
    override fun evaluatePlaceability(item: Item): Placeability {
        return if (item is Equipment) {
            Placeability.Ok
        } else {
            Placeability.Unplaceable("Only equipment can be placed here.")
        }
    }
}

val Actor.equipmentSlots: Sequence<EquipmentSlot>
    get() = slots.asSequence().filterIsInstance<EquipmentSlot>()

val Actor.equippedItems: Sequence<Item>
    get() = equipmentSlots.flatMap { it.containedItems.asSequence() }

/**
 * Returns the remaining rounds needed for the item to set up or `null` if the item is already setup.
 */
val Item.remainingSetupTime: Int?
    get() = remainingTime(Item::placementTime, Item::setupTime)

val Item.remainingCooldown: Int?
    get() = remainingTime(Item::lastUseTime, Item::cooldown)

/**
 * We'd like to restrict this method to Tool instead of Item, but we are unsure whether we want to allow mods
 * to use lastUseTime, placementTime etc. for Items.
 * Without the generic parameter a change like remainingTime(Tool::lastUseTime, Tool::cooldown)
 * would not compile.
 */
private fun <T : Item> T.remainingTime(lastTimeFn: (T) -> Int, requiredDeltaTimeFn: (T) -> Int): Int? {
    val lastTime = lastTimeFn(this)
    // Since no item can be created before ini time 0, we assume that an item with lastTime == 0 has
    // been inserted/used sufficiently long ago.
    // This assumption leads to a bug with the calculation of the cooldown when using a weapon on the first turn.
    // We accept this because we can avoid this by not giving the protagonist a weapon with cooldown at the start.
    if (lastTime == 0) return null
    return (lastTime + requiredDeltaTimeFn(this) - world.currentIniTime).takeIf { it > 0 }
}
