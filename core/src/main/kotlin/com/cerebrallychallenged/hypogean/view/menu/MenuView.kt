package com.cerebrallychallenged.hypogean.view.menu

import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.standardButton
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.messages.ExitCommand
import com.cerebrallychallenged.hypogean.messages.SaveCommand
import com.cerebrallychallenged.hypogean.model.worldFactory
import com.cerebrallychallenged.hypogean.util.SaveGameDateTimeFormat
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.input.InputCommand
import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.input.KeyEvent
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.layout.Visibility.Companion.visibleIf
import java.time.LocalDateTime

object ToggleMenuView : InputCommand("M")  //  Escape for release builds
//object ToggleMenuView2 : InputCommand("Escape") // "M" for local testing in the Unreal editor

class MenuView(context: ViewFactory.Context) : View {
    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): View {
            return MenuView(context)
        }
    }

    internal val mainNode = context.widget.layers[GuiLayer.Overlay].window {
        vgap = 10
        standardButton("Continue", Align.Stretch) {
            toggleMenu()
        }
        standardButton("Save", Align.Stretch) {
            val levelName = context.world.worldFactory?.toString() ?: "Unknown"
            val time = SaveGameDateTimeFormat.format(LocalDateTime.now())
            context.viewModel.client.sendToServer(SaveCommand("$levelName-$time"))
        }
        standardButton("Exit Game", Align.Stretch) {
            context.viewModel.client.sendToServer(ExitCommand())
        }
    }.apply {
        visibility = Visibility.Hidden
    }

    override fun onInput(inputEvent: InputEvent, commands: Collection<InputCommand>) {
        super.onInput(inputEvent, commands)
        if (inputEvent is KeyEvent && inputEvent.kind == KeyEvent.Kind.PRESS) {
            if (ToggleMenuView in commands) {
                toggleMenu()
            }
        }
    }

    private fun toggleMenu() {
        mainNode.visibility = visibleIf(!mainNode.visibility.isVisible)
    }
}
