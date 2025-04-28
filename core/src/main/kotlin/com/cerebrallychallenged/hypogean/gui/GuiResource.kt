package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.Paint
import com.cerebrallychallenged.jun.skiatree.Scalable
import com.cerebrallychallenged.jun.skiatree.SkiaImage
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.text.FontStyle
import com.cerebrallychallenged.jun.skiatree.text.MutableStringList
import com.cerebrallychallenged.jun.skiatree.text.TextStyle
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import java.io.DataInput
import java.io.DataOutput

interface GuiResource<T: Scalable<T>> {
    fun load(): T
}

data class ImageResource(val resourcePath: String): GuiResource<SkiaImage> {
    override fun load(): SkiaImage = SkiaImage.loadResource(resourcePath)
}

data class BackgroundImageResource(
    val image: ImageResource,
    val overshoot: IRect,
    val paint: Paint? = null
): GuiResource<Background.Image> {
    override fun load(): Background.Image = Background.Image(ResourceLibrary[image], overshoot, paint)
}

data class NinePatchResource(
    val image: ImageResource,
    val overshoot: IRect,
    val center: IRect,
    val paint: Paint? = null
): GuiResource<Background.NinePatch> {
    override fun load(): Background.NinePatch = Background.NinePatch(ResourceLibrary[image], overshoot, center, paint)

    val effectiveCenter: IRect
        get() = IRect(
            center.left - overshoot.left,
            center.top - overshoot.top,
            center.right - overshoot.left,
            center.bottom - overshoot.bottom
        )
}

data class TextStyleResource(
    val fontFamilies: List<FontFamilyResource>,
    val fontSize: Float,
    val fontStyle: FontStyle = FontStyle.Normal,
    val color: FLinearColor = FLinearColor.White,
    val outlineShadows: List<Pair<FLinearColor, Float>> = listOf()
): GuiResource<TextStyle> {
    override fun load(): TextStyle = TextStyle().also {
        it.fontFamilies = MutableStringList().apply {
            for (family in fontFamilies) {
                add(ResourceLibrary[family].name)
            }
        }
        it.fontSize = fontSize
        it.fontStyle = fontStyle
        it.color = color
        for ((color, amount) in outlineShadows) {
            it.addOutlineShadows(color, amount)
        }
    }
}

fun DataOutput.writeImageResource(imageResource: ImageResource) {
    writeUTF(imageResource.resourcePath)
}

fun DataInput.readImageResource(): ImageResource = ImageResource(readUTF())
