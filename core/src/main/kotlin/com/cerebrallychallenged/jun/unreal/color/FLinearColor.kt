package com.cerebrallychallenged.jun.unreal.color

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.math.geo.vec

class FLinearColor(val rgba: Vec4f) {
    companion object {
        val White = rgb(1.0f, 1.0f, 1.0f)
        val Gray = rgb(0.5f, 0.5f, 0.5f)
        val Black = rgb(0.0f, 0.0f, 0.0f)
        val Transparent = rgba(0.0f, 0.0f, 0.0f, 0.0f)
        val Red = rgb(1.0f, 0.0f, 0.0f)
        val Green = rgb(0.0f, 1.0f, 0.0f)
        val Blue = rgb(0.0f, 0.0f, 1.0f)
        val Yellow = rgb(1.0f, 1.0f, 0.0f)
        val Cyan = rgb(0.0f, 1.0f, 1.0f)

        fun rgb(r: Float, g: Float, b: Float): FLinearColor = rgba(vec(r, g, b, 1.0f))

        fun rgb(rgbColor: Vec3f): FLinearColor = rgba(rgbColor.append(1.0f))

        fun rgba(r: Float, g: Float, b: Float, a: Float): FLinearColor = rgba(vec(r, g, b, a))

        fun rgba(rgbaColor: Vec4f): FLinearColor = FLinearColor(rgbaColor)

        /**
         * https://api.unrealengine.com/INT/API/Runtime/Core/Math/FMath/Lerp/1/
         */
        @Convenience
        fun lerp(from: FLinearColor, to: FLinearColor, progress: Float): FLinearColor = from.interpolate(progress, to)
    }

    val r: Float
        get() = rgba.r

    val g: Float
        get() = rgba.g

    val b: Float
        get() = rgba.b

    val a: Float
        get() = rgba.a

    val rgb: Vec3f = vec(r, g, b)

    @Convenience
    fun interpolate(alpha: Float, other: FLinearColor): FLinearColor {
        return FLinearColor(rgba.interpolate(alpha, other.rgba))
    }

    @Convenience
    fun withA(a: Float): FLinearColor {
        return FLinearColor(vec(rgba.r, rgba.g, rgba.b, a))
    }

    operator fun times(factor: Float): FLinearColor = FLinearColor((rgb * factor).append(a))

    override fun hashCode(): Int = rgba.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is FLinearColor -> false
        else -> this.rgba == other.rgba
    }

    override fun toString(): String = "FLinearColor($r, $g, $b, $a)"
}
