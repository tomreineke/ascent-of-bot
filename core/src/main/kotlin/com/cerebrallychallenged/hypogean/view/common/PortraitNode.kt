package com.cerebrallychallenged.hypogean.view.common

import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiConfig.guiScale
import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.gui.NinePatchResource
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.node.ImageNode
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

/**
 * frameSize > borderSize is required.
 * @param frameSize unscaled frame size
 * @param borderSize unscaled border size
 */
class PortraitNode(frameSize: Int, borderSize: Int) : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val MissingPortrait = ImageResource("Images/portrait-missing.png")

        var backgroundStyle = Styling<Node, FLinearColor> { color ->
            background[InputState.Empty] = Background.Rect(color, PaintStyle.Fill)
            background[InputState.Selected] = Background.Rect(color * 1.2f, PaintStyle.Fill)
        }

        val FrameImage = NinePatchResource(
            ImageResource("Images/gui/ini_frame2.png"),
            IRect.Empty,
            IRect(168, 229, 169, 230)
        )

        var frameStyle: Styling<Node, Unit> = Styling {
            horizontalAlign = Align.Stretch
            verticalAlign = Align.Stretch
            val image = ResourceLibrary[FrameImage, guiScale * 0.66f]
            background[InputState.Empty] = image
            background[InputState.Selected] = image.copy(paint = GuiConfig.HoveredColorFilterPaint)
            inheritsInputState = true
        }
    }

    var backgroundColor: FLinearColor = FLinearColor.rgb(0.112f, 0.086f, 0.060f)
        set(value) {
            field = value
            applyStyle(Style.backgroundStyle, value)
        }

    private val imageNode = ImageNode().apply {
        horizontalAlign = Align.Center
        verticalAlign = Align.Center
        inheritsInputState = true
    }

    private val portraitSize = frameSize - 2 * borderSize

    var image: ImageResource? = null
        set(value) {
            field = value
            val image = ResourceLibrary.imageWithLongerSize(
                value ?: Style.MissingPortrait,
                portraitSize.scaled
            )
            imageNode.image = image
            imageNode.background[InputState.Selected] = Background.Image(image, IRect.Empty, GuiConfig.HoveredColorFilterPaint)
        }

    init {
        applyStyle(Style.backgroundStyle, backgroundColor)
        children.add(imageNode)
        children.add(Node().apply {
            applyStyle(Style.frameStyle)
        })
        val size = frameSize.scaled
        minWidth = size
        maxWidth = size
        minHeight = size
        maxHeight = size
    }
}
