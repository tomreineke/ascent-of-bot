package com.cerebrallychallenged.hypogean.server

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.activestate.ActiveDialog
import com.cerebrallychallenged.hypogean.activestate.GameOverReason
import com.cerebrallychallenged.hypogean.activestate.GameOverState
import com.cerebrallychallenged.hypogean.activestate.SimulationState
import com.cerebrallychallenged.hypogean.activestate.SimulationState.simulateNextStep
import com.cerebrallychallenged.hypogean.client.ClientConnector
import com.cerebrallychallenged.hypogean.messages.AckUpdate
import com.cerebrallychallenged.hypogean.messages.AdminCommand
import com.cerebrallychallenged.hypogean.messages.ClaimFaction
import com.cerebrallychallenged.hypogean.messages.ClientToServerMessage
import com.cerebrallychallenged.hypogean.messages.ErrorMessage
import com.cerebrallychallenged.hypogean.messages.ExitCommand
import com.cerebrallychallenged.hypogean.messages.ExpandPartialAction
import com.cerebrallychallenged.hypogean.messages.ExpandedAction
import com.cerebrallychallenged.hypogean.messages.HelloClient
import com.cerebrallychallenged.hypogean.messages.MoveItems
import com.cerebrallychallenged.hypogean.messages.SaveCommand
import com.cerebrallychallenged.hypogean.messages.ServerToClientMessage
import com.cerebrallychallenged.hypogean.messages.SetAttribute
import com.cerebrallychallenged.hypogean.messages.SubmitAction
import com.cerebrallychallenged.hypogean.messages.SubmitDialogOption
import com.cerebrallychallenged.hypogean.messages.WorldUpdate
import com.cerebrallychallenged.hypogean.model.ChangeScheduleDto
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.Rulebook
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.npc.NpcContext
import com.cerebrallychallenged.hypogean.npc.SkipBehavior
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.npc.behaviorBytes
import com.cerebrallychallenged.hypogean.util.gameSavePath
import com.cerebrallychallenged.hypogean.util.kryo.WorldKryo
import com.cerebrallychallenged.hypogean.util.kryo.findDispatcher
import com.cerebrallychallenged.hypogean.util.saveWorld
import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.log.log
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.common.util.concurrent.ThreadFactoryBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume

internal fun CoroutineScope.launchServer(
        rulebook: Rulebook
) : Server {
    val server = Server(rulebook)
    val threadFactoryBuilder = ThreadFactoryBuilder().setNameFormat("server-thread-%d").build()
    launch(Executors.newSingleThreadExecutor(threadFactoryBuilder).asCoroutineDispatcher()) {
        server.run()
    }
    return server
}

// How many steps may the server compute in advance before the clients receive their ACK?
private const val PRECOMPUTED_UPDATE_COUNT = 10

internal class Server(private val rulebook: Rulebook) {
    private inner class ClientRep(val clientId: Long, val send: (ByteArray) -> Unit, val isPlayer: Boolean) {
        var updateId: Long = 0

        var faction: Faction? = null
            private set

        var saveInProgress: Boolean? = null

        fun send(serverToClientMessage: ServerToClientMessage) {
            send(kryo.serializeToByteArray(serverToClientMessage))
        }

        fun sendWorldUpdate(changeSchedule: ChangeScheduleDto, updateId: Long) {
            if (faction != null) {
                send(WorldUpdate(updateId, changeSchedule))
            }
        }

        suspend fun receive(message: ClientToServerMessage) {
            when (message) {
                is ClaimFaction -> {
                    val faction = message.faction
                    this.faction?.let {
                        send(ErrorMessage(
                                "Cannot set faction type twice (previously $it, now $faction)"
                        ))
                        return
                    }
                    this.faction = faction
                    clientsByFaction.put(faction, this)
                    dispatchWorldUpdate(world.flush())
                    sendWorldUpdate(world.createInitialWorldChanges(), lastUpdateId)
                    world.maybeSimulateNextStep()
                }
                is AckUpdate -> {
                    updateId = message.updateId
                    world.maybeSimulateNextStep()
                }
                is AdminCommand -> {
                    //
                }
                is SubmitAction -> {
                    submitAction(message)
                }
                is ExpandPartialAction -> {
                    val activeState = world.activeState
                    if (activeState is ActiveActorState && activeState.id == message.activeActorStateId) {
                        val children = activeState.expandAction(message.actionInstanceId)
                        send(ExpandedAction(message.actionInstanceId, children))
                    }
                }
                is SubmitDialogOption -> {
                    submitDialogSelection(message)
                }
                is MoveItems -> {
                    // TODO[A] check if client is allowed to move that items
                    for (move in message.moves) {
                        move.movedItem.containerPosition = move.newContainerPosition
                    }
                    dispatchWorldUpdate(world.flush())
                }
                is SetAttribute<*> -> {
                    fun <T> SetAttribute<T>.apply() {
                        attribute.setValue(entity, value)
                    }
                    message.apply()
                    val prevState = world.activeState
                    if (prevState is ActiveActorState) {
                        world.activeState = prevState.recompute()
                    }
                    dispatchWorldUpdate(world.flush())
                }
                is SaveCommand -> {
                    saveInProgress = true
                    saveWorld(world, gameSavePath(message.name))
                    // Adding a progress indicator would be nice
                    saveInProgress = false
                }
                is ExitCommand -> {
                    while (saveInProgress == true) {
                        delay(1000)
                    }
                    JunManager.quitGame()
                }
            }
        }
    }

