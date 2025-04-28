package com.cerebrallychallenged.hypogean.view.modular.modules

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.activestate.ActiveWorldState
import com.cerebrallychallenged.hypogean.gui.StandardButton
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.vanilla.actions.CollectAllItemsAction
import com.cerebrallychallenged.hypogean.view.modular.ModuleContext
import com.cerebrallychallenged.hypogean.view.modular.ViewModule
import com.cerebrallychallenged.jun.skiatree.node.Node

context(ModuleContext)
class CollectAllInventoryModule(private val inventory: Item) : ViewModule() {
    private val viewModel = context.viewModel

    private var collectAllActionInstance: ActionInstance? = null

    override val mainNode: Node = StandardButton("Collect All") {
        collectAllActionInstance?.let { viewModel.submitAction(it) }
    }

    private val changeListener = object : WorldChange.SimpleVisitor {
        override fun visit(change: WorldChange.ActiveStateChanged) {
            updateActiveState(change.activeState)
        }
    }

    override fun onChange(change: WorldChange) {
        change.accept(changeListener)
    }

    private fun updateActiveState(state: ActiveWorldState?) {
        collectAllActionInstance = if (state is ActiveActorState && state.activeActor.isOwn) {
            state.availableActions
                .groupedByAction[CollectAllItemsAction]
                .groupedByTarget[inventory]
                .instances.firstOrNull()
        } else null
    }

    init {
        updateActiveState(context.world.activeState)
    }
}
