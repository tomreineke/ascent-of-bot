package com.cerebrallychallenged.hypogean.npc

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.util.kryo.WorldKryo
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class NpcContext(
    private val kryo: WorldKryo,
    override val world: World,
    activeActorState: ActiveActorState,
    ownFaction: Faction,
    private val submitAction: (ActionInstance) -> Unit,
) : WorldContext, FactionContext {
    override val ownFactionEntity: FactionEntity = ownFaction.entity

    val activeActor = activeActorState.activeActor

    val availableActions: ActionTable = activeActorState.availableActions

    val random: Random
        get() = world.random

    val hostileActors: List<Actor> by lazy {
        world.actors.filter { it.factionRelation == Faction.Relation.HOSTILE && it.isLocated }
    }

    val alliedActors: List<Actor> by lazy {
        world.actors.filter {
            val factionRelation = it.factionRelation
            (factionRelation == Faction.Relation.SAME || factionRelation == Faction.Relation.ALLIED) && it.isLocated
        }
    }

    val locatedActors: List<Actor> by lazy {
        world.actors.filter { it.isLocated }
    }

    suspend fun submit(actionInstance: ActionInstance): Unit = suspendCoroutine { continuation ->
        val byteArray = kryo.serializeToByteArray(continuation)
        activeActor.behaviorBytes = byteArray
        submitAction(actionInstance)
    }

    fun childrenOf(partialActionInstance: ActionInstance): ActionTable = partialActionInstance.expand()
}
