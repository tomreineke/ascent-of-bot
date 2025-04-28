package com.cerebrallychallenged.hypogean.graphics

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import java.nio.file.Paths
import kotlin.time.ExperimentalTime

@ExperimentalTime
fun main() {
//    var totalTime = Duration.ZERO
//    repeat(100) {
//
//        val time = measureTime {
//            val image = ImmutableImage.loader().fromPath(Paths.get("gui-graphics/created/sizeFull/gage_head_diff.png"))
//            val scaledImage = image.scaleToWidth(240)
//            scaledImage.output(PngWriter.MaxCompression, Paths.get("gui-graphics/tmp/scaledDiff.png"))
//        }
//        totalTime += time
//    }
//    println(totalTime)

    //actionButton-sizeFull-chassis-selected-active-hover.png

    val image = ImmutableImage.loader().fromPath(Paths.get("gui-graphics/generated/sizeFull/actionButton-sizeFull-chassis-selected-active-hover.png"))
    val scaledImage = image.scaleToWidth(91)
    scaledImage.output(PngWriter.MaxCompression, Paths.get("gui-graphics/tmp/small_action.png"))
}
