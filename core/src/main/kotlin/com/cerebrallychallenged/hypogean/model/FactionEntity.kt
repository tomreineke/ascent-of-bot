package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.attribute.attribute

class FactionEntity(initializer: Initializer) : SlotBearer(initializer), FactionContext {
    override val ownFactionEntity: FactionEntity
        get() = this

    val faction: Faction = (initializer as FactionInitializer).faction

    private val _actors: MutableSet<Actor> = linkedSetOf()

    val actors: Set<Actor>
        get() = _actors

    var relations: Map<Faction, Faction.Relation> by attribute(mapOf())

    var defaultRecon: Recon by attribute(Recon.Unknown)

    /**
     * Must only be called from [World.clear].
     */
    internal fun clear() {
        _actors.clear()
    }

    /**
     * Must only be called from Actor.
     */
    internal fun internalAddActor(actor: Actor) = _actors.add(actor)

    /**
     * Must only be called from Actor.
     */
    internal fun internalRemoveActor(actor: Actor) = _actors.remove(actor)

    override fun remove() {
        for (actor in _actors.toList()) {
            actor.faction = null
        }
        super.remove()
    }

    override fun collectInitialChanges(collector: (WorldChange) -> Unit) {
        super.collectInitialChanges(collector)
        for (actor in _actors) {
            collector(WorldChange.FactionMembershipChanged(actor, faction))
        }
    }
}

/**
 * Must only be used in the primary world, returns the recon of the specified entity for this faction.
 */
fun FactionEntity.reconOf(entity: Entity): Recon = world.reconTable[this, entity]

internal fun FactionEntity?.relationTo(other: Faction?): Faction.Relation {
    return when {
        this == null || other == null -> Faction.Relation.HOSTILE
        this.faction === other -> Faction.Relation.SAME
        else -> {
            relations[other] ?: Faction.Relation.NEUTRAL
        }
    }
}

internal fun FactionEntity?.relationTo(other: FactionEntity?): Faction.Relation = relationTo(other?.faction)

internal fun FactionEntity.setRelationTo(other: Faction, relation: Faction.Relation) {
    relations += other to relation
    other.entity.relations += this@setRelationTo.faction to relation
}
