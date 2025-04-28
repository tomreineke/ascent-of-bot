package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.unreal.*
import com.cerebrallychallenged.jun.unreal.mesh.UMeshComponent
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject
import com.cerebrallychallenged.jun.wrapSharedPtr

open class UWidgetComponent(ptr: CPointer) : UMeshComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    var drawSize: Vec2f
        get() = getDrawSize(ptr)
        set(value) {
            setDrawSize(ptr, value)
        }

    var ownerPlayer: ULocalPlayer?
        get() = getOwnerPlayer(ptr).wrapNullableUObject()
        set(value) {
            setOwnerPlayer(ptr, value.nullablePtr)
        }

    var pivot: Vec2f
        get() = getPivot(ptr)
        set(value) {
            setPivot(ptr, value)
        }

    var slateWidget: TSharedPtr<SWidget>
        get() = getSlateWidget(ptr).wrapSharedPtr()
        set(value) {
            setSlateWidget(ptr, value.nullableSharedPtrPtr)
        }

    var widget: UUserWidget?
        get() = getWidget(ptr).wrapNullableUObject()
        set(value) {
            setWidget(ptr, value.nullablePtr)
        }

    var widgetSpace: EWidgetSpace
        get() = EWidgetSpace.values()[getWidgetSpace(ptr).toInt()]
        set(value) {
            setWidgetSpace(ptr, value.ordinal.toByte())
        }
}

private external fun getDrawSize(ptr: CPointer): Vec2f

private external fun getOwnerPlayer(ptr: CPointer): CPointer

private external fun getPivot(ptr: CPointer): Vec2f

private external fun getSlateWidget(ptr: CPointer): CPointer

private external fun getWidget(ptr: CPointer): CPointer

private external fun getWidgetSpace(ptr: CPointer): Byte




private external fun setDrawSize(ptr: CPointer, size: Vec2f)

private external fun setOwnerPlayer(ptr: CPointer, playerPtr: CPointer)

private external fun setPivot(ptr: CPointer, newValue: Vec2f)

private external fun setSlateWidget(ptr: CPointer, widgetPtrPtr: CPointer)

private external fun setWidget(ptr: CPointer, widgetPtr: CPointer)

private external fun setWidgetSpace(ptr: CPointer, newValue: Byte)