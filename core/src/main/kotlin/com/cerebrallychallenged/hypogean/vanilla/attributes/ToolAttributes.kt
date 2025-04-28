package com.cerebrallychallenged.hypogean.vanilla.attributes

import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.attribute.attribute

// Note that the attributes extend [Item] rather than [Tool] to allow more flexibility.

/**
 * Number of uses left for this item. A value of `null` indicates infinite uses.
 */
var Item.remainingUseCount: Int? by attribute(null)

/**
 * Maximal distance from the activeActor to the target.
 */
var Item.range: Float by attribute(0.0f)

/**
 * The weight of the item. An actor can only take up an item if the cumulative weight of
 * the items he carries is less than the value of maxTonnage provided by the chassis.
 */
var Item.weight: Float by attribute(0.0f)

/**
 * The item has to be present in its equipment slot for that amount of ini time before it can be used.
 */
var Item.setupTime: Int by attribute(0)

/**
 * Absolute ini time when that item has been used last.
 */
var Item.lastUseTime: Int by attribute(0)

/**
 * This item has to wait that number of rounds before it can be used again.
 */
var Item.cooldown: Int by attribute(0)

/**
 * Actors using this item have to wait that number of rounds before it is their turn again.
 */
var Item.initiativeCost: Int by attribute(1)

/**
 * This property is used to retrieve icons for actions that are available for the item. We only have
 * one action per item. This icon is shown in the [com.cerebrallychallenged.hypogean.view.actionbar.ActionBarView]
 * for example.
 * Document this.
 */
var Item.actionButtonIcon: String? by attribute(null)
