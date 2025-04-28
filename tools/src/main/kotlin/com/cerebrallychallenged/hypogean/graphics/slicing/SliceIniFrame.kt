package com.cerebrallychallenged.hypogean.graphics.slicing

import java.nio.file.Paths

fun main() {
    slice(
        Paths.get("gui-graphics/extracted/ini_frame.png"),
        Paths.get("core/src/jvmMain/resources/Images/generated/ini_frame"),
        "ini_frame",
        SliceData(8, 8, 154, 292, 292),
        SliceData(5, 5, 124, 349, 349),
    )
}
