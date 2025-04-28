package com.cerebrallychallenged.hypogean.graphics.gimp

open class GimpDrawable(gimp: Gimp) : GimpValue(gimp)

fun GimpDrawable.editClear() {
    append("pdb.gimp_drawable_edit_clear($this)")
}

fun GimpDrawable.brightnessContrast(deltaBrightness: GimpFloat, deltaContrast: GimpFloat) {
    append("pdb.gimp_drawable_brightness_contrast($this, $deltaBrightness, $deltaContrast)")
}

fun GimpDrawable.brightnessContrast(deltaBrightness: Double, deltaContrast: Double) {
    brightnessContrast(deltaBrightness.g, deltaContrast.g)
}
