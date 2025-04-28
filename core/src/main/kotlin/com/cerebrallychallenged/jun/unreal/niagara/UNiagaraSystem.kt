package com.cerebrallychallenged.jun.unreal.niagara

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class UNiagaraSystem(ptr: CPointer) : UFXSystemAsset(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}
