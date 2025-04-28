package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.unreal.APlayerController
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

open class UUserWidget(ptr: CPointer) : UWidget(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass

        fun createWidget(owningPlayer: APlayerController, userWidgetClass: UClass): UUserWidget
                = createWidget(owningPlayer.ptr, userWidgetClass.ptr).wrapUObject()
    }

    fun addToViewport(zOrder: Int) {
        addToViewport(ptr, zOrder)
    }
}

private external fun createWidget(owningPlayerPtr: CPointer, userWidgetClassPtr: CPointer): CPointer

private external fun addToViewport(ptr: CPointer, zOrder: Int)
