package com.cerebrallychallenged.jun.unreal.slate

import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.UTexture2D
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface FDeferredCleanupSlateBrush {
    companion object {
        fun createBrush(texture: UTexture2D): TSharedRef<FDeferredCleanupSlateBrush>
                = createBrush(texture.ptr).wrapSharedRef()
    }
}

private external fun createBrush(texturePtr: CPointer): CPointer