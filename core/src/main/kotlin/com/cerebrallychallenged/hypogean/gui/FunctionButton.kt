package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.hypogean.gui.GuiConfig.HoveredColorFilterPaint
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.node.Node

open class FunctionButton : Node() {
    var actionListener: (() -> Unit)? = null

    init {
        primaryPressedListeners += { _, _ ->
            true
        }
        primaryReleasedListeners += { _, hoveredNode, _ ->
            if (isAncestorOf(hoveredNode)) {
                actionListener?.invoke()
            }
            true
        }
    }
}

class CloseButton : FunctionButton() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val Image = ImageResource("Images/gui/close_button.png")
        val PressedImage = ImageResource("Images/gui/close_button_pressed.png")

        val BackgroundOvershoot = IRect(2, 2, 5, 5)
        const val Size = 200 - 2 - 5

        val Background = BackgroundImageResource(Image, BackgroundOvershoot)
        val HoveredBackground = Background.copy(paint = HoveredColorFilterPaint)
        val PressedBackground = BackgroundImageResource(PressedImage, BackgroundOvershoot)
        val HoveredPressedBackground = PressedBackground.copy(paint = HoveredColorFilterPaint)

        val DefaultButtonStyle = Styling<FunctionButton, Unit> {
            scaling(0.66f) {
                background[InputState.Empty] = ResourceLibrary[Background, guiScale]
                background[InputState.Hovered] = ResourceLibrary[HoveredBackground, guiScale]
                background[InputState.Pressed] = ResourceLibrary[PressedBackground, guiScale]
                background[InputState.Hovered, InputState.Pressed] = ResourceLibrary[HoveredPressedBackground, guiScale]
                val size = Size.scaled
                minWidth = size
                maxWidth = size
                minHeight = size
                maxHeight = size
                hitModel = HitModel.RoundedRect(IRect.Empty, size / 2)
            }
        }

        var style: Styling<FunctionButton, Unit> = DefaultButtonStyle
    }

    init {
        applyStyle(Style.style)
    }
}
