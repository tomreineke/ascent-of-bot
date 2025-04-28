package com.cerebrallychallenged.hypogean.npc

import com.cerebrallychallenged.hypogean.client.Client
import com.cerebrallychallenged.hypogean.messages.MoveItems
import com.cerebrallychallenged.hypogean.messages.SetAttribute
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.attribute.Attribute
import com.cerebrallychallenged.hypogean.model.containment.ItemMove

/**
 * With this class we only want to send updates to the server that may not cause cascades of other changes.
 * E. g. switch a weapon from one inventory slot to another. But NOT set health of an explosive actor to zero.
 */
class NpcInteractions internal constructor(private val client: Client) {
    fun moveItems(moves: List<ItemMove>) {
        client.sendToServer(MoveItems(moves))
    }

    suspend fun getChildrenOf(activeActor: Actor, partialActionInstance: ActionInstance): ActionTable =
            client.getChildrenOf(activeActor.world, partialActionInstance)

//    fun selectDialogOption(dialogId: DialogId, actor: Actor, dialogNodeIndex: Int) {
//        client.sendToServer(SelectDialogOption(dialogId, actor, dialogNodeIndex))
//    }

    fun <T> setAttribute(entity: Entity, attribute: Attribute<T>, value: T) {
        client.sendToServer(SetAttribute(entity, attribute, value))
    }
}
