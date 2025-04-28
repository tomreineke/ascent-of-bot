package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.UVisual
import com.cerebrallychallenged.jun.util.CPointer

open class UWidget(ptr: CPointer) : UVisual(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun removeFromParent() {
        removeFromParent(ptr)
    }

    fun setKeyboardFocus() {
        setKeyboardFocus(ptr)
    }
}

private external fun removeFromParent(ptr: CPointer)

private external fun setKeyboardFocus(ptr: CPointer)
