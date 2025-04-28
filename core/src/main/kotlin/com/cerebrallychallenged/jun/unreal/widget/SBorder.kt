package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface SBorder : SCompoundWidget

var AnyRef<SBorder>.content: TSharedRef<SWidget>
    get() = getContent(directPtr).wrapSharedRef()
    set(value) {
        setContent(directPtr, value.sharedPtrPtr)
    }

private external fun getContent(directPtr: CPointer): CPointer

private external fun setContent(directPtr: CPointer, contentPtr: CPointer)