package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.unreal.color.FColor
import com.cerebrallychallenged.jun.util.CPointer

open class UArrowComponent(ptr: CPointer) : UPrimitiveComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    var arrowColor: FColor
        get() = FColor.fromPackedARGB(getArrowColor(ptr))
        set(value) {
            setArrowColor(ptr, value.packedARGB)
        }

    var arrowSize: Float
        get() = getArrowSize(ptr)
        set(value) {
            setArrowSize(ptr, value)
        }
}

private external fun getArrowColor(ptr: CPointer): Int

private external fun getArrowSize(ptr: CPointer): Float

private external fun setArrowColor(ptr: CPointer, color: Int)

private external fun setArrowSize(ptr: CPointer, arrowSize: Float)