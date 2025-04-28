package com.cerebrallychallenged.hypogean.graphics.buttons

import com.cerebrallychallenged.hypogean.graphics.gimp.*
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.vec
import java.nio.file.Files

private val OutputPath = PngOutputPath.resolve("standard_button")

private val Crop = mapOf(
    "left" to Bounds.byMinSize(vec(0, 0), vec(150, 303)),
    "center" to Bounds.byMinSize(vec(150, 0), vec(1, 303)),
    "right" to Bounds.byMinSize(vec(500, 0), vec(183, 303))
)

private fun GimpContext.process(baseImage: GimpImage, isActive: Boolean) {
    if (isActive) {
        baseImage.selectEllipse(GimpChannelOp.Replace, 98.0, 89.0, 400.0, 400.0)
        baseImage.selectEllipse(GimpChannelOp.Add, 858.0, 89.0, 400.0, 400.0)
        baseImage.selectRectangle(GimpChannelOp.Add, 298.0, 89.0, 760.0, 400.0)
        baseImage.layers[0].brightnessContrast(-0.5, 0.0)
    }
    baseImage.scaleFull(683, 303, ScaleInterpolation.Cubic)
    for ((key, bounds) in Crop) {
        val image = baseImage.duplicate()
        image.crop(bounds)
        val name = "standard-button${if (isActive) "-active" else ""}-$key.png"
        image.pngSave(image.layers[0], OutputPath.resolve(name), "?")
    }
}

fun main() {
    Files.createDirectories(OutputPath)
    val script = buildGimp {
        val image = filePngLoad(ExtractedPath.resolve("standardButton.png"), "")
        process(image.duplicate(), false)
        process(image, true)
    }
    println(script)
    if (!script.isEmpty) {
        script.execute()
    }
}