package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface SJunToolTipWidget : SBorder {
    companion object {
        fun createBySNew(): TSharedRef<SJunToolTipWidget> = createBySNewImpl().wrapSharedRef()
    }
}

var AnyRef<SJunToolTipWidget>.delay: Double
    get() = getDelay(directPtr)
    set(value) {
        setDelay(directPtr, value)
    }

private external fun createBySNewImpl(): CPointer

private external fun getDelay(directPtr: CPointer): Double

private external fun setDelay(directPtr: CPointer, delay: Double)