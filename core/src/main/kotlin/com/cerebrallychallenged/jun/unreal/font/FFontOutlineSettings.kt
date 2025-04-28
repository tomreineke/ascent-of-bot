package com.cerebrallychallenged.jun.unreal.font

import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface FFontOutlineSettings {
    companion object {
        fun makeShared(outlineSize: Int = 0, color: FLinearColor = FLinearColor.Black): TSharedRef<FFontOutlineSettings>
                = makeShared(outlineSize, color.rgba).wrapSharedRef()
    }
}

private external fun makeShared(outlineSize: Int, color: Vec4f): CPointer