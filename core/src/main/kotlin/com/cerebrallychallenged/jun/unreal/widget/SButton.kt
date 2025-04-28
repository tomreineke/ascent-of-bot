package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface SButton : SBorder {
    companion object {
        fun createBySNew(): TSharedRef<SButton> = createBySNewImpl().wrapSharedRef()
    }
}

private external fun createBySNewImpl(): CPointer