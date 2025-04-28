package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.color.FColor
import com.cerebrallychallenged.jun.unreal.font.UFont
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class UTextRenderComponent(ptr: CPointer) : UPrimitiveComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    var font: UFont?
        get() = getFont(ptr).wrapNullableUObject()
        set(value) {
            setFont(ptr, value.nullablePtr)
        }

    var horizontalAlignment: EHorizTextAlignment
        get() = EHorizTextAlignment.values()[getHorizontalAlignment(ptr)]
        set(value) {
            setHorizontalAlignment(ptr, value.ordinal)
        }

    var horizSpacingAdjust: Float
        get() = getHorizSpacingAdjust(ptr)
        set(value) {
            setHorizSpacingAdjust(ptr, value)
        }

    @Convenience
    var scale: Vec2f
        get() = vec(xScale, yScale)
        set(value) {
            xScale = value.x
            yScale = value.y
        }

    var text: String
        get() = getText(ptr)
        set(value) {
            setText(ptr, value)
        }

    var textMaterial: UMaterialInterface?
        get() = getTextMaterial(ptr).wrapNullableUObject()
        set(value) {
            setTextMaterial(ptr, value.nullablePtr)
        }

    var textRenderColor: FColor
        get() = FColor.fromPackedARGB(getTextRenderColor(ptr))
        set(value) {
            setTextRenderColor(ptr, value.packedARGB)
        }

    var verticalAlignment: EVerticalTextAlignment
        get() = EVerticalTextAlignment.values()[getVerticalAlignment(ptr)]
        set(value) {
            setVerticalAlignment(ptr, value.ordinal)
        }

    var vertSpacingAdjust: Float
        get() = getVertSpacingAdjust(ptr)
        set(value) {
            setVertSpacingAdjust(ptr, value)
        }

    var worldSize: Float
        get() = getWorldSize(ptr)
        set(value) {
            setWorldSize(ptr, value)
        }

    var xScale: Float
        get() = getXScale(ptr)
        set(value) {
            setXScale(ptr, value)
        }

    var yScale: Float
        get() = getYScale(ptr)
        set(value) {
            setYScale(ptr, value)
        }
}

private external fun getFont(ptr: CPointer): CPointer

private external fun getHorizontalAlignment(ptr: CPointer): Int

private external fun getHorizSpacingAdjust(ptr: CPointer): Float

private external fun getText(ptr: CPointer): String

private external fun getTextMaterial(ptr: CPointer): CPointer

private external fun getTextRenderColor(ptr: CPointer): Int

private external fun getVerticalAlignment(ptr: CPointer): Int

private external fun getVertSpacingAdjust(ptr: CPointer): Float

private external fun getWorldSize(ptr: CPointer): Float

private external fun getXScale(ptr: CPointer): Float

private external fun getYScale(ptr: CPointer): Float

private external fun setFont(ptr: CPointer, fontPtr: CPointer)

private external fun setHorizontalAlignment(ptr: CPointer, newValue: Int)

private external fun setHorizSpacingAdjust(ptr: CPointer, newValue: Float)

private external fun setText(ptr: CPointer, text: String)

private external fun setTextMaterial(ptr: CPointer, materialPtr: CPointer)

private external fun setTextRenderColor(ptr: CPointer, color: Int)

private external fun setVerticalAlignment(ptr: CPointer, newValue: Int)

private external fun setVertSpacingAdjust(ptr: CPointer, newValue: Float)

private external fun setWorldSize(ptr: CPointer, newValue: Float)

private external fun setXScale(ptr: CPointer, newValue: Float)

private external fun setYScale(ptr: CPointer, newValue: Float)