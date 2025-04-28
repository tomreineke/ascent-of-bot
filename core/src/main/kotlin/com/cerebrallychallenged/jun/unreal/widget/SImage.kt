package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.DirectRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.slate.FSlateBrush
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface SImage : SLeafWidget {
    companion object {
        fun createBySNew(): TSharedRef<SImage> = createBySNewImpl().wrapSharedRef()
    }
}

var AnyRef<SImage>.image: DirectRef<FSlateBrush>
    @Deprecated("Property is write only")
    get() = error("Property is write only")
    set(value) {
        setImage(directPtr, value.directPtr)
    }

private external fun createBySNewImpl(): CPointer

private external fun setImage(directPtr: CPointer, slateBrushDirectPtr: CPointer)