package com.cerebrallychallenged.hypogean.graphics.slicing

import com.cerebrallychallenged.jun.util.crossJoin
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import java.nio.file.Files
import java.nio.file.Path

//        Lo       Inner       Hi
//  loOuter loInner      hiInner hiOuter
//     |       |            |       |
//     |       |            |       |
// ----+-------+------------+-------+---- loOuter
//     |.......|............|.......|             Lo
// ----+-------+------------+-------+---- loInner
//     |.......|............|.......|
//     |.......|............|.......|             Inner
//     |.......|............|.......|
// ----+-------+------------+-------+---- hiInner
//     |.......|............|.......|             Hi
// ----+-------+------------+-------+---- hiOuter
//     |       |            |       |
//     |       |            |       |

enum class SliceLoc(
    val startOf: (SliceData) -> Int,
    val sizeOf: (SliceData) -> Int,
) {
    Lo(
        SliceData::loExterior,
        SliceData::loWidth,
    ),
    Inner(
        SliceData::inner,
        { 1 },
    ),
    Hi(
        SliceData::inner,
        SliceData::hiWidth,
    )
}

data class SliceData(
    val loExterior: Int,
    val loOuter: Int,
    val inner: Int,
    val hiOuter: Int,
    val hiExterior: Int
) {
    val loWidth: Int
        get() = inner - loExterior

    val hiWidth: Int
        get() = hiExterior - inner

}

private fun name(horizontal: SliceLoc, vertical: SliceLoc): String = when (horizontal) {
    SliceLoc.Lo -> when (vertical) {
        SliceLoc.Lo -> "left_top"
        SliceLoc.Inner -> "left"
        SliceLoc.Hi -> "left_bottom"
    }
    SliceLoc.Inner -> when (vertical) {
        SliceLoc.Lo -> "top"
        SliceLoc.Inner -> "center"
        SliceLoc.Hi -> "bottom"
    }
    SliceLoc.Hi -> when (vertical) {
        SliceLoc.Lo -> "right_top"
        SliceLoc.Inner -> "right"
        SliceLoc.Hi -> "right_bottom"
    }
}

fun ImmutableImage.subimage(
    horizontalData: SliceData,
    horizontalLoc: SliceLoc,
    verticalData: SliceData,
    verticalLoc: SliceLoc
): ImmutableImage = subimage(
    horizontalLoc.startOf(horizontalData),
    verticalLoc.startOf(verticalData),
    horizontalLoc.sizeOf(horizontalData),
    verticalLoc.sizeOf(verticalData)
)

val SliceLocations = SliceLoc.values().asSequence().crossJoin(SliceLoc.values().asSequence()).toList()

fun slice(
    sourcePath: Path,
    targetPath: Path,
    name: String,
    horizontal: SliceData,
    vertical: SliceData
) {
    val base = ImmutableImage.loader().fromPath(sourcePath)
    Files.createDirectories(targetPath)
    fun targetName(horizontalLocation: SliceLoc, verticalLocation: SliceLoc): String =
        "${name}_${name(horizontalLocation, verticalLocation)}.png"
    for ((horizontalLocation, verticalLocation) in SliceLocations) {
        val slicedImage = base.subimage(horizontal, horizontalLocation, vertical, verticalLocation)
        val targetName = targetName(horizontalLocation, verticalLocation)
        slicedImage.output(PngWriter.MaxCompression, targetPath.resolve(targetName))
    }
}
