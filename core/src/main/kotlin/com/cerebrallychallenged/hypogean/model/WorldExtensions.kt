package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.activestate.ActiveDialog
import com.cerebrallychallenged.hypogean.activestate.ActiveEventState
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.jun.math.geo.Vec2i

@JvmInline
value class WorldCells(private val world: World) {
    operator fun get(position: Vec2i): Cell = world.cellAt(position) ?: modelError("No cell found at $position")
}

val World.cell: WorldCells
    get() = WorldCells(this)

/**
 * Creates an [Actor] using the specified [EntityType].
 * Example:
 *     val location: Cell = ...
 *     val actorType: EntityType<Actor> = ...
 *     world.create(actorType, location)
 */
fun <T: Actor> World.create(actorType: EntityType<T>, location: Cell?): T = create(actorType.factory, location)

fun <T: Actor> World.create(
        factory: (Initializer) -> T,
        position: Vec2i,
        faction: Faction? = null,
        heading: Heading? = null,
        iniDelta: Int? = null
): T = create(factory, cell[position]).apply {
    faction?.let { this.faction = it }
    heading?.let { this.heading = it.angle }
    iniDelta?.let { enqueueRelative(it) }
}

/**
 * Creates an [Item], [Event], or [Transient] using the specified [EntityType].
 * Example:
 *     val itemType: EntityType<Item> = ...
 *     world.create(itemType)
 */
fun <T: ItemOrOrEventOrTransient> World.create(entityType: EntityType<T>): T = create(entityType.factory)

val World.factionEntities: List<FactionEntity>
    get() = rulebook.factions.map { world.factionEntity(it) }

val World.activeActor: Actor?
    get() = when (val state = activeState) {
        is ActiveActorState -> state.activeActor
        is ActiveDialog -> state.activeActor
        else -> null
    }

val World.activeIniHolder: IniHolder?
    get() = when (val state = activeState) {
        is ActiveActorState -> state.activeActor
        is ActiveDialog -> state.activeActor
        is ActiveEventState -> state.activeEvent
        else -> null
    }
