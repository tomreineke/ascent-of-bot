package com.cerebrallychallenged.jun.unreal.material

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

open class UMaterial(ptr: CPointer) : UMaterialInterface(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass

        fun getDefaultMaterial(domain: EMaterialDomain): UMaterial = getDefaultMaterial(domain.ordinal).wrapUObject()
    }
}

private external fun getDefaultMaterial(domain: Int): CPointer