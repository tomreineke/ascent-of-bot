package com.cerebrallychallenged.hypogean.client

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.messages.AckUpdate
import com.cerebrallychallenged.hypogean.messages.ClaimFaction
import com.cerebrallychallenged.hypogean.messages.ClientToServerMessage
import com.cerebrallychallenged.hypogean.messages.ErrorMessage
import com.cerebrallychallenged.hypogean.messages.ExpandPartialAction
import com.cerebrallychallenged.hypogean.messages.ExpandedAction
import com.cerebrallychallenged.hypogean.messages.HelloClient
import com.cerebrallychallenged.hypogean.messages.ServerToClientMessage
import com.cerebrallychallenged.hypogean.messages.WorldUpdate
import com.cerebrallychallenged.hypogean.model.ChangeScheduleDto
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.Rulebook
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionInstanceId
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume

internal class Client(private val world: World, val faction: Faction) {
    val rulebook: Rulebook = world.rulebook

    private var clientId: Long = 0

    private val serverToClientMessages: Flow<ByteArray>

    private val senderToServer: SendChannel<ByteArray>

    val connector: ClientConnector

    init {
        val serverToClient = Channel<ByteArray>(UNLIMITED)
        serverToClientMessages = serverToClient.consumeAsFlow()

        senderToServer = Channel(UNLIMITED)
        val clientToServerMessages = senderToServer.consumeAsFlow()

        connector = ClientConnector(serverToClient::trySend, clientToServerMessages)
    }

    private val partialActionExpansionRequests =
            ConcurrentHashMap<ActionInstanceId, CancellableContinuation<ActionTable>>()

    fun sendToServer(message: ClientToServerMessage) {
        senderToServer.trySend(world.kryo.serializeToByteArray(message))
    }

    suspend fun getChildrenOf(
        world: World,
        partialActionInstance: ActionInstance
    ): ActionTable {
        return partialActionInstance.children ?: let {
            suspendCancellableCoroutine {
                val id = partialActionInstance.id
                partialActionExpansionRequests[id] = it
                val activeActorState =
                    world.activeState as? ActiveActorState ?: error("Action expansion requires ActiveActorState")
                sendToServer(ExpandPartialAction(id, activeActorState.id))
            }.also {
                partialActionInstance.children = it
            }
        }
    }

    val worldUpdates: Flow<ChangeScheduleDto> = serverToClientMessages.transform {
        when (val message = world.kryo.deserializeFromByteArray<ServerToClientMessage>(it)) {
            is HelloClient -> {
                clientId = message.clientId
                sendToServer(ClaimFaction(faction))
            }
            is ErrorMessage -> {
                throw ServerMessagingException("Server sent error message: ${message.errorMessage}")
            }
            is WorldUpdate -> {
                sendToServer(AckUpdate(message.updateId))
                emit(message.changeSchedule)
            }
            is ExpandedAction -> {
                val continuation =
                    partialActionExpansionRequests.remove(message.actionInstanceId)
                        ?: throw ServerMessagingException(
                            "Unexpected expansion for action instance ${message.actionInstanceId}"
                        )
                continuation.resume(message.children)
            }
        }
    }
}
