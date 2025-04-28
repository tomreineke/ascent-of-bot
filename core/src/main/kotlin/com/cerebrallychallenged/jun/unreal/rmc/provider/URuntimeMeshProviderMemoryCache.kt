package com.cerebrallychallenged.jun.unreal.rmc.provider

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class URuntimeMeshProviderMemoryCache(ptr: CPointer) : URuntimeMeshProvider(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}