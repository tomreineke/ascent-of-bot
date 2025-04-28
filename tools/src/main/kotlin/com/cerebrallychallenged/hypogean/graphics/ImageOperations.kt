package com.cerebrallychallenged.hypogean.graphics

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.pixels.Pixel
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.nio.file.Path

fun Color.elementWise(rhs: Color, f: (Double, Double) -> Double): Color = Color(
        f(red, rhs.red).coerceIn(0.0, 1.0),
        f(green, rhs.green).coerceIn(0.0, 1.0),
        f(blue, rhs.blue).coerceIn(0.0, 1.0),
        opacity
)

fun Color.elementWise(f: (Double) -> Double): Color = elementWise(Color.TRANSPARENT) { a, _ -> f(a) }

operator fun Color.plus(rhs: Color): Color = elementWise(rhs) { a, b -> a + b }

operator fun Color.minus(rhs: Color): Color = elementWise(rhs) { a, b -> a - b }

operator fun Color.times(rhs: Color): Color = elementWise(rhs) { a, b -> a * b }

fun Color.maximizeBrightness(): Color {
    val maxComponent = maxOf(red, green, blue)
    return if (maxComponent > 0) {
        val factor = 1.0 / maxComponent
        Color(red * factor, green * factor, blue * factor, 1.0)
    } else {
        this
    }
}

fun loadImage(path: Path): Image = Image(path.toUri().toURL().toString())

fun Image.pixelWise(rhs: Image, f: (Color, Color) -> Color): Image {
    val width = width.toInt()
    val height = height.toInt()
    require(width == rhs.width.toInt()) { "Image widths $width and ${rhs.width} do not match" }
    require(height == rhs.height.toInt()) { "Image heights $height and ${rhs.height} do not match" }
    val result = WritableImage(width, height)
    val firstReader = pixelReader
    val secondReader = rhs.pixelReader
    val writer = result.pixelWriter
    for (y in 0 until height) {
        for (x in 0 until  width) {
            val firstColor = firstReader.getColor(x, y)
            val secondColor = secondReader.getColor(x, y)
            writer.setColor(x, y, f(firstColor, secondColor))
        }
    }
    return result
}


operator fun Pixel.component1(): Int = x

operator fun Pixel.component2(): Int = y

fun ImmutableImage.concatVertically(other: ImmutableImage): ImmutableImage {
    require(width == other.width)
    val height = height
    val otherHeight = other.height
    val newImage = ImmutableImage.create(width, height + otherHeight)
    newImage.mapInPlace { (x, y) ->
        if (y < height) {
            pixel(x, y).toColor().toAWT()
        } else {
            other.pixel(x, y - height).toColor().toAWT()
        }
    }
    return newImage
}

fun ImmutableImage.concatHorizontally(other: ImmutableImage): ImmutableImage {
    require(height == other.height)
    val width = width
    val otherWidth = other.width
    val newImage = ImmutableImage.create(width + otherWidth, height)
    newImage.mapInPlace { (x, y) ->
        if (x < width) {
            pixel(x, y).toColor().toAWT()
        } else {
            other.pixel(x - width, y).toColor().toAWT()
        }
    }
    return newImage
}

fun ImmutableImage.safeScaleTo(targetWidth: Int, targetHeight: Int): ImmutableImage {
    return if (width < 3 || height < 3 || targetWidth < 3 || targetHeight < 3) {
        scaleTo(targetWidth, targetHeight, ScaleMethod.FastScale)
    } else {
        scaleTo(targetWidth, targetHeight, ScaleMethod.Bicubic)
    }
}

fun ImmutableImage.safeScaleToWidth(targetWidth: Int): ImmutableImage {
    return if (width < 3 || height < 3 || targetWidth < 3) {
        scaleToWidth(targetWidth, ScaleMethod.FastScale)
    } else {
        scaleToWidth(targetWidth, ScaleMethod.Bicubic)
    }
}