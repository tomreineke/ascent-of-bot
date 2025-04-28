package com.cerebrallychallenged.hypogean.view.radialmenu

import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.StandardButton
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.PickupActionInstance
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.hypogean.view.PickupRadialViewDisplay
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory

class RadialMenuView(context: ViewFactory.Context) : View, FactionContext by context {

    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): RadialMenuView = RadialMenuView(context)
    }

    private val widget = context.widget

    private val viewModel = context.viewModel

    private val buttons = mutableListOf<StandardButton>()

    private fun updatePickupSelectionMenu(instances: List<PickupActionInstance>) {
        buttons.clear()
        val mousePosition = widget.mousePosition
        val currentChildren = widget.layers[GuiLayer.Overlay].children
        for ((index, action) in instances.withIndex()) {
            StandardButton(
                "Pick up ${action.itemToPickUp.name}",
                actionListener = {
                    viewModel.submitAction(action)
                    for (button in buttons) {
                        button.detach()
                    }
                }
            ).apply {
                top = mousePosition.y + index * (150.scaled + 10)
                left = mousePosition.x
                buttons.add(this)
                currentChildren.add(this)
            }
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        if (change is PickupRadialViewDisplay) {
            updatePickupSelectionMenu(change.instances.filterIsInstance<PickupActionInstance>())
        } else if (change is ActionInputStateChanged && change.hasSelectionChanged) {
            // if something else is selected hide the PickupRadialViewDisplay
            val currentChildren = widget.layers[GuiLayer.Overlay].children
            for (button in buttons) {
                currentChildren.remove(button)
            }
            buttons.clear()
        }
    }
}
