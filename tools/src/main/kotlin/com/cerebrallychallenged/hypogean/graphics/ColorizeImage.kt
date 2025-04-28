package com.cerebrallychallenged.hypogean.graphics

import com.cerebrallychallenged.jun.util.writeImage
import javafx.application.Platform
import javafx.scene.image.Image
import javafx.scene.paint.Color
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Platform.startup {
        val black = Image(args[0])
        val diff = Image(args[1])
        val outputPath = args[2]
        val components = args.toList().subList(3, 6).map { it.toInt() / 255.0 }.toDoubleArray()
        val color = Color(components[0], components[1], components[2], 1.0)
        val result = black.pixelWise(diff) { blackPixel, diffPixel ->
            blackPixel + diffPixel * color
        }
        File(outputPath).writeImage(result)
        exitProcess(0)
    }
}
