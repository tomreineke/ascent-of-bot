package com.cerebrallychallenged.hypogean.graphics.gimp

import com.cerebrallychallenged.jun.math.geo.Vec2i

class GimpLayer(gimp: Gimp) : GimpDrawable(gimp) {
    fun translate(x: GimpInt, y: GimpInt) {
        append("$this.translate($x, $y)")
    }

    fun translate(x: Int, y: Int) {
        translate(x.g, y.g)
    }

    fun translate(v: Vec2i) {
        translate(v.x, v.y)
    }
}

fun GimpLayer.newFromDrawable(destinationImage: GimpImage): GimpLayer =
    GimpLayer(gimp).also { append("$it = pdb.gimp_layer_new_from_drawable($this, $destinationImage)") }

