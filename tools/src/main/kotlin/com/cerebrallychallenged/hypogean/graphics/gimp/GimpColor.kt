package com.cerebrallychallenged.hypogean.graphics.gimp

class GimpColor(gimp: Gimp, constantString: String? = null) : GimpValue(gimp, constantString) {
    companion object
}

fun GimpContext.rgb(red: GimpInt, green: GimpInt, blue: GimpInt): GimpColor =
    GimpColor(gimp).also { append("$it = gimpcolor.RGB($red, $green, $blue)") }

fun GimpContext.rgb(red: Int, green: Int, blue: Int): GimpColor = rgb(red.g, green.g, blue.g)