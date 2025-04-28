package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.CloseableResourceFactory
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.VOID
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_LONG

class ParagraphStyle private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<ParagraphStyle>(::ParagraphStyle, "skiatree_paragraph_style_delete") {
        @JvmStatic
        private val paragraphStyleNew = function("skiatree_paragraph_style_new", ADDRESS)

        @JvmStatic
        private val paragraphStyleSetTextAlign = function(
            "skiatree_paragraph_style_set_text_align",
            VOID,
            ADDRESS,
            JAVA_LONG
        )

        operator fun invoke(): ParagraphStyle = ParagraphStyle { paragraphStyleNew() as MemorySegment }
    }

    var textAlign: TextAlign = TextAlign.Left
        set(value) {
            field = value
            paragraphStyleSetTextAlign(address, value.ordinal)
        }
}
