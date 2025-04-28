package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.nullableAddress
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.geo.IPointLayout
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.geo.toSegment
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT

class Canvas(internal val address: MemorySegment) {
    companion object {
        @JvmStatic
        private val canvasClear = function(
            "skiatree_canvas_clear",
            VOID,
            ADDRESS,
            Color4fLayout
        )

        @JvmStatic
        private val canvasDrawCircle = function(
            "skiatree_canvas_draw_circle",
            VOID,
            ADDRESS,
            IPointLayout,
            JAVA_FLOAT,
            ADDRESS
        )

        @JvmStatic
        private val canvasDrawImage = function(
            "skiatree_canvas_draw_image",
            VOID,
            ADDRESS,
            ADDRESS,
            JAVA_FLOAT,
            JAVA_FLOAT,
            ADDRESS
        )

        @JvmStatic
        private val canvasDrawImageNine = function(
            "skiatree_canvas_draw_image_nine",
            VOID,
            ADDRESS,
            ADDRESS,
            IRect.Layout,
            IRect.Layout,
            JAVA_INT,
            ADDRESS
        )

        @JvmStatic
        private val canvasDrawRect = function(
            "skiatree_canvas_draw_rect",
            VOID,
            ADDRESS,
            IRect.Layout,
            ADDRESS
        )
    }

    fun clear(color: FLinearColor) {
        confinedArena {
            canvasClear(address, color.toSegment())
        }
    }

    fun drawCircle(center: Vec2f, radius: Float, paint: Paint) {
        confinedArena {
            canvasDrawCircle(address, center.toSegment(), radius, paint.address)
        }
    }

    fun drawImage(image: SkiaImage, leftTop: Vec2f, paint: Paint? = null) {
        canvasDrawImage(
            address,
            image.address,
            leftTop.x,
            leftTop.y,
            paint.nullableAddress
        )
    }

    fun drawImageNine(
        image: SkiaImage,
        center: IRect,
        dst: IRect,
        filterMode: FilterMode = FilterMode.Linear,
        paint: Paint? = null
    ) {
        confinedArena {
            canvasDrawImageNine(
                address,
                image.address,
                center.toSegment(),
                dst.toSegment(),
                filterMode.ordinal,
                paint.nullableAddress
            )
        }
    }

    fun drawRect(rect: IRect, paint: Paint) {
        confinedArena {
            canvasDrawRect(address, rect.toSegment(), paint.address)
        }
    }
}
