package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.util.CPointer

interface SWidget

val AnyRef<SWidget>.supportsKeyBoardFocus: Boolean
    get() = supportsKeyBoardFocus(directPtr)

var AnyRef<SWidget>.visbility: EVisibility
    get() = EVisibility.values()[getVisibility(directPtr)]
    set(value) {
        setVisibility(directPtr, value.ordinal)
    }

private external fun getVisibility(directPtr: CPointer): Int

private external fun setVisibility(directPtr: CPointer, visibilityMagic: Int)

private external fun supportsKeyBoardFocus(directPtr: CPointer): Boolean