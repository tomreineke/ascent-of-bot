@file:Suppress("BlockingMethodInNonBlockingContext")

package com.cerebrallychallenged.hypogean.graphics

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.util.buildImage
import com.cerebrallychallenged.jun.util.drawImage
import com.cerebrallychallenged.jun.util.size
import com.cerebrallychallenged.jun.util.writeImage
import javafx.application.Platform
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.Color.rgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

data class Input(
        val name: String,
        val iconPath: String = "$name.png",
        val color: Color
)

private val Inputs = listOf(
        Input("energy", color = rgb(11, 127, 125))
)

private val OutputDirectory = Paths.get("gui-graphics/tmp")

private val GuiScales = mapOf(
        "Full" to 1.0,
        "1" to 0.168,
        "2" to 0.237,
        "3" to 0.474
)

private suspend fun process(blackImage: Image, diffImage: Image, inputConfig: Input) {
    val iconImage = loadImage(Paths.get("gui-graphics", "extracted", "icons", inputConfig.iconPath))
//    val scaledImage = iconImage.scaled(1.0 / iconImage.width)
//    println(scaledImage.width)
//    val colorFactor = scaledImage.pixelReader.getColor(0, 0)
    val colorFactor = inputConfig.color.maximizeBrightness()
//    colorFactor.brightness = 1.0
    val colorizedImage = blackImage.pixelWise(diffImage) { blackColor, diffColor ->
        blackColor + diffColor * colorFactor
    }
    val size = colorizedImage.size
    val resultImage = buildImage(size) {
        drawImage(colorizedImage, Vec2f.ZERO)
        drawImage(iconImage, Vec2f.ZERO)
    }
    val outputPath = OutputDirectory.resolve("gage-${inputConfig.name}.png")
    Files.createDirectories(outputPath.parent)
    outputPath.toFile().writeImage(resultImage)
//    for ((i, scale) in GuiScales) {
//        val outputPath = OutputDirectory.resolve("size$i/gage-${inputConfig.name}.png")
//        Files.createDirectories(outputPath.parent)
//        outputPath.toFile().writeImage(resultImage, scale = scale)
//    }
    println(colorFactor)
}

fun main() {
    Platform.startup {
        GlobalScope.launch(Dispatchers.JavaFx) {
            val whiteImage = loadImage(Paths.get("gui-graphics", "extracted", "gage_white.png"))
            val blackImage = loadImage(Paths.get("gui-graphics", "extracted", "gage_black.png"))
            val diffImage = whiteImage.pixelWise(blackImage) { firstColor, secondColor -> firstColor - secondColor }
            for (input in Inputs) {
                process(blackImage, diffImage, input)
            }
            exitProcess(0)
        }
    }
}