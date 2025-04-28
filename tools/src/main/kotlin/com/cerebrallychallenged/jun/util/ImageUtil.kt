package com.cerebrallychallenged.jun.util

import com.cerebrallychallenged.jun.math.geo.Vec2d
import com.cerebrallychallenged.jun.math.geo.vec
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun Image.scaled(factor: Double): Image {
    val imageView = ImageView(this)
    imageView.isPreserveRatio = true
    imageView.isSmooth = true
    imageView.fitHeight = this.height * factor
    return imageView.snapshot(null, null)
}

suspend fun buildImage(size: Vec2d, builderAction: GraphicsContext.() -> Unit): Image = Canvas(size.x, size.y).run {
    graphicsContext2D.builderAction()
    withContext(Dispatchers.JavaFx) {
        snapshot(null, null)
    }
}

fun Image.toBufferedImage(): BufferedImage = SwingFXUtils.fromFXImage(this, null)

fun File.writeImage(bufferedImage: BufferedImage, format: String = "png") {
    ImageIO.write(bufferedImage, format, this)
}

fun File.writeImage(image: Image, format: String = "png") {
    writeImage(image.toBufferedImage(), format)
}

val Image.size: Vec2d
    get() = vec(width, height)
