package com.cerebrallychallenged.hypogean.graphics

import com.cerebrallychallenged.jun.util.writeImage
import javafx.application.Platform
import javafx.scene.image.Image
import java.io.File

fun main(args: Array<String>) {
    Platform.startup {
        val first = Image(args[0])
        val second = Image(args[1])
        val result = first.pixelWise(second) { firstColor, secondColor -> firstColor - secondColor }
        File("gui-graphics/tmp/diff.png").writeImage(result)
    }
}