package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject
import com.cerebrallychallenged.jun.wrapUObject

class UClass(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass

        fun loadClass(className: String): UClass = loadClassImpl(className).wrapUObject()
    }

    val superClass: UClass?
        get() = getSuperClass(ptr).wrapNullableUObject()
}

private external fun loadClassImpl(className: String): CPointer

internal external fun getSuperClass(ptr: CPointer): CPointer
