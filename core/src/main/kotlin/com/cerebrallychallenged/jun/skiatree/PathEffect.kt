package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.apibuilder.toSegment
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT

class PathEffect private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<PathEffect>(::PathEffect, "skiatree_path_effect_delete") {
        @JvmStatic
        private val pathEffectNewDashPattern = function(
            "skiatree_path_effect_new_dash_pattern",
            ADDRESS,
            ADDRESS,
            JAVA_INT,
            JAVA_FLOAT
        )

        fun dashPattern(dashes: FloatArray, phase: Float): PathEffect = PathEffect {
            confinedArena {
                pathEffectNewDashPattern(dashes.toSegment(), dashes.size, phase) as MemorySegment
            }
        }
    }
}
