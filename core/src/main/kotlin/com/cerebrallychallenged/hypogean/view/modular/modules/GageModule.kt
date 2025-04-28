package com.cerebrallychallenged.hypogean.view.modular.modules

import com.cerebrallychallenged.hypogean.gui.GuiConfig.DefaultTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.guiScale
import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.gui.NinePatchResource
import com.cerebrallychallenged.hypogean.gui.ParagraphNode
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.TextStyleResource
import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.buildParagraph
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.tooltip
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute
import com.cerebrallychallenged.hypogean.view.modular.ModuleContext
import com.cerebrallychallenged.hypogean.view.modular.ViewModule
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.BlendMode
import com.cerebrallychallenged.jun.skiatree.ColorFilter
import com.cerebrallychallenged.jun.skiatree.ColorMatrix
import com.cerebrallychallenged.jun.skiatree.ImageFilter
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.Paint
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.skiatree.text.FontStyle

context(ModuleContext)
class GageModule<T : Entity>(
    val entity: T,
    val attribute: SimpleIntAttribute<T>
) : ViewModule() {
    override val mainNode: Gage = Gage(attribute, attribute.current.get(entity), attribute.max.get(entity))

    private val changeVisitor = object : WorldChange.SimpleVisitor {
        override fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            change.ifOf(attribute.current) { (entity, _, current, _) ->
                if (entity == this@GageModule.entity) {
                    mainNode.current = current
                }
            }
            change.ifOf(attribute.max) { (entity, _, max, _) ->
                if (entity == this@GageModule.entity) {
                    mainNode.max = max
                }
            }
        }
    }

    override fun onChange(change: WorldChange) {
        change.accept(changeVisitor)
    }
}

class Gage(private val attribute: SimpleIntAttribute<*>, current: Int, max: Int) : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        const val ScaleFactor = 0.75f

        val GageHeadOverShoot = IRect(26, 25, 25, 25)
        val GageBarBackground = NinePatchResource(
            ImageResource("Images/gage/gage_bar.png"),
            IRect(GageHeadOverShoot.left, GageHeadOverShoot.top, 18, GageHeadOverShoot.bottom),
            IRect(440, 270, 441, 271)
        )

        const val GageSize = 540

        val DefaultHeight = GageSize - GageHeadOverShoot.top - GageHeadOverShoot.bottom
        val DefaultHeadWidth = GageSize - GageHeadOverShoot.left - GageHeadOverShoot.right
        val IconOffset = vec(17, 17)

        val DefaultBarStyle = Styling<Gage, Unit> {
            val scale = guiScale * ScaleFactor
            background[InputState.Empty] = ResourceLibrary[GageBarBackground, scale]
            val height = (DefaultHeight * scale).ceilToInt()
            minHeight = height
            maxHeight = height
            minWidth = 2000.scaled
            horizontalAlign = Align.Stretch
        }

        var barStyle: Styling<Gage, Unit> = DefaultBarStyle

        val DefaultFillingLeft = 440 - GageBarBackground.overshoot.left
        val DefaultFillingRight = 105 - GageBarBackground.overshoot.right
        val DefaultFillingTop = 188 - GageBarBackground.overshoot.top
        val DefaultFillingBottom = 182 - GageBarBackground.overshoot.bottom

        val DefaultFillingStyle = Styling<Node, SimpleIntAttribute<*>> { attribute ->
            val scale = guiScale * ScaleFactor
            left = (DefaultFillingLeft * scale).ceilToInt()
            top = (DefaultFillingTop * scale).ceilToInt()
            bottom = (DefaultFillingBottom * scale).ceilToInt()
            verticalAlign = Align.Stretch
            minWidth = 200
            maxWidth = 200
            background[InputState.Empty] = Background.Rect(attribute.fillingColor, PaintStyle.Fill)
        }

        var fillingStyle: Styling<Node, SimpleIntAttribute<*>> = DefaultFillingStyle

        val DefaultOverlayTextStyle = DefaultTextStyle.copy(fontSize = 80.0f, fontStyle = FontStyle.Bold)

        var textStyle: TextStyleResource = DefaultOverlayTextStyle

        val DefaultTextNodeStyle = Styling<ParagraphNode, Unit> {
            val scale = guiScale * ScaleFactor
            horizontalAlign = Align.Center
            verticalAlign = Align.Center
            left = (DefaultFillingLeft * scale).ceilToInt()
            top = (DefaultFillingTop * scale).ceilToInt()
            bottom = (DefaultFillingBottom * scale).ceilToInt()
            right = (DefaultFillingRight * scale).ceilToInt()
        }

        var textNodeStyle: Styling<ParagraphNode, Unit> = DefaultTextNodeStyle

        val GageDiffImage = ImageResource("Images/gage/gage_diff.png")

        val GageBlackImage = ImageResource("Images/gage/gage_black.png")

        val DefaultImageStyle = Styling<Node, SimpleIntAttribute<*>> { attribute ->
            val scale = guiScale * ScaleFactor
            val width = (DefaultHeadWidth * scale).ceilToInt()
            val height = (DefaultHeight * scale).ceilToInt()
            minWidth = width
            maxWidth = width
            minHeight = height
            maxHeight = height

            background[InputState.Empty] = Background.Rect(
                Paint().apply {
                    imageFilter = ImageFilter.blend(
                        BlendMode.SrcOver,
                        ImageFilter.blend(
                            BlendMode.Plus,
                            ImageFilter.image(ResourceLibrary[GageDiffImage, scale])
                                .colorFilter(ColorFilter.matrix(ColorMatrix.color(attribute.headColor))),
                            ImageFilter.image(ResourceLibrary[GageBlackImage, scale])
                        ),
                        ImageFilter.image(ResourceLibrary[attribute.icon, scale]).offset(IconOffset * scale)
                    ).offset(vec(-GageHeadOverShoot.left * scale, -GageHeadOverShoot.top * scale))
                    style = PaintStyle.Fill
                },
                overshoot = GageHeadOverShoot
            )
        }

        var imageStyle: Styling<Node, SimpleIntAttribute<*>> = DefaultImageStyle
    }

    val filling = Node().apply {
        this@Gage.children.add(this)
        applyStyle(Style.fillingStyle, attribute)
    }

    val text = ParagraphNode().apply {
        this@Gage.children.add(this)
        applyStyle(Style.textNodeStyle)
    }

    val image = Node().apply {
        this@Gage.children.add(this)
        applyStyle(Style.imageStyle, attribute)
    }

    var max: Int = max
        set(value) {
            field = value
            update()
        }

    var current: Int = current
        set(value) {
            field = value
            update()
        }

    private fun update() {
        val scale = guiScale * Style.ScaleFactor
        val availableWidth = size.x - (Style.DefaultFillingLeft * scale + Style.DefaultFillingRight * scale).ceilToInt()
        val width = ((availableWidth * current).toFloat() / max).ceilToInt()
        filling.minWidth = width
        filling.maxWidth = width
        text.paragraph = buildParagraph(textStyle = Style.textStyle) {
            +"$current / $max"
        }
    }

    init {
        applyStyle(Style.barStyle)
        resizeListeners += {
            update()
        }
        update()
        tooltip = Tooltip {
            +attribute.name
        }
    }
}
