package com.cerebrallychallenged.jun

import com.cerebrallychallenged.jun.math.geo.Vec2i

interface JunManagerExtension {
    fun WrapperIncubator.declareWrappers()

    fun isPixelOccluded(position: Vec2i): Boolean = false

    fun onEndPlay() {}
}