package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.nullableAddress
import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.CloseableResourceFactory
import com.cerebrallychallenged.jun.skiatree.Color4fLayout
import com.cerebrallychallenged.jun.skiatree.Paint
import com.cerebrallychallenged.jun.skiatree.Scalable
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.VOID
import com.cerebrallychallenged.jun.skiatree.toSegment
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT

class TextStyle private constructor(resource: CloseableResource) : CloseableResourceBearer(resource), Scalable<TextStyle> {
    companion object : CloseableResourceFactory<TextStyle>(::TextStyle, "skiatree_text_style_delete") {
        @JvmStatic
        private val textStyleNew = function("skiatree_text_style_new", ADDRESS)

        @JvmStatic
        private val textStyleClone = function(
            "skiatree_text_style_clone",
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val textStyleGetBackgroundPaint = function(
            "skiatree_text_style_get_background_paint",
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val textStyleSetBackgroundPaint = function(
            "skiatree_text_style_set_background_paint",
            VOID,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val textStyleGetForegroundPaint = function(
            "skiatree_text_style_get_foreground_paint",
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val textStyleSetForegroundPaint = function(
            "skiatree_text_style_set_foreground_paint",
            VOID,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val textStyleSetColor = function(
            "skiatree_text_style_set_color",
            VOID,
            ADDRESS,
            Color4fLayout
        )

        @JvmStatic
        private val textStyleSetTextBaseline = function(
            "skiatree_text_style_set_text_baseline",
            VOID,
            ADDRESS,
            JAVA_INT
        )

        @JvmStatic
        private val textStyleSetBaselineShift = function(
            "skiatree_text_style_set_baseline_shift",
            VOID,
            ADDRESS,
            JAVA_FLOAT
        )

        @JvmStatic
        private val textStyleSetFontFamilies = function(
            "skiatree_text_style_set_font_families",
            VOID,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val textStyleSetFontSize = function(
            "skiatree_text_style_set_font_size",
            VOID,
            ADDRESS,
            JAVA_FLOAT
        )

        @JvmStatic
        private val textStyleSetFontStyle = function(
            "skiatree_text_style_set_font_style",
            VOID,
            ADDRESS,
            JAVA_INT
        )

        @JvmStatic
        private val textStyleAddOutlineShadows = function(
            "skiatree_text_style_add_outline_shadows",
            VOID,
            ADDRESS,
            Color4fLayout,
            JAVA_FLOAT
        )

        operator fun invoke(): TextStyle = TextStyle { textStyleNew() as MemorySegment }
    }

    fun clone(): TextStyle = TextStyle { textStyleClone(address) as MemorySegment }

    var backgroundColor: Paint
        get() = Paint { textStyleGetBackgroundPaint(address) as MemorySegment }
        set(value) {
            textStyleSetBackgroundPaint(address, value.address)
        }

    var foregroundColor: Paint
        get() = Paint { textStyleGetForegroundPaint(address) as MemorySegment }
        set(value) {
            textStyleSetForegroundPaint(address, value.nullableAddress)
        }

    var color: FLinearColor = FLinearColor.Transparent
        set(value) {
            field = value
            confinedArena {
                textStyleSetColor(address, value.toSegment())
            }
        }

    var textBaseline: TextBaseline = TextBaseline.Alphabetic
        set(value) {
            field = value
            textStyleSetTextBaseline(address, value.ordinal)
        }

    var baselineShift: Float = 0.0f
        set(value) {
            field = value
            textStyleSetBaselineShift(address, value)
        }

    var fontFamilies: StringList = StringList.Empty
        set(value) {
            field = value
            textStyleSetFontFamilies(address, value.resource.address)
        }

    var fontSize: Float = 1.0f
        set(value) {
            field = value
            textStyleSetFontSize(address, value)
        }

    var fontStyle: FontStyle = FontStyle.Normal
        set(value) {
            field = value
            textStyleSetFontStyle(address, value.value)
        }

    fun addOutlineShadows(color: FLinearColor, amount: Float) {
        confinedArena {
            textStyleAddOutlineShadows(address, color.toSegment(), amount) as Unit
        }
    }

    override fun scale(factor: Float): TextStyle = clone().also { it.fontSize = fontSize * factor }
}
