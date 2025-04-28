package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.geo.IPointLayout
import com.cerebrallychallenged.jun.skiatree.geo.decodeFromSegment
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_INT
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class SkiaImage private constructor(
    resource: CloseableResource, val size: Vec2i
) : CloseableResourceBearer(resource), Scalable<SkiaImage> {
    companion object {
        @JvmStatic
        private val imageLoad = function(
            "skiatree_image_load",
            ADDRESS,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val imageDelete = function(
            "skiatree_image_delete",
            VOID,
            ADDRESS
        )

        @JvmStatic
        private val imageResize = function(
            "skiatree_image_resize",
            ADDRESS,
            ADDRESS,
            JAVA_INT,
            JAVA_INT
        )

        fun load(path: Path): SkiaImage {
            val resource: CloseableResource
            val size: Vec2i
            resource = guardedResourceArena(imageDelete) {
                val pointSegment = allocate(IPointLayout)
                val image = imageLoad(path.absolutePathString().toSegment(), pointSegment) as MemorySegment
                size = Vec2i.decodeFromSegment(pointSegment)
                image
            }
            return SkiaImage(resource, size)
        }
    }

    init {
        createWeakReference(resource)
    }

    fun resize(newSize: Vec2i): SkiaImage {
        return SkiaImage(
            guardedResource(imageDelete) {
                imageResize(address, newSize.x, newSize.y) as MemorySegment
            },
            newSize
        )
    }

    override fun scale(factor: Float): SkiaImage = resize((size * factor).ceil())
}
