package com.cerebrallychallenged.jun.unreal.slate

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.UTexture2D
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface FSlateDynamicImageBrush : FSlateBrush {
    companion object {
        fun makeShared(texture: UTexture2D, size: Vec2f, name: String? = null): TSharedRef<FSlateDynamicImageBrush> =
                makeShared(texture.ptr, size, name).wrapSharedRef()
    }
}

private external fun makeShared(texturePtr: CPointer, size: Vec2f, name: String?): CPointer