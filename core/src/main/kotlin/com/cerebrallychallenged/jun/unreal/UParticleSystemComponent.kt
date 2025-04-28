package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class UParticleSystemComponent(ptr: CPointer) : UPrimitiveComponent(ptr), ParticleSystemComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    val hasCompleted: Boolean
        get() = hasCompleted(ptr)

    final override var template: UParticleSystem?
        get() = getTemplate(ptr).wrapNullableUObject()
        set(value) {
            setTemplate(ptr, value.nullablePtr)
        }
}

private external fun getTemplate(ptr: CPointer): CPointer

private external fun hasCompleted(ptr: CPointer): Boolean

private external fun setTemplate(ptr: CPointer, templatePtr: CPointer)
