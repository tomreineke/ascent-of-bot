package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.jun.math.geo.Vec2i

interface MovingEntity : LocatedEntity

/**
 * All MovingEntities occupy a quadratic area. diameter tells the length of its sides.
 * E.g., a diameter of 2 means this actor occupies a 2x2 area.
 */
var MovingEntity.diameter: Int
    get() {
        val size = size
        if (size.x != size.y) {
            modelError("Size of MovingEntity must be equal for x and y")
        }
        return size.x
    }
    set(value) {
        size = Vec2i.ONE * value
    }