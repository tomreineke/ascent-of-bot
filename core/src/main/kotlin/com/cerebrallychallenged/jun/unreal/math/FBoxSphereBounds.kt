package com.cerebrallychallenged.jun.unreal.math

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.times

data class FBoxSphereBounds(val origin: Vec3f, val boxExtent: Vec3f, val sphereRadius: Float) {
    @Convenience
    fun toBounds(): Bounds<Vec3f> = Bounds.centered(origin, 0.5f * boxExtent)
}