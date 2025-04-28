package com.cerebrallychallenged.jun.unreal.spline

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.util.buffers.LayoutFSplinePoint
import com.cerebrallychallenged.jun.util.buffers.Memory

open class USplineComponent(ptr: CPointer) : UPrimitiveComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun addPoints(points: Memory<LayoutFSplinePoint>, updateSpline: Boolean) {
        addPoints(ptr, points.baseAddress, points.size, updateSpline)
    }

    fun clearSplinePoints(updateSpline: Boolean) {
        clearSplinePoints(ptr, updateSpline)
    }

    var drawDebug: Boolean
        get() = getDrawDebug(ptr)
        set(value) {
            setDrawDebug(ptr, value)
        }

//    fun setSplinePoints(points: Memory<LayoutFSplinePoint>, coordinateSpace: ESplineCoordinateSpace, updateSpline: Boolean) {
//        setSplinePoints(ptr, points.baseAddress.toRawLongValue(), points.size, coordinateSpace.ordinal, updateSpline)
//    }

    fun updateSpline() {
        updateSpline(ptr)
    }
}

private external fun addPoints(ptr: CPointer, dataPtr: CPointer, elementCount: Long, updateSpline: Boolean)

private external fun clearSplinePoints(ptr: CPointer, updateSpline: Boolean)

private external fun getDrawDebug(ptr: CPointer): Boolean

private external fun setDrawDebug(ptr: CPointer, debugDraw: Boolean)

//private external fun setSplinePoints(ptr: CPointer, dataPtr: CPointer, elementCount: Long, coordinateSpace: Int, updateSpline: Boolean)

private external fun updateSpline(ptr: CPointer)
