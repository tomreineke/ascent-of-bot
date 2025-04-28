package com.cerebrallychallenged.hypogean.model.containment

import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.MAX_CONTAINMENT_DEPTH
import com.cerebrallychallenged.hypogean.model.ModelException
import com.cerebrallychallenged.hypogean.model.SlotBearer
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec

/**
 * @param container
 * @param boxPosition Position of that item within its container, given by the number of boxes from left and top.
 */
data class ContainerPosition(val container: Item, val boxPosition: Vec2i) {
    val anchor: SlotBearer?
        get() = container.anchor

    val containedItem: Item? = container.containedItems.firstOrNull { it.containerPosition == this }
}

fun Item.findFreeContainerPosition(): ContainerPosition? =
    providedPositions.firstOrNull { it.containedItem == null }

fun Item.insert(item: Item, boxPosition: Vec2i? = null) {
    item.containerPosition = if (boxPosition != null) {
        ContainerPosition(this, boxPosition)
    } else {
        findFreeContainerPosition() ?: throw ModelException("No free box position in container $this")
    }
}


/**
 * The space (boxes) provided by this container for its contained items.
 * A size of vec(0, 0) indicates that the item cannot be used as a container.
 */
var Item.providedBoxes: Vec2i by attribute(Vec2i.ZERO)

val Item.providedBoxPositions: Bounds<Vec2i>
    get() = Bounds.byMinSize(Vec2i.ZERO, providedBoxes)

val Item.providedPositions: Sequence<ContainerPosition>
    get() = sequence {
        val providedBoxes = providedBoxes
        for (y in 0 until providedBoxes.y) {
            for (x in 0 until providedBoxes.x) {
                yield(ContainerPosition(this@providedPositions, vec(x, y)))
            }
        }
    }

///**
// * Position of that item within its container, given by the number of boxes from left and top.
// */
//var Item.boxPosition: Vec2i by attribute(Vec2i.ZERO)

/**
 * Determines which kind of items that container does accept (not regarding space requirements).
 */
var Item.itemAcceptor: ItemAcceptor by attribute(AcceptAll)

/**
 * Returns if this item is transitively contained by {@code other}.
 * Example: A backpack contains a box, which contains a rocket launcher, which contains a rocket.
 * Then the rocket is directly only contained by the rocket launcher,
 * but transitively also contained by the box and the backpack.
 */
fun Item.isTransitivelyContainedBy(other: Item): Boolean {
    var item = this
    repeat(MAX_CONTAINMENT_DEPTH) {
        val container = item.container ?: return false
        if (container == other) {
            return true
        } else {
            item = container
        }
    }
    modelError("Containment hierarchy of item $this is too deep (potential cycles)")
}

val Item.transitivelyContainedItems: Sequence<Item>
    get() = sequence {
        yield(this@transitivelyContainedItems)
        for (item in containedItems) {
            yieldAll(item.transitivelyContainedItems)
        }
    }

val SlotBearer.transitivelyContainedItems: Sequence<Item>
    get() = slots.asSequence().flatMap { it.transitivelyContainedItems }

fun Item.containedItemAt(boxPosition: Vec2i): Item? = containedItems.firstOrNull {
    it.containerPosition?.boxPosition == boxPosition
}
