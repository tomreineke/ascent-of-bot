package com.cerebrallychallenged.hypogean.model

interface FactionMember : Entity, IniHolder {
    var factionEntity: FactionEntity?

    var faction: Faction?
        get() = factionEntity?.faction
        set(value) {
            factionEntity = value?.entity
        }

    fun factionRelationTo(other: Faction?): Faction.Relation
            = this.factionEntity?.relationTo(other) ?: Faction.Relation.NEUTRAL
}
