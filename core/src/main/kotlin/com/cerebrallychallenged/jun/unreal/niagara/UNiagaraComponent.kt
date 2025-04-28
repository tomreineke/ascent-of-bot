package com.cerebrallychallenged.jun.unreal.niagara

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.nullablePtr
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class UNiagaraComponent(ptr: CPointer) : UFXSystemComponent(ptr), NiagaraComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    override var asset: UNiagaraSystem?
        get() = getAsset(ptr).wrapNullableUObject()
        set(value) {
            setAsset(ptr, value.nullablePtr, true)
        }

    val isComplete: Boolean
        get() = isComplete(ptr)
}

private external fun getAsset(ptr: CPointer): CPointer

private external fun isComplete(ptr: CPointer): Boolean

private external fun setAsset(ptr: CPointer, templatePtr: CPointer, resetExistingOverrideParameters: Boolean)
