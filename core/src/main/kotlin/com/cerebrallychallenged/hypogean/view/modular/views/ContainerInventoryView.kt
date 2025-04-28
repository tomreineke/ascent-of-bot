package com.cerebrallychallenged.hypogean.view.modular.views

import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.SlotBearer
import com.cerebrallychallenged.hypogean.vanilla.actions.OpenInventoryActionInstance
import com.cerebrallychallenged.hypogean.view.ViewActionExecuted
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.modular.ModularView

data class InventoryPair(val left: SlotBearer, val right: SlotBearer)

data class ActorContainerPair(val actor: Actor, val container: Item)

// TODO maybe use for TradingView in the future
data class ActorActorPair(val left: Actor, val right: Actor)

class ContainerInventoryView(context: ViewFactory.Context) : ModularView<ActorContainerPair>(context) {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DefaultStyle = Styling<Window, Unit> {
//            minWidth = 1500.scaled
//            minHeight = 1000.scaled
        }

        var style: Styling<Window, Unit> = DefaultStyle
    }

    class Factory : ModularView.Factory<ActorContainerPair>(::ContainerInventoryView)

    override val windowStyle: Styling<Window, Unit>
        get() = InfoView.Style.style

    override suspend fun onViewModelChange(change: ViewModelChange) {
        super.onViewModelChange(change)
        if (change is ViewActionExecuted) {
            val viewAction = change.viewActionInstance
            if (viewAction is OpenInventoryActionInstance) {
                val activeActor = viewAction.activeActor
                val openedContainer = viewAction.target
                show(ActorContainerPair(activeActor, openedContainer))
            }
        }
    }
}
