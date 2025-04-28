package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.skiatree.ColorFilter
import com.cerebrallychallenged.jun.skiatree.ColorMatrix
import com.cerebrallychallenged.jun.skiatree.Layers
import com.cerebrallychallenged.jun.skiatree.Paint
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.skiatree.text.FontStyle
import com.cerebrallychallenged.jun.skiatree.text.TextStyle
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

open class GuiScaleContext(var guiScale: Float) {
    val Number.scaled: Int
        get() = (this.toFloat() * guiScale).ceilToInt()

    inline fun <R> scaling(factor: Float, f: GuiScaleContext.() -> R): R =
        GuiScaleContext(guiScale * factor).f()
}

val Number.scaled: Int
    get() = (this.toFloat() * GuiConfig.guiScale).ceilToInt()

inline fun <R> scaling(factor: Float, f: GuiScaleContext.() -> R): R =
    GuiScaleContext(GuiConfig.guiScale * factor).f()

@Suppress("MemberVisibilityCanBePrivate") // Public members shall be configurable by mods.
object GuiConfig : GuiScaleContext(0.25f) {
    val DefaultTextStyle = TextStyleResource(
        listOf(CommonFontFamilies.NotoSans),
        64.0f
    )

    val MainTitleTextStyle = DefaultTextStyle.copy(fontSize = 160.0f, fontStyle = FontStyle.Bold)

    val MediumTitleTextStyle = DefaultTextStyle.copy(fontSize = 120.0f, fontStyle = FontStyle.Bold)

    val SmallTitleTextStyle = DefaultTextStyle.copy(fontSize = 80.0f, fontStyle = FontStyle.Bold)

    val TinyTitleTextStyle = DefaultTextStyle.copy(fontStyle = FontStyle.Bold)

    val BigOverlayTextStyle = DefaultTextStyle.copy(fontSize = 160.0f, fontStyle = FontStyle.Bold)

    val DefaultItalicStyle = DefaultTextStyle.copy(fontStyle = FontStyle.Italic)

    val DefaultMonospaceStyle = TextStyleResource(
        listOf(CommonFontFamilies.NotoSansMono),
        64.0f
    )

    val HoveredColorFilterPaint = Paint().apply { colorFilter = ColorFilter.matrix(ColorMatrix.brightness(1.2f)) }

    val DisabledColorFilterPaint = Paint().apply { colorFilter = ColorFilter.matrix(ColorMatrix.grayscale(0.5f)) }

    val ColorByFactionRelation: MutableMap<Faction.Relation?, FLinearColor> = mutableMapOf(
        null to FLinearColor.rgb(1.0f, 1.0f, 1.0f),
        Faction.Relation.SAME to FLinearColor.rgb(0.0f, 0.5f, 1.0f),
        Faction.Relation.ALLIED to FLinearColor.rgb(0.0f, 0.5f, 1.0f),
        Faction.Relation.NEUTRAL to FLinearColor.rgb(0.565f, 0.565f, 0.0f),
        Faction.Relation.HOSTILE to FLinearColor.rgb(1.0f, 0.0f, 0.0f),
    )

    val HintColor: FLinearColor = FLinearColor.Green

    val TextStyleByFactionRelation: Map<Faction.Relation?, TextStyleResource> by lazy {
        ColorByFactionRelation.mapValues { (_, color) ->
            DefaultTextStyle.copy(color = color)
        }
    }
}

enum class GuiLayer(val layerIndex: Int) {
    Base(0),
    Window(1),
    Overlay(5),
    Tooltip(7),
    LoadingView(100)
}

operator fun Layers.get(layer: GuiLayer): Node = get(layer.layerIndex)

fun interface Styling<in T, P> {
    fun T.applyStyle(parameter: P)
}

fun <T, P> T.applyStyle(style: Styling<T, P>, parameter: P) {
    with(style) {
        applyStyle(parameter)
    }
}

fun <T> T.applyStyle(style: Styling<T, Unit>) {
    with(style) {
        applyStyle(Unit)
    }
}

fun <T, P> Styling<T, P>.derive(
    refining: Styling<T, P>
): Styling<T, P> = Styling { parameter ->
    applyStyle(this@derive, parameter)
    applyStyle(refining, parameter)
}

fun TextStyle.derive(refining: TextStyle.() -> Unit): TextStyle = clone().apply(refining)
