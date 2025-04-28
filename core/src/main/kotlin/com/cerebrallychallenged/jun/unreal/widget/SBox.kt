package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface SBox : SPanel {
    companion object {
        fun createBySNew(): TSharedRef<SBox> = createBySNewImpl().wrapSharedRef()
    }
}

private external fun createBySNewImpl(): CPointer

var AnyRef<SBox>.content: TSharedRef<SWidget>
    @Deprecated("Property is write only")
    get() = error("Property is write only")
    set(value) {
        setContent(directPtr, value.sharedPtrPtr)
    }

private external fun setContent(directPtr: CPointer, contentPtr: CPointer)