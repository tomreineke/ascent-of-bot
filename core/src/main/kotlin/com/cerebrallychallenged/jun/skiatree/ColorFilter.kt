package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS

class ColorFilter private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<ColorFilter>(::ColorFilter, "skiatree_color_filter_delete") {
        @JvmStatic
        private val colorFilterNewLighting = function(
            "skiatree_color_filter_new_lighting",
            ADDRESS,
            Color4fLayout,
            Color4fLayout
        )

        @JvmStatic
        private val colorFilterNewCompose = function(
            "skiatree_color_filter_new_compose",
            ADDRESS,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val colorFilterNewMatrix = function(
            "skiatree_color_filter_new_matrix",
            ADDRESS,
            ADDRESS
        )

        fun lighting(mul: FLinearColor, add: FLinearColor): ColorFilter = ColorFilter {
            confinedArena {
                colorFilterNewLighting(mul.toSegment(), add.toSegment()) as MemorySegment
            }
        }

        fun matrix(matrix: ColorMatrix): ColorFilter = ColorFilter{
            colorFilterNewMatrix(matrix.segment) as MemorySegment
        }
    }

    fun compose(inner: ColorFilter): ColorFilter = ColorFilter {
        colorFilterNewCompose(address, inner.address) as MemorySegment
    }
}
