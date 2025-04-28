package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_FLOAT

class SkiaPath private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<SkiaPath>(::SkiaPath, "skiatree_path_delete") {
        @JvmStatic
        private val pathNew = function("skiatree_path_new", ADDRESS)

        @JvmStatic
        private val pathLineTo = function(
            "skiatree_path_line_to",
            VOID,
            ADDRESS,
            JAVA_FLOAT,
            JAVA_FLOAT
        )

        @JvmStatic
        private val pathMoveTo = function(
            "skiatree_path_move_to",
            VOID,
            ADDRESS,
            JAVA_FLOAT,
            JAVA_FLOAT
        )

        operator fun invoke(): SkiaPath = SkiaPath {
            pathNew() as MemorySegment
        }
    }

    fun lineTo(point: Vec2f) {
        pathLineTo(address, point.x, point.y)
    }

    fun moveTo(point: Vec2f) {
        pathMoveTo(address, point.x, point.y)
    }
}
