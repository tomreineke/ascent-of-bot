package com.cerebrallychallenged.hypogean.graphics.slicing

import java.nio.file.Paths

fun main() {
    slice(
        Paths.get("gui-graphics/extracted/view_frame.png"),
        Paths.get("core/src/jvmMain/resources/Images/generated/view_frame"),
        "view_frame",
        SliceData(0, 10, 688, 1178, 1183),
        SliceData(0, 5, 454, 949, 957)
    )
}