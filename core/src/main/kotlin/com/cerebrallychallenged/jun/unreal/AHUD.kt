package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.unreal.font.UFont
import com.cerebrallychallenged.jun.util.CPointer

open class AHUD(ptr: CPointer) : AActor(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    @Convenience
    fun drawText(
            text: String,
            textColor: FLinearColor,
            screenPos: Vec2f,
            font: UFont,
            scale: Float,
            scalePosition: Boolean
    ) = drawText(text, textColor, screenPos.x, screenPos.y, font, scale, scalePosition)

    fun drawText(
            text: String,
            textColor: FLinearColor,
            screenX: Float,
            screenY: Float,
            font: UFont,
            scale: Float,
            scalePosition: Boolean
    ) {
        drawText(
                ptr,
                text,
                textColor.rgba,
                screenX,
                screenY,
                font.ptr,
                scale,
                scalePosition
        )
    }
}

external fun drawText(
        ptr: CPointer,
        text: String,
        textColor: Vec4f,
        screenX: Float,
        screenY: Float,
        fontPtr: CPointer,
        scale: Float,
        scalePosition: Boolean
)