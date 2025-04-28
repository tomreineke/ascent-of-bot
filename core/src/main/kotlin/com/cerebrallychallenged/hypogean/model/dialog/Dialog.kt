package com.cerebrallychallenged.hypogean.model.dialog

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.activestate.ActiveDialog
import com.cerebrallychallenged.hypogean.activestate.ActiveWorldState
import com.cerebrallychallenged.hypogean.activestate.DialogContext
import com.cerebrallychallenged.hypogean.activestate.SimulationState
import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.executeCascade
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.util.randomUUID
import java.util.Objects.hash

abstract class Dialog {
    interface Role<T : Entity> {
        infix fun playedBy(entity: T): RoleMap.Entry<T> = RoleMap.Entry(this, entity)
    }

    abstract inner class Continuation {
        val dialog: Dialog
            get() = this@Dialog
    }

    abstract inner class NodeOrEnd : Continuation() {
        abstract fun proceed(activeDialog: ActiveDialog): ActiveWorldState
    }

    inner class End(val initiativeCost: InitiativeCost = InitiativeCost.KeepTurn) : NodeOrEnd() {
        override fun proceed(activeDialog: ActiveDialog): ActiveWorldState {
            val activeActor = activeDialog.activeActor
            return if (activeActor != null) {
                when (initiativeCost) {
                    is InitiativeCost.KeepTurn -> ActiveActorState(activeActor)
                    is InitiativeCost.Delta -> {
                        activeActor.world.iniQueue.enqueueRelative(initiativeCost.rounds, activeActor)
                        SimulationState
                    }
                    else -> SimulationState
                }
            } else {
                SimulationState
            }
        }
    }

    inner class Node internal constructor(internal val block: suspend context(DialogContext) () -> Continuation) : NodeOrEnd() {
        val index = nodes.size

        override fun proceed(activeDialog: ActiveDialog): ActiveWorldState {
            var continuation: Dialog.Continuation? = null
            with(activeDialog.world) {
                executeCascade {
                    cascadeBlock {
                        continuation = block(DialogContext(activeDialog, this))
                    }
                }
            }
            return activeDialog.copy(continuation = requireNotNull(continuation))
        }

        override fun equals(other: Any?): Boolean = other is Node && dialog == other.dialog && index == other.index

        override fun hashCode(): Int = hash(dialog, index)
    }

    inner class Select internal constructor(val selectingActor: Actor, val node: Node) : Continuation() {
        inner class Option(
            val then: NodeOrEnd,
            internal val isSpeech: Boolean,
            internal val text: String,
            val uuid: String = randomUUID()
        ) {
            var available: Boolean = true
                internal set

            fun availableIf(/*blocker: OptionBlocker, */precondition: () -> Boolean) {
                available = precondition()
            }
        }

        internal val options = mutableListOf<Option>()

        fun optionLeadingTo(then: NodeOrEnd): Option =
            options.firstOrNull { it.then == then } ?: modelError("No option found leading to $then")

        context(DialogContext)
        fun say(then: NodeOrEnd, text: String): Option = Option(
            then,
            true,
            text
        ).also { options.add(it) }


        context(DialogContext)
        fun act(then: NodeOrEnd, text: String): Option = Option(
            then,
            false,
            text
        ).also { options.add(it) }

        internal fun select(
            activeDialog: ActiveDialog,
            optionUuid: String
        ): ActiveWorldState? = with(activeDialog.world) {
            options.firstOrNull { it.uuid == optionUuid }?.let {
                activeDialog.select(selectingActor, it)
            }
        }

        override fun equals(other: Any?): Boolean = other is Select && node == other.node

        override fun hashCode(): Int = node.hashCode()
    }

    private data class OptionPrecondition(/*val blocker: OptionBlocker, */val precondition: () -> Boolean)

//    sealed class OptionBlocker {
//        data class Reason(val reason: String)
//        object OpenSecret : OptionBlocker()
//        object Hidden : OptionBlocker()
//    }

    private val nodes = mutableListOf<Node>()

    internal fun nodeByIndex(index: Int): Node = nodes[index]

    fun node(block: suspend context(DialogContext) () -> Continuation): Node = Node(block).also { nodes.add(it) }

    abstract val start: Node

    val end: End = End()

    fun end(initiativeCost: InitiativeCost): Dialog.End = End(initiativeCost)

    abstract fun determineRoles(activeActor: Actor, addressee: Entity): RoleMap

    internal fun initiate(world: World, roles: RoleMap, activeActor: Actor?): ActiveDialog =
        ActiveDialog(world, this, roles, activeActor, start)
}

class Dialogs : SimpleObjectRegistry<Dialog>()

class DialogRoles : SimpleObjectRegistry<Dialog.Role<*>>()
