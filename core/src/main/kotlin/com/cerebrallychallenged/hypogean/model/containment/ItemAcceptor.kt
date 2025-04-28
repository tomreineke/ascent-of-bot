package com.cerebrallychallenged.hypogean.model.containment

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Item

/**
 * Determines which kind of items a container does accept (not regarding space requirements).
 * @see itemAcceptor
 */
interface ItemAcceptor {
    /**
     * Returns if the specified item is allowed to be placed in the specified container
     * (not regarding space requirements).
     * @param item the item
     * @return [Placeability.Ok] or [Placeability.Unplaceable] with list of reasons why the item cannot be placed there.
     */
    fun evaluatePlaceability(item: Item): Placeability
}

class ItemAcceptors : SimpleObjectRegistry<ItemAcceptor>()

/**
 * Accepts all items.
 */
object AcceptAll : ItemAcceptor {
    override fun evaluatePlaceability(item: Item): Placeability = Placeability.Ok
}
