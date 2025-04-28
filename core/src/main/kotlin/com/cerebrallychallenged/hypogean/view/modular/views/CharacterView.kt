package com.cerebrallychallenged.hypogean.view.modular.views

import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.activeActor
import com.cerebrallychallenged.hypogean.view.ViewActionExecuted
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.input.InputCommand
import com.cerebrallychallenged.hypogean.view.modular.ModularView
import com.cerebrallychallenged.hypogean.view.modular.actions.CharacterViewActionInstance
import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.input.KeyEvent

object ToggleCharacterView : InputCommand("C")
object ToggleInventoryView : InputCommand("I")

class CharacterView(context: ViewFactory.Context): ModularView<Actor>(context) {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DefaultStyle = Styling<Window, Unit> {
//            minWidth = 1500.scaled
//            minHeight = 1000.scaled
        }

        var style: Styling<Window, Unit> = DefaultStyle
    }

    class Factory : ModularView.Factory<Actor>(::CharacterView)

    override val windowStyle: Styling<Window, Unit>
        get() = Style.style

    override suspend fun onViewModelChange(change: ViewModelChange) {
        super.onViewModelChange(change)
        if (change is ViewActionExecuted) {
            val instance = change.viewActionInstance
            if (instance is CharacterViewActionInstance) {
                show(instance.target)
            }
        }
    }

    override fun onInput(inputEvent: InputEvent, commands: Collection<InputCommand>) {
        super.onInput(inputEvent, commands)
        if (inputEvent is KeyEvent && inputEvent.kind == KeyEvent.Kind.PRESS) {
            if (ToggleCharacterView in commands || ToggleInventoryView in commands) {
                val activeActor = world.activeActor
                if (activeActor != null && activeActor.faction == viewModel.clientFaction) {
                    if (!isVisible) {
                        show(activeActor)
                    } else {
                        hide()
                    }
                }
            }
        }
    }
}