    private val messages = Channel<suspend (CoroutineScope) -> Unit>(UNLIMITED)

    internal val world: World = rulebook.createWorld(isPrimary = true)

    private val kryo = world.kryo.apply {
        registerSubstitutedClass<NpcContext>()
        registerSubstitutedClass<Server>()
    }

    private val clients = mutableListOf<ClientRep>()

    private val clientsByFaction: Multimap<Faction, ClientRep> = HashMultimap.create()

    /**
     * All factions controlled by player client.
     */
    private val playerControlledFactions: Sequence<Faction>
        get() = rulebook.factions.asSequence().filter { faction -> clientsByFaction.get(faction).any { it.isPlayer } }

    private var lastUpdateId: Long = 0L

    fun addClient(
            connector: ClientConnector,
            isPlayer: Boolean
    ) {
        messages.trySend { scope ->
            val client = ClientRep(clients.size.toLong() + 1, connector.sendToClient, isPlayer)
            clients.add(client)
            client.send(HelloClient(client.clientId))
            scope.launch {
                connector.clientToServerMessages.collect { clientToServerMessage ->
                    enqueueClientMessage(client, clientToServerMessage)
                }
            }
        }
    }

    private suspend fun submitAction(message: SubmitAction) {
        pendingNpcResponse = false
        val activeState = world.activeState
        if (activeState is ActiveActorState && activeState.id == message.activeActorStateId) {
            with(activeState) {
                world.performAction(message.actionInstanceId).forEach { dispatchWorldUpdate(it) }
                world.maybeSimulateNextStep()
            }
        }
    }

    private suspend fun submitDialogSelection(message: SubmitDialogOption) {
        val activeState = world.activeState
        if (activeState is ActiveDialog) {
            val continuation = activeState.continuation
            if (continuation is Dialog.Select) {
                val newActiveState = continuation.select(activeState, message.optionUuid)
                if (newActiveState != null) {
                    world.activeState = newActiveState
                    dispatchWorldUpdate(world.flush())
                    world.maybeSimulateNextStep()
                }
            }
        }
    }

    private fun enqueueClientMessage(client: ClientRep, clientToServerMessage: ByteArray) {
        messages.trySend {
            client.receive(kryo.deserializeFromByteArray(clientToServerMessage))
        }
    }

    fun loadWorld(setup: World.() -> Unit) {
        messages.trySend {
            world.clear(0)
            world.setup()
            dispatchWorldUpdate(world.flush())
            log.info { "Loaded world. #entitites=${world.entities.size} #attributes=${rulebook.attributes.size}" }
            log.info { "#changed attribute values=${world.attributeStore.changedValueCount}" }
        }
    }

    suspend fun run() {
        coroutineScope {
            messages.consumeAsFlow().collect { message -> message(this) }
        }
    }

    private fun dispatchWorldUpdate(changeSchedule: ChangeScheduleDto) {
        if (changeSchedule.isEmpty()) return
        ++lastUpdateId
        for (client in clients) {
            client.sendWorldUpdate(changeSchedule, lastUpdateId)
        }
    }

