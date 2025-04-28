package com.cerebrallychallenged.jun.unreal.color

import com.cerebrallychallenged.jun.math.geo.Vec4f
import kotlin.math.roundToInt

class FColor(val packedARGB: Int) {
    companion object {
        val White = rgb(255, 255, 255)
        val Black = rgb(0, 0, 0)
        val Transparent = rgba(0, 0, 0, 0)
        val Red = rgb(255, 0, 0)
        val Green = rgb(0, 255, 0)
        val Blue = rgb(0, 0, 255)
        val Yellow = rgb(255, 255, 0)
        val Cyan = rgb(0, 255, 255)
        val Magenta = rgb(255, 0, 255)
        val Orange = rgb(243, 156, 18)
        val Purple = rgb(169, 7, 228)
        val Turquoise = rgb(26, 188, 156)
        val Silver = rgb(189, 195, 199)
        val Emerald = rgb(46, 204, 113)

        fun rgba(r: Int, g: Int, b: Int, a: Int): FColor {
            return FColor(
                    a and 0xFF shl 24
                            or (r and 0xFF shl 16)
                            or (g and 0xFF shl 8)
                            or (b and 0xFF)
            )
        }

        fun rgb(r: Int, g: Int, b: Int): FColor {
            return FColor(
                    r and 0xFF shl 16
                            or (g and 0xFF shl 8)
                            or (b and 0xFF)
            )
        }

        fun rgba(color: Vec4f): FColor {
            return rgba(
                    (color.r * 255.0).roundToInt(),
                    (color.g * 255.0).roundToInt(),
                    (color.b * 255.0).roundToInt(),
                    (color.a * 255.0).roundToInt()
            )
        }

        fun fromPackedARGB(packedARGB: Int): FColor = FColor(packedARGB)
    }

    val b: Int
        get() = packedARGB and 0xFF

    val g: Int
        get() = (packedARGB shr 8) and 0xFF

    val r: Int
        get() = (packedARGB shr 16) and 0xFF

    val a: Int
        get() = (packedARGB shr 24) and 0xFF
}
