package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.CloseableResourceFactory
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT

class PlaceholderStyle private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<PlaceholderStyle>(::PlaceholderStyle, "skiatree_placeholder_style_delete") {
        @JvmStatic
        private val placeholderStyleNew = function(
            "skiatree_placeholder_style_new",
            ADDRESS,
            JAVA_FLOAT,
            JAVA_FLOAT,
            JAVA_INT,
            JAVA_INT,
            JAVA_FLOAT
        )

        operator fun invoke(
            width: Float,
            height: Float,
            placeholderAlignment: PlaceholderAlignment = PlaceholderAlignment.Baseline,
            baseline: TextBaseline = TextBaseline.Alphabetic,
            offset: Float = 0.0f
        ): PlaceholderStyle = PlaceholderStyle {
            placeholderStyleNew(width, height, placeholderAlignment.ordinal, baseline.ordinal, offset) as MemorySegment
        }
    }
}
