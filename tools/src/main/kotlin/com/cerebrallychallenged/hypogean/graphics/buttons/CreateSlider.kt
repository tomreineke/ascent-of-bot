package com.cerebrallychallenged.hypogean.graphics.buttons

import com.cerebrallychallenged.hypogean.graphics.gimp.*
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.vec
import java.nio.file.Files

private val OutputPath = PngOutputPath.resolve("slider")

private fun GimpContext.process(baseImage: GimpImage) {
    val crop = mapOf(
            "left" to Bounds.byMinSize(vec(0, 0), vec(150, 275)),
            "center" to Bounds.byMinSize(vec(150, 0), vec(1, 275)),
            "right" to Bounds.byMinSize(vec(1230, 0), vec(150, 275))
    )
    baseImage.scaleFull(
            crop["right"]!!.min.x + crop["right"]!!.size.x,
            crop["right"]!!.size.y,
            ScaleInterpolation.Cubic
    )
    for ((key, bounds) in crop) {
        val image = baseImage.duplicate()
        image.crop(bounds)
        val name = "slider-track-$key.png"
        image.pngSave(image.layers[0], OutputPath.resolve(name), "?")
    }
}

fun main() {
    Files.createDirectories(OutputPath)
    val script = buildGimp {
        val image = filePngLoad(ExtractedPath.resolve("slider-bar.png"), "")
        process(image)
    }
    println(script)
    if (!script.isEmpty) {
        script.execute()
    }
}