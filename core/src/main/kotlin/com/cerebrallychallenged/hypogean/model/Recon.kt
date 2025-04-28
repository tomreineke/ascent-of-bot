package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.maps.SparseEntityMatrix
import com.cerebrallychallenged.hypogean.model.maps.SparseEntityRelation
import com.cerebrallychallenged.hypogean.rays.DEFAULT_SIGHT_STRENGTH
import com.cerebrallychallenged.hypogean.rays.SightQuery
import com.cerebrallychallenged.hypogean.rays.exposure
import com.cerebrallychallenged.hypogean.rays.sight
import com.cerebrallychallenged.hypogean.vanilla.rays.VisibilityExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovement
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap

enum class Recon {
    /**
     * Complete ignorance that entity exists.
     */
    Unknown,

    /**
     * That entity can be seen right now.
     */
    Visible,

    /**
     * The entity has once been seen, but the faction has lost sight of it.
     */
    LostSight
}

/**
 * Keeps track of the recon this faction has of all entities.
 */
internal class ReconTable(private val world: World) {
    private val factionEntities = world.factionEntities

    private val table = SparseEntityMatrix<FactionEntity, Entity, Recon>()

    private var isValid = false

    operator fun get(faction: FactionEntity, entity: Entity): Recon = table[faction, entity] ?: Recon.Unknown

    internal fun process(reconChanged: WorldChange.ReconChanged) {
        val (entity, reconByFaction) = reconChanged
        for ((faction, recon) in reconByFaction) {
            table[faction, entity] = recon
        }
        world.notify(reconChanged)
    }

    internal fun invalidate() {
        isValid = false
    }

    internal fun recompute() {
        if (isValid) return
        val factionSeesEntity = world.computeVisibilities()
        for (entity in world.entities) {
            if (!entity.isAlive) continue
            var changes: Object2ObjectArrayMap<FactionEntity, Recon>? = null
            for (faction in factionEntities) {
                val prevRecon = table[faction, entity]
                val visible = factionSeesEntity[faction, entity]
                val newRecon = when {
                    visible && prevRecon != Recon.Visible -> Recon.Visible
                    !visible && prevRecon == Recon.Visible -> Recon.LostSight
                    prevRecon == null -> faction.defaultRecon
                    else -> continue
                }
                if (changes == null) {
                    changes = Object2ObjectArrayMap()
                }
                table[faction, entity] = newRecon
                changes[faction] = newRecon
            }
            if (changes != null) {
                world.notify(WorldChange.ReconChanged(entity, changes))
            }
        }
        isValid = true
    }

    internal fun collectInitialChanges(collector: (WorldChange) -> Unit) {
        for (entity in world.entities) {
            val changes = Object2ObjectArrayMap<FactionEntity, Recon>()
            for (faction in factionEntities) {
                changes[faction] = table[faction, entity] ?: Recon.Unknown
            }
            collector(WorldChange.ReconChanged(entity, changes))
        }
    }
}

/**
 * This entity cannot have a [Recon.Visible] even if it is in line-of-sight.
 */
var Entity.disguised: Boolean by attribute(false)

/**
 * If `true`, every cell shall be visible to any faction regardless of observers.
 */
var World.everythingIsRevealed: Boolean by attribute(false)

private fun World.computeVisibilities(): SparseEntityRelation<FactionEntity, Entity> {
    data class Observer(val entity: Entity, val sightQuery: SightQuery, val strength: Float)
    data class ObservingFaction(val faction: FactionEntity, val observers: List<Observer>)

    val factionSeesEntity = SparseEntityRelation<FactionEntity, Entity>()
    val factionEntities = factionEntities

    val preliminaryObservingFactions = factionEntities.map { faction ->
        ObservingFaction(faction, faction.actors.map { actor ->
            //TODO some actors may have a different movement, e.g., flying actors
            //TODO some actors may have a different sight strength, e.g., by using radar
            Observer(actor, actor.sight(VisibilityExtractor, GroundMovement(actor)), DEFAULT_SIGHT_STRENGTH)
        })
    }
    val observingFactions = factionEntities.map { faction ->
        // The world as a whole, all events and all transients are always visible.
        factionSeesEntity[faction, world] = true
        for (event in events) {
            factionSeesEntity[faction, event] = true
        }
        for (transient in transients) {
            factionSeesEntity[faction, transient] = true
        }
        val allObservers = mutableListOf<Observer>()
        // TODO surveillance cams etc.
        for ((otherFaction, observers) in preliminaryObservingFactions) {
            // Every faction sees every other faction.
            factionSeesEntity[faction, otherFaction] = true
            val relation = otherFaction.relationTo(faction)
            if (relation == Faction.Relation.SAME || relation == Faction.Relation.ALLIED) {
                allObservers.addAll(observers)
                for (observer in observers) {
                    // See observers from the same or allied factions.
                    factionSeesEntity[faction, observer.entity] = true
                }
            }
        }
        ObservingFaction(faction, allObservers)
    }
    val everythingIsVisible = world.everythingIsRevealed
    for (cell in cells) {
        val presentActor = cell.presentActor?.takeUnless { it.disguised }
        for ((faction, observers) in observingFactions) {
            if (everythingIsVisible) {
                factionSeesEntity[faction, cell] = true
                if (presentActor != null) {
                    factionSeesEntity[faction, presentActor] = true
                }
            } else {
                if (observers.any { (_, query, strength) -> query.of(cell, strength).exposure > 0.0 }) {
                    // Add all cells visible to any of the observers for that faction.
                    factionSeesEntity[faction, cell] = true
                    if (presentActor != null) {
                        // See actor present in visible cell unless it is disguised
                        // (allied actors have already been added).
                        factionSeesEntity[faction, presentActor] = true
                    }
                }
            }
        }
    }
    for (item in items) {
        val disguised = item.disguised
        val anchor = item.anchor ?: continue
        val anchorFaction = (anchor as? FactionMember)?.faction
        for (faction in factionEntities) {
            if (factionSeesEntity[faction, anchor]) {
                val relation = anchorFaction?.relationTo(faction)
                if (!disguised || relation == Faction.Relation.SAME || relation == Faction.Relation.ALLIED) {
                    // See items if their anchor can be seen, and if they are either undisguised or the anchor is allied.
                    factionSeesEntity[faction, item] = true
                }
            }
        }
    }
    for (statusEffect in allStatusEffects) {
        val bearer = statusEffect.bearer
        for (faction in factionEntities) {
            if (factionSeesEntity[faction, bearer]) {
                // Status effects of visible bearers are visible, too.
                factionSeesEntity[faction, statusEffect] = true
            }
        }
    }
    return factionSeesEntity
}
