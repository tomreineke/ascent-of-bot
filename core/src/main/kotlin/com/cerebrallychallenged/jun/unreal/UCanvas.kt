package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.font.UFont
import com.cerebrallychallenged.jun.util.CPointer

open class UCanvas(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    @Convenience
    fun drawText(font: UFont, text: String, position: Vec2f, scale: Vec2f) {
        drawText(font, text, position.x, position.y, scale.x, scale.y)
    }

    fun drawText(font: UFont, text: String, x: Float, y: Float, xScale: Float, yScale: Float) {
        drawText(ptr, font.ptr, text, x, y, xScale, yScale)
    }

    val sizeX: Float
        get() = getSizeX(ptr)

    val sizeY: Float
        get() = getSizeY(ptr)

    val size: Vec2f
        get() = vec(sizeX, sizeY)
}

private external fun drawText(
        ptr: CPointer,
        fontPtr: CPointer,
        text: String,
        x: Float,
        y: Float,
        xScale: Float,
        yScale: Float
)

private external fun getSizeX(ptr: CPointer): Float

private external fun getSizeY(ptr: CPointer): Float