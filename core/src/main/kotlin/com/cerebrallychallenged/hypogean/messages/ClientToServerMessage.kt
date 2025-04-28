package com.cerebrallychallenged.hypogean.messages

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionInstanceId
import com.cerebrallychallenged.hypogean.model.attribute.Attribute
import com.cerebrallychallenged.hypogean.model.containment.ItemMove

sealed class ClientToServerMessage


internal class ClaimFaction(val faction: Faction) : ClientToServerMessage()

internal class AckUpdate(val updateId: Long) : ClientToServerMessage()

internal class AdminCommand(val command: String, val parameter: ByteArray) : ClientToServerMessage()

internal class SaveCommand(val name: String) : ClientToServerMessage()

internal class ExitCommand : ClientToServerMessage()

//internal class SelectDialogOption(
//        val dialogId: DialogId,
//        val actor: Actor,
//        val dialogNodeIndex: Int
//) : ClientToServerMessage() {
//    companion object : StreamableCompanion<ClientToServerMessage, World> {
//        override fun DataInput.read(context: World): ClientToServerMessage = SelectDialogOption(
//                context.rulebook.dialogIds.objectForId(readString()),
//                readEntity(context),
//                readInt()
//        )
//    }
//
//    override fun DataOutput.writeThis() {
//        writeString(actor.rulebook.dialogIds.idForObject(dialogId))
//        writeEntity(actor)
//        writeInt(dialogNodeIndex)
//    }
//}

internal class SubmitAction private constructor(
        val actionInstanceId: ActionInstanceId,
        val activeActorStateId: Long
) : ClientToServerMessage() {
    constructor(actionInstance: ActionInstance, activeActorState: ActiveActorState) : this(
            actionInstance.id,
            activeActorState.id
    )
}

internal class ExpandPartialAction(
        val actionInstanceId: ActionInstanceId,
        val activeActorStateId: Long
) : ClientToServerMessage()

internal class MoveItems(val moves: List<ItemMove>) : ClientToServerMessage() {
    init {
        require(moves.isNotEmpty())
    }
}

internal class SetAttribute<T>(
        val entity: Entity,
        val attribute: Attribute<T>,
        val value: T
) : ClientToServerMessage()

internal class SubmitDialogOption(val optionUuid: String) : ClientToServerMessage()