    private suspend fun World.maybeSimulateNextStep() {
        // Any players connected?
        if (playerControlledFactions.none()) return

        val activeState = activeState

        when {
            activeState is ActiveActorState -> {
                val activeFaction = activeState.activeActor.faction
                if (activeFaction != null && activeFaction !in playerControlledFactions) {
                    executeNpcBehavior(activeState, activeFaction)
                }
                return
            }
            activeState is ActiveDialog && activeState.continuation !is Dialog.NodeOrEnd -> {
                val dialogContinuation = activeState.continuation
                if (dialogContinuation is Dialog.Select) {
                    val selectingActor = dialogContinuation.selectingActor
                    val selectingFaction = selectingActor.faction
                    if (selectingFaction != null && selectingFaction !in playerControlledFactions) {
                        val selectedOption = selectingActor.behavior.select(dialogContinuation)
                        messages.trySend {
                            submitDialogSelection(SubmitDialogOption(selectedOption.uuid))
                        }
                    }
                }
                return
            }
        }

        val minUpdateId = clients.asSequence().map { it.updateId }.minOrNull() ?: Long.MAX_VALUE

        // None of the clients lags behind by more than PRECOMPUTED_UPDATE_COUNT?
        // Otherwise do not perform another step to prevent congestion.
        if (lastUpdateId - PRECOMPUTED_UPDATE_COUNT > minUpdateId) return

        with(activeState) {
            when (this) {
                is SimulationState -> {
                    val gameOverReason = GameOverReason.determine(playerControlledFactions.toList())
                    if (gameOverReason != null) {
                        this@maybeSimulateNextStep.activeState = GameOverState(gameOverReason, world.currentIniTime)
                        dispatchWorldUpdate(flush())
                    } else {
                        simulateNextStep().forEach {
                            dispatchWorldUpdate(it)
                        }
                    }
                }
                is ActiveDialog -> {
                    require(continuation is Dialog.NodeOrEnd)
                    this@maybeSimulateNextStep.activeState = continuation.proceed(this)
                    dispatchWorldUpdate(flush())
                }
            }
        }
    }

    private var pendingNpcResponse: Boolean = false

    private suspend fun executeNpcBehavior(activeActorState: ActiveActorState, ownFaction: Faction) {
        if (pendingNpcResponse) return
        pendingNpcResponse = true
        val npcContext = NpcContext(
            kryo,
            world,
            activeActorState,
            ownFaction
        ) { actionInstance ->
            messages.trySend {
                submitAction(SubmitAction(actionInstance, activeActorState))
            }
        }
        val coroutineContext = coroutineContext

        val activeActor = activeActorState.activeActor
        val behaviorBytes = activeActor.behaviorBytes
        val continuation = if (behaviorBytes == null) {
            val behavior = activeActor.behavior
            suspend {
                with(behavior) {
                    npcContext.run()
                }
            }.createCoroutine(Continuation(coroutineContext) {
                it.exceptionOrNull()?.let { exception ->
                    log.error { exception.stackTraceToString() }
                    activeActor.behavior = SkipBehavior
                }
                activeActor.behaviorBytes = null
                pendingNpcResponse = false
                this.messages.trySend {
                    world.maybeSimulateNextStep()
                }
            })
        } else {
            kryo.putSubstitution(npcContext)
            kryo.putSubstitution(coroutineContext.findDispatcher())
            kryo.putSubstitution(Thread.currentThread())
            kryo.putSubstitution(WorldKryo.CoroutineContextKey, coroutineContext)

            kryo.putSubstitution(this)
            kryo.deserializeFromByteArray(behaviorBytes)
        }
        continuation.resume(Unit)
    }
}

suspend fun measureSuspending(id: String, block: suspend () -> Unit) {
    val start = System.nanoTime()
    val idWithUUID = "$id-${UUID.randomUUID()}"
    log.info { "######### Before $idWithUUID" }
    block()
    log.info { "######### After $idWithUUID: ${(System.nanoTime() - start) / 1_000_000_000} seconds." }
}

fun measure(id: String, block: () -> Unit) {
    val start = System.nanoTime()
    val idWithUUID = "$id-${UUID.randomUUID()}"
    log.info { "######### Before $idWithUUID" }
    block()
    log.info { "######### After $idWithUUID: ${(System.nanoTime() - start) / 1_000_000_000} seconds." }
}
