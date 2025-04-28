package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.nullableAddress
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BOOLEAN
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_FLOAT

class Paint private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<Paint>(::Paint, "skiatree_paint_delete") {
        @JvmStatic
        private val paintNew = function("skiatree_paint_new", ADDRESS)

        @JvmStatic
        private val paintSetAntiAlias = function(
            "skiatree_paint_set_anti_alias",
            VOID,
            ADDRESS,
            JAVA_BOOLEAN
        )

        @JvmStatic
        private val paintSetColor = function(
            "skiatree_paint_set_color",
            VOID,
            ADDRESS,
            Color4fLayout
        )

        @JvmStatic
        private val paintSetColorFilter = function(
            "skiatree_paint_set_color_filter",
            VOID,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val paintSetImageFilter = function(
            "skiatree_paint_set_image_filter",
            VOID,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val paintSetPathEffect = function(
            "skiatree_paint_set_path_effect",
            VOID,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val paintSetShader = function(
            "skiatree_paint_set_shader",
            VOID,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val paintSetStrokeWidth = function(
            "skiatree_paint_set_stroke_width",
            VOID,
            ADDRESS,
            JAVA_FLOAT
        )

        @JvmStatic
        private val paintSetStyle = function(
            "skiatree_paint_set_style",
            VOID,
            ADDRESS,
            JAVA_BYTE
        )

        operator fun invoke(): Paint = Paint { paintNew() as MemorySegment }
    }

    init {
        createWeakReference(resource)
    }

    var antiAlias: Boolean = false
        set(value) {
            field = value
            paintSetAntiAlias(address, value)
        }

    var color: FLinearColor = FLinearColor.Transparent
        set(value) {
            field = value
            confinedArena {
                paintSetColor(address, value.toSegment()) as Unit
            }
        }

    var colorFilter: ColorFilter? = null
        set(value) {
            field = value
            paintSetColorFilter(address, colorFilter.nullableAddress) as Unit
        }

    var imageFilter: ImageFilter? = null
        set(value) {
            field = value
            paintSetImageFilter(address, imageFilter.nullableAddress) as Unit
        }

    var pathEffect: PathEffect? = null
        set(value) {
            field = value
            paintSetPathEffect(address, pathEffect.nullableAddress) as Unit
        }

    var shader: Shader? = null
        set(value) {
            field = value
            paintSetShader(address, shader.nullableAddress) as Unit
        }

    var strokeWidth: Float = 1.0f
        set(value) {
            field = value
            paintSetStrokeWidth(address, value) as Unit
        }

    var style: PaintStyle = PaintStyle.Fill
        set(value) {
            field = value
            paintSetStyle(address, value.ordinal.toByte())
        }
}
