package com.cerebrallychallenged.jun.unreal.sound

import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface FSoundAttenuationSettings {
    companion object {
        fun create(): TSharedRef<FSoundAttenuationSettings> = createSoundAttenuationSettings().wrapSharedRef()
    }
}

var TSharedRef<FSoundAttenuationSettings>.spatialize: Boolean
    get() = getSpatialize(directPtr)
    set(value) {
        setSpatialize(directPtr, value)
    }

private external fun createSoundAttenuationSettings(): CPointer

private external fun getSpatialize(ptr: CPointer): Boolean

private external fun setSpatialize(ptr: CPointer, spatialize: Boolean)
