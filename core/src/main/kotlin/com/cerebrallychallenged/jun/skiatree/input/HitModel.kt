package com.cerebrallychallenged.jun.skiatree.input

import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.skiatree.Scalable
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.geo.scale
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import com.cerebrallychallenged.jun.skiatree.guardedUnitArena
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG

sealed class HitModel {
    companion object {
        @JvmStatic
        private val nodeSetHitModelNone = function(
            "skiatree_node_set_hit_model_none",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG
        )

        @JvmStatic
        private val nodeSetHitModelRect = function(
            "skiatree_node_set_hit_model_rect",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            IRect.Layout
        )

        @JvmStatic
        private val nodeSetHitModelRoundedRect = function(
            "skiatree_node_set_hit_model_rounded_rect",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            IRect.Layout,
            JAVA_INT
        )
    }

    object None : HitModel() {
        override fun setFor(node: Node) {
            guardedUnit {
                nodeSetHitModelNone(
                    SkiaTreeApi.libraryPointer,
                    node.resource.key,
                ) as Byte
            }
        }

        override fun scale(factor: Float): HitModel = this
    }

    data class Rect(val border: IRect = IRect.Empty) : HitModel(), Scalable<Rect> {
        override fun setFor(node: Node) {
            guardedUnitArena {
                nodeSetHitModelRect(
                    SkiaTreeApi.libraryPointer,
                    node.resource.key,
                    border.toSegment()
                ) as Byte
            }
        }

        override fun scale(factor: Float): Rect = Rect(border.scale(factor))
    }

    data class RoundedRect(val border: IRect, val cornerRadius: Int) : HitModel(), Scalable<RoundedRect> {
        override fun setFor(node: Node) {
            guardedUnitArena {
                nodeSetHitModelRoundedRect(
                    SkiaTreeApi.libraryPointer,
                    node.resource.key,
                    border.toSegment(),
                    cornerRadius
                ) as Byte
            }
        }

        override fun scale(factor: Float): RoundedRect = RoundedRect(
            border.scale(factor),
            (cornerRadius * factor).floorToInt()
        )
    }

    internal abstract fun setFor(node: Node)

    abstract fun scale(factor: Float): HitModel
}
