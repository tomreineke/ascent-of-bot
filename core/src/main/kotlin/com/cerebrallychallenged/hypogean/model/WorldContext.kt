package com.cerebrallychallenged.hypogean.model

interface WorldContext : RulebookContext {
    val world: World

    override val rulebook: Rulebook
        get() = world.rulebook

    val Faction.entity: FactionEntity
        get() = world.factionEntity(this)

    fun Faction?.relationTo(other: Faction?): Faction.Relation = this?.entity.relationTo(other)

    fun Faction?.relationTo(other: FactionEntity?): Faction.Relation = this?.entity.relationTo(other)

    fun Faction.setRelationTo(other: Faction, relation: Faction.Relation) {
        entity.setRelationTo(other, relation)
    }

    fun Faction.reconOf(entity: Entity): Recon = this.entity.reconOf(entity)

    fun FactionMember.isOwnOrAlliedFor(faction: Faction): Boolean =
        factionEntity?.let { factionEntity -> faction.relationTo(factionEntity).isSameOrAllied } ?: false
}

inline fun <R> withWorld(world: World, trunk: WorldContext.() -> R): R {
    return object : WorldContext {
        override val world: World = world
    }.trunk()
}
