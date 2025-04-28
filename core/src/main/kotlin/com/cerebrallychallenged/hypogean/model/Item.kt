package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.base.InventorySlot
import com.cerebrallychallenged.hypogean.model.base.occupiedLocations
import com.cerebrallychallenged.hypogean.model.base.placement
import com.cerebrallychallenged.hypogean.model.base.presentPropsList
import com.cerebrallychallenged.hypogean.model.base.propSlot
import com.cerebrallychallenged.hypogean.model.base.transformedPropSize
import com.cerebrallychallenged.hypogean.model.containment.ContainerPosition
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.vanilla.attributes.transform
import com.cerebrallychallenged.jun.math.FLOAT_PI
import com.cerebrallychallenged.jun.math.geo.Quaternion.Companion.fromNormalAxisAngle
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec

const val MAX_CONTAINMENT_DEPTH = 128

abstract class Item(initializer: Initializer) : NonWorldEntity(initializer), LocatedEntity, ItemOrOrEventOrTransient {
    /**
     * Absolute ini time when this item has been placed in its current container.
     */
    var placementTime: Int = 0

    /**
     * The Item directly containing this.
     */
    val container: Item?
        get() = containerPosition?.container
//
//    val boxPosition: Vec2i
//        get() = containment?.boxPosition ?: Vec2i.ZERO

    var containerPosition: ContainerPosition? = null
        set(newContainerPosition) {
            val oldContainerPosition = field
            if (oldContainerPosition != newContainerPosition) {
                oldContainerPosition?.container?.let { oldContainer ->
                    for (cell in occupiedLocations(oldContainer, placement)) {
                        cell.presentPropsList -= this
                    }
                    for (statusEffect in transitivelyCarriedStatusEffects) {
                        statusEffect.unregisterAtCells(positionOverride = oldContainer.position2f)
                    }
                }

                oldContainerPosition?.container?._containedItems?.remove(this)
                newContainerPosition?.container?._containedItems?.add(this)
                placementTime = world.currentIniTime
                field = newContainerPosition
                containmentRoot // just to make sure there are no containment cycles
                world.notify(WorldChange.ItemMove(this, oldContainerPosition, newContainerPosition))

                newContainerPosition?.container?.let { newContainer ->
                    for (cell in occupiedLocations(newContainer, placement)) {
                        // This may already have been added to presentPropList. That's due to the fact that
                        // this set-method is first called through World.addProp(), and then also
                        // transitively by the line
                        // world.notify(WorldChange.ItemMove(this, oldContainerPosition, newContainerPosition))
                        // (see above).
                        if (!cell.presentPropsList.contains(this)) {
                            cell.presentPropsList += this
                        }
                    }
                    for (statusEffect in transitivelyCarriedStatusEffects) {
                        statusEffect.registerAtCells(positionOverride = newContainer.position2f)
                    }
                }

            }
        }

    private val _containedItems: MutableSet<Item> = linkedSetOf()

    val containedItems: Set<Item>
        get() = _containedItems

    /**
     * The entity (Cell, Wall, or Actor) ultimately containing this Item.
     */
    open val anchor: SlotBearer?
        get() = (containmentRoot as? Slot)?.anchor

    private val checkedLocatedAnchor: LocatedEntity
        get() {
            val anchor = this.anchor
            return if (anchor is LocatedEntity && anchor.isLocated) {
                anchor
            } else if (this is InventorySlot) {
                (this.anchor as FactionEntity).actors.first { it.isAlive && it.isLocated }
            } else {
                modelError("Item $this has no located anchor")
            }
        }


    override fun remove() {
        containerPosition = null
        for (item in _containedItems.toList()) {
            item.remove()
        }
        _containedItems.clear()
        super.remove()
    }

    override fun onAttributeChanged(change: WorldChange.AttributeChanged<*>) {
        super.onAttributeChanged(change)
        change.ifOf(Item::placement) { (_, _, newPlacement, oldPlacement) ->
            if (newPlacement != oldPlacement) {
                for (cell in occupiedLocations(anchor as? LocatedEntity, oldPlacement)) {
                    cell.presentPropsList -= this
                }
                for (cell in occupiedLocations(anchor as? LocatedEntity, newPlacement)) {
                    cell.presentPropsList += this
                }
            }
        }
    }


//    internal fun insertIn(newContainer: Item?) {
//        val prevContainer = this.container
//        if (prevContainer != newContainer) {
//            for (cell in occupiedLocations(prevContainer, placement)) {
//                cell.presentPropsList -= this
//            }
//
//            prevContainer?._containedItems?.remove(this)
//            newContainer?._containedItems?.add(this)
//            placementTime = world.currentIniTime
//            this.container = newContainer
//            containmentRoot // just to make sure there are no containment cycles
//            world.notify(WorldChange.ItemInsertion(newContainer, prevContainer, this))
//
//            for (cell in occupiedLocations(newContainer, placement)) {
//                cell.presentPropsList += this
//            }
//        }
//    }

    /**
     * Returns the root of the containment hierarchy.
     * That can be a slot or an item not contained anywhere.
     * For those items containmentRoot returns themselves.
     */
    private val containmentRoot: Item
        get() {
            var item = this
            repeat(MAX_CONTAINMENT_DEPTH) {
                item = item.container ?: return item
            }
            modelError("Containment hierarchy of item $this is too deep (potential cycles)")
        }

    override fun collectInitialChanges(collector: (WorldChange) -> Unit) {
        super.collectInitialChanges(collector)
        containerPosition?.let { collector(WorldChange.ItemMove(this, null, it)) }
    }

    override val isLocated: Boolean
        get() {
            val anchor = this.anchor
            return anchor is LocatedEntity && anchor.isLocated
        }

    override val position: Vec2i
        get() = checkedLocatedAnchor.position

    override val basePoint: Vec3f
        get() {
            val size = transformedPropSize
            return (
                    checkedLocatedAnchor.basePoint
                            + vec(0.5f * (size.x - 1), 0.5f * (size.y - 1), 0.0f)
                            + placement.delta
            )
        }
}

fun <T : Item> World.addItem(
    propFactory: (Initializer) -> T,
    position: Vec2i,
    transform: (T) -> Transform3f = {
        it.transformWhenDropped(0, 0).withRotation(
            fromNormalAxisAngle(Vec3f.UNIT_Z, FLOAT_PI * random.nextFloat())
        )
    }
) {
    cell[position].propSlot.insert(
        world.create(propFactory).apply {
            this.transform = transform(this)
        }
    )
}