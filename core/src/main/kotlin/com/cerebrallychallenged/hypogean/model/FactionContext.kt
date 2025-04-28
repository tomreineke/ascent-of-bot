package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.model.action.ActionTable

interface FactionContext {
    val ownFactionEntity: FactionEntity

    val ownFaction: Faction
        get() = ownFactionEntity.faction

    val Entity.recon: Recon
        get() = ownFactionEntity.reconOf(this)

    val FactionMember.factionRelation: Faction.Relation
        get() = ownFactionEntity.relationTo(factionEntity)

    val FactionMember.isOwn: Boolean
        get() = factionRelation == Faction.Relation.SAME

    val FactionMember.isOwnOrAllied: Boolean
        get() = factionRelation.isSameOrAllied

    val WorldChange.ActiveStateChanged.ownActions: ActionTable?
        get() {
            val (actor, actions) = activeState as? ActiveActorState ?: return null
            return actions.takeIf { actor.factionEntity == ownFactionEntity }
        }
}
