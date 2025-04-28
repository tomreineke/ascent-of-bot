package com.cerebrallychallenged.hypogean.model.base

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.Slot
import com.cerebrallychallenged.hypogean.model.SlotBearer
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cell
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.vanilla.attributes.transform
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.points
import com.cerebrallychallenged.jun.math.geo.times
import com.cerebrallychallenged.jun.math.geo.vec

const val PROP_SLOT_NAME = "props"

open class Prop(initializer: Initializer) : Item(initializer)

interface HeadedProp : Entity

var HeadedProp.heading: Heading by attribute(Heading.NORTH_WEST)

sealed class PropPlacement {
    object Center : PropPlacement() {
        override val delta: Vec3f
            get() = Vec3f.ZERO

        override val rotation: Quaternion
            get() = Quaternion.IDENTITY
    }

    data class Border(val heading: Heading) : PropPlacement() {
        override val delta: Vec3f
            get() = 0.5f * heading.delta.append(0)

        override val rotation: Quaternion
            get() = heading.rotation
    }

    abstract val delta: Vec3f

    abstract val rotation: Quaternion
}

fun propSize(length: Int): Vec2i = vec(1, length)

fun propSize(x: Int, y: Int): Vec2i = vec(x, y)

/**
 * Determines the set of cells occupied by this item if used as a prop.
 */
var Item.propSize: Vec2i by attribute(propSize(1, 1))

var Item.placement: PropPlacement by attribute(PropPlacement.Center)

val Item.isUsedAsProp: Boolean
    get() = container is PropSlot

private fun Item.transformedPropSize(placement: PropPlacement): Vec2i = when (placement) {
    PropPlacement.Center -> propSize
    is PropPlacement.Border -> when (placement.heading) {
        Heading.NORTH_WEST, Heading.SOUTH_EAST -> propSize
        Heading.SOUTH_WEST, Heading.NORTH_EAST -> propSize.yx
    }
}

val Item.transformedPropSize: Vec2i
    get() = transformedPropSize(placement)

/**
 * The positions this prop occupies if it was placed at the specified location.
 */
private fun Item.occupiedPositions(anchor: LocatedEntity?, placement: PropPlacement): Bounds<Vec2i> =
        if (isUsedAsProp && anchor != null)
            Bounds.byMinSize(anchor.position, transformedPropSize(placement))
        else
            Bounds.empty2i()

val Item.occupiedPositions: Bounds<Vec2i>
    get() = occupiedPositions(anchor as? LocatedEntity, placement)

/**
 * The locations this prop occupies if it was placed at the specified location.
 * We need that potentially counterfactual computation to determine (even _after_ the anchor or placement have changed)
 * the cells occupied before the change.
 */
internal fun Item.occupiedLocations(anchor: LocatedEntity?, placement: PropPlacement): Sequence<Cell> =
        occupiedPositions(anchor, placement).points.map { world.cell[it] }

val Item.occupiedLocations: Sequence<Cell>
    get() = occupiedLocations(anchor as? LocatedEntity, placement)

val LocatedEntity.occupiedLocations: Sequence<Cell>
    get() = when (this) {
        is Cell -> sequenceOf(this)
        is Actor -> occupiedLocations
        is Item -> occupiedLocations
        else -> sequenceOf()
    }

open class PropSlot(initializer: Initializer) : Slot(initializer)

val SlotBearer.propSlot: Slot
    get() = slot(PROP_SLOT_NAME)

class Props(val props: Sequence<Item>): Sequence<Item> by props {
    operator fun get(placement: PropPlacement): Sequence<Item> = filter { it.placement == placement }
}

/**
 * Props contained in the [propSlot] of this entity.
 * Does not include /large/ props from nearby cells. To include those, see [presentProps].
 */
val SlotBearer.props: Props
    get() = Props(propSlot.containedItems.asSequence().filterIsInstance<Prop>())

/**
 * Props present in this cell.
 * Includes props contained in nearby cells if their [Prop.propSize] is large enough to intersect this cell.
 */
var Cell.presentPropsList: List<Item> by attribute(listOf())

val Cell.presentProps: Props
    get() = Props(presentPropsList.asSequence())

fun <T : Prop> World.addProp(
        propFactory: (Initializer) -> T,
        position: Vec2i,
        transform: Transform3f = Transform3f.IDENTITY,
        placement: PropPlacement = PropPlacement.Center
): T {
    val prop = create(propFactory)
    prop.placement = placement
    prop.transform = transform
    cell[position].propSlot.insert(prop)
    return prop
}

fun <T : Prop> World.addProp(
        propFactory: (Initializer) -> T,
        position: Vec2i,
        heading: Heading,
        placement: PropPlacement = PropPlacement.Center
): T = addProp(propFactory, position, heading.rotationTransform, placement)


internal object PropPlacementAttributeCodec : AttributeCodec<PropPlacement>
