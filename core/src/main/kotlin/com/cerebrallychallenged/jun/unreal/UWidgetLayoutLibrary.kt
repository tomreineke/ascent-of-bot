package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.util.CPointer

open class UWidgetLayoutLibrary(ptr: CPointer) : UBlueprintFunctionLibrary(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass

        fun getMousePositionOnViewport(worldContextObject: UObject = JunManager.defaultActor): Vec2f
                = getMousePositionOnViewport(worldContextObject.ptr)
    }
}

private external fun getMousePositionOnViewport(worldContextObjectPtr: CPointer): Vec2f