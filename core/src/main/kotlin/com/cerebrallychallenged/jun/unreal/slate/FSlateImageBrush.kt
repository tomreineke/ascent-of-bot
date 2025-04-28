package com.cerebrallychallenged.jun.unreal.slate

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface FSlateImageBrush {
    companion object {
        fun makeShared(
                name: String,
                imageSize: Vec2f,
                color: FLinearColor,
                tiling: ESlateBrushTileType,
                imageType: ESlateBrushImageType
        ): TSharedRef<FSlateImageBrush>
                = makeShared(name, imageSize, color.rgba, tiling.ordinal, imageType.ordinal).wrapSharedRef()
    }
}

private external fun makeShared(name: String, imageSize: Vec2f, color: Vec4f, tiling: Int, imageType: Int): CPointer