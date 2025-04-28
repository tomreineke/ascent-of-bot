package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.MovingEntity
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.base.presentProps
import com.cerebrallychallenged.hypogean.model.diameter
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor
import com.cerebrallychallenged.hypogean.vanilla.actors.GreatAI.Companion.GREAT_AI_CELL
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.points
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.util.minAllBy

fun Cell.nearCells(radius: Float): Sequence<Cell> {
    val position = this.position
    val squaredRadius = radius * radius
    return Bounds.centered(position, Vec2i.ONE * radius.ceilToInt()).points
            .filter{ it.squaredDistanceTo(position) <= squaredRadius }
            .mapNotNull { world.cellAt(it) }
}

/**
 * Here is a hack to make it possible for the Great AI to reach all actors
 * when talking to them.
 */
fun Cell.nearActors(radius: Float): Sequence<Actor> {
    return if (this.position == GREAT_AI_CELL) {
        world.actors.asSequence()
    } else {
        nearCells(radius).mapNotNull { it.presentActor }.distinct()
    }
}


fun Cell.nearProps(radius: Float): Sequence<Item> =
    nearCells(radius).flatMap { it.presentProps }.distinct()

context(WorldContext)
fun Vec2f.nearCells(radius: Float): Sequence<Cell> {
    val delta = Vec2f.ONE * radius
    val squaredRadius = radius * radius
    return Bounds.of((this - delta).ceil(), (this + delta).floor()).points.filter {
        it.toFloat().squaredDistanceTo(this) <= squaredRadius
    }.mapNotNull { world.cellAt(it) }
}

context(WorldContext)
fun Vec2f.nearActors(radius: Float): Sequence<Actor> = nearCells(radius).mapNotNull { it.presentActor }.distinct()

context(WorldContext)
fun Vec2f.nearProps(radius: Float): Sequence<Item> = nearCells(radius).flatMap { it.presentProps }.distinct()

private fun LocatedEntity.radiusOrZero(): Float = (this as? MovingEntity)?.let { 0.5f * it.diameter } ?: 0.0f

fun LocatedEntity.nearCells(radius: Float, positionOverride: Vec2f? = null): Sequence<Cell> =
    if (isLocated) { (positionOverride ?: position2f).nearCells(radius + radiusOrZero()) } else sequenceOf()


fun LocatedEntity.nearActors(radius: Float): Sequence<Actor> = position2f.nearActors(radius + radiusOrZero())

fun LocatedEntity.nearProps(radius: Float): Sequence<Item> = position2f.nearProps(radius + radiusOrZero())

private fun LocatedEntity.adjacentPositions(ownSize: Int, otherSize: Int): Sequence<Vec2i> = sequence {
    val pos = position
    for (i in -otherSize + 1 until ownSize) {
        yield(pos + vec(ownSize, i))
        yield(pos + vec(i, ownSize))
        yield(pos + vec(-ownSize, i))
        yield(pos + vec(i, -ownSize))
    }
}

//fun Entity.nearCells(radius: Float, actualPosition: Vec2f = )

/**
 * All positions for which, if an actor of the specified size was placed there, that actor would touch sides with
 * this cell.
 */
fun Cell.adjacentPositions(otherSize: Int): Sequence<Vec2i> = adjacentPositions(1, otherSize)

fun Cell.adjacentPositions(other: Actor): Sequence<Vec2i> = adjacentPositions(other.diameter)

fun Actor.adjacentPositions(otherSize: Int): Sequence<Vec2i> = adjacentPositions(diameter, otherSize)

fun Actor.adjacentPositions(other: Actor): Sequence<Vec2i> = adjacentPositions(other.diameter)

fun Cell.adjacentLocations(otherSize: Int): Sequence<Cell>
        = adjacentPositions(otherSize).mapNotNull { world.cellAt(it) }

fun Cell.adjacentLocations(other: Actor): Sequence<Cell>
        = adjacentPositions(other).mapNotNull { world.cellAt(it) }

fun Actor.adjacentLocations(otherSize: Int): Sequence<Cell>
        = adjacentPositions(otherSize).mapNotNull { world.cellAt(it) }

fun Actor.adjacentLocations(other: Actor): Sequence<Cell>
        = adjacentPositions(other).mapNotNull { world.cellAt(it) }

fun Actor.adjacentActors(): Sequence<Actor>
        = adjacentLocations(1).mapNotNull { it.presentActor }.distinct()

private fun Actor.adjacentPositionsWithHeading(): Sequence<Pair<Vec2i, Heading>> = sequence {
    val dia = diameter
    val pos = position
    for (i in 0 until dia) {
        // Heading points towards this actor in order to determine the wall.
        yield(Pair(pos + vec(dia, i), Heading.SOUTH_EAST))
        yield(Pair(pos + vec(i, dia), Heading.SOUTH_WEST))
        yield(Pair(pos + vec(-1, i), Heading.NORTH_WEST))
        yield(Pair(pos + vec(i, -1), Heading.NORTH_EAST))
    }
}

fun Actor.adjacentHittableLocations(blockerValueExtractor: BlockerValueExtractor, threshold: Double = 0.5)
        = adjacentPositionsWithHeading().mapNotNull { (pos, heading) ->
    // Heading points towards this actor in order to determine the blocking at the border.
    if (
            blockerValueExtractor.doubleSidedBorderValue(
                    world,
                    pos + heading.delta,
                    heading.opposite(),
                    this
            ) <= threshold
    ) {
        world.cellAt(pos)
    } else null
}

fun Actor.adjacentHittableActors(blockerValueExtractor: BlockerValueExtractor, threshold: Double = 0.5)
        = adjacentHittableLocations(blockerValueExtractor, threshold).mapNotNull { it.presentActor }.distinct()


fun Actor.findClosestPosition(targetPosition: Vec2i): Vec2i {
    // The actor occupies at least one position. Pick the closest.
    return occupiedPositions.points.minByOrNull { it.squaredDistanceTo(targetPosition) }!!
}

private fun Actor.findAllClosestPositionPairs(target: Actor): List<Pair<Vec2i, Vec2i>> {
    return target.occupiedPositions.points
            .map { Pair(findClosestPosition(it), it) }
            .minAllBy { (a, b) -> a.squaredDistanceTo(b) }
}

fun Actor.findClosestPositionPair(target: Actor): Pair<Vec2i, Vec2i> = findAllClosestPositionPairs(target)[0]
