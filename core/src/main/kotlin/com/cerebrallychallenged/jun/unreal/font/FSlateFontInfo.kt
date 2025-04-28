package com.cerebrallychallenged.jun.unreal.font

import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface FSlateFontInfo {
    companion object {
        fun makeShared(): TSharedRef<FSlateFontInfo> = makeSharedImpl().wrapSharedRef()

        fun makeShared(
                fontObject: UObject,
                size: Int,
                typefaceFontName: String?,
                outlineSettings: TSharedRef<FFontOutlineSettings>
        ): TSharedRef<FSlateFontInfo>
                = makeSharedImpl(fontObject.ptr, size, typefaceFontName, outlineSettings.sharedPtrPtr).wrapSharedRef()
    }
}

private external fun makeSharedImpl(): CPointer

private external fun makeSharedImpl(
        fontObjectPtr: CPointer,
        size: Int,
        typefaceFontName: String?,
        outlineSettingsSharedPtr: CPointer
): CPointer