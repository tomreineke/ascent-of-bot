package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.skiatree.Canvas
import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.CloseableResourceFactory
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.VOID
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import kotlin.math.min

class Paragraph private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<Paragraph>(::Paragraph, "skiatree_paragraph_delete") {
        @JvmStatic
        private val paragraphLayout = function(
            "skiatree_paragraph_layout",
            VOID,
            ADDRESS,
            JAVA_FLOAT
        )

        @JvmStatic
        private val paragraphPaint = function(
            "skiatree_paragraph_paint",
            VOID,
            ADDRESS,
            ADDRESS,
            JAVA_FLOAT,
            JAVA_FLOAT
        )

        @JvmStatic
        private val paragraphGetHeight = function(
            "skiatree_paragraph_get_height",
            JAVA_FLOAT,
            ADDRESS
        )

        @JvmStatic
        private val paragraphGetMaxWidth = function(
            "skiatree_paragraph_get_max_width",
            JAVA_FLOAT,
            ADDRESS
        )

        @JvmStatic
        private val paragraphGetMaxIntrinsicWidth = function(
            "skiatree_paragraph_get_max_intrinsic_width",
            JAVA_FLOAT,
            ADDRESS
        )
    }

    init {
        layout(10000.0f)
    }

    internal lateinit var nodes: List<Node>

    fun layout(width: Float) {
        paragraphLayout(address, width)
    }

    fun paint(canvas: Canvas, point: Vec2f) {
        paragraphPaint(address, canvas.address, point.x, point.y)
    }

    val height: Float
        get() = paragraphGetHeight(address) as Float

    val maxWidth: Float
        get() = paragraphGetMaxWidth(address) as Float

    val maxIntrinsicWidth: Float = paragraphGetMaxIntrinsicWidth(address) as Float

    val width: Float
        get() = min(maxWidth, maxIntrinsicWidth)
}
