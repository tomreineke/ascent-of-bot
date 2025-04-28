package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.nullableAddress
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.geo.scale
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_LONG
import kotlin.math.roundToInt

sealed class Background {
    companion object {
        @JvmStatic
        private val nodeSetBackgroundEmpty = function(
            "skiatree_node_set_background_empty",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE
        )

        @JvmStatic
        private val nodeSetBackgroundRect = function(
            "skiatree_node_set_background_rect",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            ADDRESS,
            ADDRESS,
            JAVA_FLOAT,
            IRect.Layout
        )

        @JvmStatic
        private val nodeSetBackgroundWireframe = function(
            "skiatree_node_set_background_wireframe",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            Color4fLayout
        )

        @JvmStatic
        private val nodeSetBackgroundNinepatch = function(
            "skiatree_node_set_background_ninepatch",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            ADDRESS,
            IRect.Layout,
            IRect.Layout,
            ADDRESS
        )

        @JvmStatic
        private val nodeSetBackgroundImage = function(
            "skiatree_node_set_background_image",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            ADDRESS,
            IRect.Layout,
            ADDRESS
        )

        @JvmStatic
        private val nodeSetBackgroundPath = function(
            "skiatree_node_set_background_path",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val nodeRemoveBackground = function(
            "skiatree_node_remove_background",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE
        )

        internal fun removeFor(node: Node, inputState: InputState) {
            guardedUnit {
                nodeRemoveBackground(
                    libraryPointer,
                    node.resource.key,
                    inputState.value.toByte()
                ) as Byte
            }
        }
    }

    object Empty : Background(), Scalable<Empty> {
        override fun setFor(node: Node, inputState: InputState) {
            guardedUnit {
                nodeSetBackgroundEmpty(
                    libraryPointer,
                    node.resource.key,
                    inputState.value.toByte()
                ) as Byte
            }
        }

        override fun scale(factor: Float): Empty = this
    }

    class Rect(
        val firstPaint: Paint,
        val secondPaint: Paint? = null,
        val cornerRadius: Float = 0.0f,
        val overshoot: IRect = IRect.Empty
    ) : Background(), Scalable<Rect> {
        companion object {
            operator fun invoke(color: FLinearColor, style: PaintStyle): Rect =
                Rect(Paint().apply {
                    this.color = color
                    this.style = style
                })
        }

        override fun setFor(node: Node, inputState: InputState) {
            guardedUnitArena {
                nodeSetBackgroundRect(
                    libraryPointer,
                    node.resource.key,
                    inputState.value.toByte(),
                    firstPaint.address,
                    secondPaint.nullableAddress,
                    cornerRadius,
                    overshoot.toSegment()
                ) as Byte
            }
        }

        override fun scale(factor: Float): Rect = this
    }

    data class Wireframe(val color: FLinearColor) : Background(), Scalable<Wireframe> {
        override fun setFor(node: Node, inputState: InputState) {
            guardedUnitArena {
                nodeSetBackgroundWireframe(
                    libraryPointer,
                    node.resource.key,
                    inputState.value.toByte(),
                    color.toSegment()
                ) as Byte
            }
        }

        override fun scale(factor: Float): Wireframe = this
    }

    data class NinePatch(
        val image: SkiaImage,
        val overshoot: IRect,
        val center: IRect,
        val paint: Paint? = null
    ) : Background(), Scalable<NinePatch> {
        override fun setFor(node: Node, inputState: InputState) {
            guardedUnitArena {
                nodeSetBackgroundNinepatch(
                    libraryPointer,
                    node.resource.key,
                    inputState.value.toByte(),
                    image.address,
                    overshoot.toSegment(),
                    center.toSegment(),
                    paint.nullableAddress
                ) as Byte
            }
        }

        override fun scale(factor: Float): NinePatch {
            val resizedImage = image.scale(factor)
            val resizedCenterLeft = (center.left * factor).roundToInt()
            val resizedCenterTop = (center.top * factor).roundToInt()
            val resizedCenterWidth = ((center.right - center.left) * factor).ceilToInt()
            val resizedCenterHeight = ((center.bottom - center.top) * factor).ceilToInt()
            val resizedCenter = IRect(
                resizedCenterLeft,
                resizedCenterTop,
                resizedCenterLeft + resizedCenterWidth,
                resizedCenterTop + resizedCenterHeight
            )
            return NinePatch(resizedImage, overshoot.scale(factor), resizedCenter, paint)
        }
    }

    data class Image(
        val image: SkiaImage,
        val overshoot: IRect,
        val paint: Paint? = null
    ) : Background(), Scalable<Image> {
        override fun setFor(node: Node, inputState: InputState) {
            guardedUnitArena {
                nodeSetBackgroundImage(
                    libraryPointer,
                    node.resource.key,
                    inputState.value.toByte(),
                    image.address,
                    overshoot.toSegment(),
                    paint.nullableAddress
                ) as Byte
            }
        }

        override fun scale(factor: Float): Image {
            return Image(image.scale(factor), overshoot.scale(factor), paint)
        }
    }

    data class Path(
        val path: SkiaPath,
        val paint: Paint
    ) : Background() {
        override fun setFor(node: Node, inputState: InputState) {
            guardedUnit {
                nodeSetBackgroundPath(
                    libraryPointer,
                    node.resource.key,
                    inputState.value.toByte(),
                    path.address,
                    paint.address
                ) as Byte
            }
        }

        override fun scale(factor: Float): Path = this
    }

    internal abstract fun setFor(node: Node, inputState: InputState)

    abstract fun scale(factor: Float): Background
}
