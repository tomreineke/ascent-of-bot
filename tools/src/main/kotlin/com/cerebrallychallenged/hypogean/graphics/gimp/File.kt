package com.cerebrallychallenged.hypogean.graphics.gimp

import java.nio.file.Path

enum class GimpSvgPaths(val code: String) {
    None("0"),
    Individual("1"),
    Merge("2");

    override fun toString(): String = code
}

fun GimpContext.filePngLoad(path: GimpString, name: GimpString): GimpImage = GimpImage(gimp).also {
    append("$it = pdb.file_png_load($path, $name)")
}

fun GimpContext.filePngLoad(path: Path, name: String): GimpImage = filePngLoad(path.g, name.g)

fun GimpContext.fileSvgLoad(
    path: GimpString,
    name: GimpString,
    resolution: GimpFloat = 90.0.g,
    width: GimpInt,
    height: GimpInt,
    paths: GimpSvgPaths
): GimpImage = GimpImage(gimp).also {
    append("$it = pdb.file_svg_load($path, $name, $resolution, $width, $height, ${paths.code})")
}

fun GimpContext.fileSvgLoad(
    path: Path,
    name: String,
    resolution: Double = 90.0,
    width: Int,
    height: Int,
    paths: GimpSvgPaths
): GimpImage = fileSvgLoad(path.g, name.g, resolution.g, width.g, height.g, paths)

fun GimpContext.fileLoad(path: GimpString, name: GimpString): GimpImage =
    GimpImage(gimp).also { append("$it = pdb.gimp_file_load($path, $name)") }

fun GimpContext.fileLoad(path: Path, name: String): GimpImage = fileLoad(path.g, name.g)

fun GimpImage.pngSave(
    layer: GimpLayer,
    path: GimpString,
    name: GimpString,
    interlace: GimpBoolean,
    compression: GimpInt,
    bkgd: GimpBoolean,
    gama: GimpBoolean,
    offs: GimpBoolean,
    phys: GimpBoolean,
    time: GimpBoolean
) {
    append("pdb.file_png_save($this, $layer, $path, $name, $interlace, $compression, $bkgd, $gama, $offs, $phys, $time)")
}

fun GimpImage.pngSave(
    layer: GimpLayer,
    path: Path,
    name: String,
    interlace: Boolean = false,
    compression: Int = 9,
    bkgd: Boolean = false,
    gama: Boolean = false,
    offs: Boolean = false,
    phys: Boolean = false,
    time: Boolean = false
) {
    pngSave(layer, path.g, name.g, interlace.g, compression.g, bkgd.g, gama.g, offs.g, phys.g, time.g)
}