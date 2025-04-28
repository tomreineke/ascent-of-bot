package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedPtr

interface SWindow

val AnyRef<SWindow>.nativeWindow: TSharedPtr<FGenericWindow>
    get() = getNativeWindow(directPtr).wrapSharedPtr()

private external fun getNativeWindow(directPtr: CPointer): CPointer