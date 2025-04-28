package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.hypogean.gui.GuiConfig.HoveredColorFilterPaint
import com.cerebrallychallenged.hypogean.gui.GuiConfig.guiScale
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Margin
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.skiatree.node.NodeCore
import com.cerebrallychallenged.jun.skiatree.text.buildParagraph
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import com.cerebrallychallenged.jun.unreal.sound.USoundCue

class StandardButton(
    caption: String,
    horizontalAlign: Align = Align.Min,
    actionListener: (() -> Unit)? = null
) : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val ClickSound = UnrealRef<USoundCue>("/Script/Engine.SoundCue'/Engine/VREditor/Sounds/VR_click1_Cue.VR_click1_Cue'")

        lateinit var clickSound: USoundCue

        val Image = ImageResource("Images/gui/standard_button.png")

        val PressedImage = ImageResource("Images/gui/standard_button_pressed.png")

        val NinePatchOvershoot = IRect(5, 2, 10, 16)

        val NinePatchCenter = IRect(164, 147, 165, 148)

        val Background = NinePatchResource(Image, NinePatchOvershoot, NinePatchCenter)

        val PressedBackground = NinePatchResource(PressedImage, NinePatchOvershoot, NinePatchCenter)

        val HoveredBackground = Background.copy(paint = HoveredColorFilterPaint)

        val HoveredPressedBackground = PressedBackground.copy(paint = HoveredColorFilterPaint)

        val DefaultHitModel = HitModel.RoundedRect(IRect.Empty, 142)

        val DefaultButtonStyle = Styling<StandardButton, Unit> {
            val scale = guiScale * 0.5f
            background[InputState.Empty] = ResourceLibrary[Background, scale]
            background[InputState.Hovered] = ResourceLibrary[HoveredBackground, scale]
            background[InputState.Pressed] = ResourceLibrary[PressedBackground, scale]
            background[InputState.Pressed, InputState.Hovered] = ResourceLibrary[HoveredPressedBackground, scale]
            hitModel = DefaultHitModel.scale(scale)
        }

        var buttonStyle: Styling<StandardButton, Unit> = DefaultButtonStyle

        val DefaultCaptionStyle = Styling<Node, Unit> {
            horizontalAlign = Align.Center
            verticalAlign = Align.Center
            margin = Margin.all(40.scaled)
            visualTranslation[InputState.Pressed] = vec(1, 1)
        }

        var captionStyle: Styling<Node, Unit> = DefaultCaptionStyle
    }

    object AssetLoader : GuiAssetLoader {
        override suspend fun load(assetLibrary: AssetLibrary) {
            Style.clickSound = assetLibrary.load(Style.ClickSound)
        }
    }

    private val captionNode = Node().apply {
        debugName = """StandardButton.Caption"$caption""""
        inheritsInputState = true
        applyStyle(Style.captionStyle)
    }

    var caption: String = ""
        set(value) {
            field = value
            captionNode.debugName = """StandardButton.Caption"$caption""""
            captionNode.core = if (value.isNotEmpty()) NodeCore.Paragraph(buildParagraph { +value }) else NodeCore.Null
        }

    var actionListener: (() -> Unit)? = null

    init {
        children.add(captionNode)
        primaryPressedListeners += { _, _ ->
            true
        }
        primaryReleasedListeners += { _, hoveredNode, _ ->
            if (isAncestorOf(hoveredNode)) {
                UGameplayStatics.playSound2D(sound = Style.clickSound, isUISound = true)
                actionListener?.invoke()
            }
            true
        }
        applyStyle(Style.buttonStyle)
        debugName = "StandardButton"
        this.caption = caption
        this.horizontalAlign = horizontalAlign
        this.actionListener = actionListener
    }
}

fun Node.standardButton(
    caption: String,
    horizontalAlign: Align = Align.Min,
    actionListener: (() -> Unit)? = null
): StandardButton = StandardButton(caption, horizontalAlign, actionListener).also {
    children.add(it)
}
