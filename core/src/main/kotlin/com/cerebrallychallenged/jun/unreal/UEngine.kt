package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class UEngine(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    val gameViewport: UGameViewportClient?
        get() = getGameViewport(ptr).wrapNullableUObject()
}

private external fun getGameViewport(ptr: CPointer): CPointer