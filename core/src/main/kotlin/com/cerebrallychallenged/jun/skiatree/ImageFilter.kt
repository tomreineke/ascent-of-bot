package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.nullableAddress
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT

class ImageFilter private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<ImageFilter>(::ImageFilter, "skiatree_image_filter_delete") {
        @JvmStatic
        private val imageFilterNewBlend = function(
            "skiatree_image_filter_new_blend",
            ADDRESS,
            JAVA_INT,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val imageFilterNewColorFilter = function(
            "skiatree_image_filter_new_color_filter",
            ADDRESS,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val imageFilterNewCompose = function(
            "skiatree_image_filter_new_compose",
            ADDRESS,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val imageFilterNewImage = function(
            "skiatree_image_filter_new_image",
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val imageFilterNewOffset = function(
            "skiatree_image_filter_new_offset",
            ADDRESS,
            ADDRESS,
            JAVA_FLOAT,
            JAVA_FLOAT
        )

        fun blend(blendMode: BlendMode, background: ImageFilter?, foreground: ImageFilter): ImageFilter = ImageFilter {
            imageFilterNewBlend(blendMode.ordinal, background.nullableAddress, foreground.address) as MemorySegment
        }

        fun image(image: SkiaImage): ImageFilter = ImageFilter {
            imageFilterNewImage(image.address) as MemorySegment
        }
    }

    fun colorFilter(colorFilter: ColorFilter): ImageFilter = ImageFilter {
        imageFilterNewColorFilter(address, colorFilter.address) as MemorySegment
    }

    fun compose(inner: ImageFilter): ImageFilter = ImageFilter {
        imageFilterNewCompose(address, inner.address) as MemorySegment
    }

    fun offset(offset: Vec2f): ImageFilter = ImageFilter {
        imageFilterNewOffset(address, offset.x, offset.y) as MemorySegment
    }
}
