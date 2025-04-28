package com.cerebrallychallenged.jun.unreal.rmc

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.mesh.UMeshComponent
import com.cerebrallychallenged.jun.unreal.nullablePtr
import com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProvider
import com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStatic
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject
import com.cerebrallychallenged.jun.wrapUObject

open class URuntimeMeshComponent(ptr: CPointer) : UMeshComponent(ptr), RuntimeMeshComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    override fun getOrCreateRuntimeMesh(): URuntimeMesh = getOrCreateRuntimeMesh(ptr).wrapUObject()

    override fun initialize(provider: URuntimeMeshProvider) {
        initialize(ptr, provider.ptr)
    }

    override val provider: URuntimeMeshProvider?
        get() = getProvider(ptr).wrapNullableUObject()

    override var runtimeMesh: URuntimeMesh?
        get() = getRuntimeMesh(ptr).wrapNullableUObject()
        set(value) {
            setRuntimeMesh(ptr, value.nullablePtr)
        }

    override var runtimeMeshMobility: ERuntimeMeshMobility
        get() = ERuntimeMeshMobility.values()[getRuntimeMeshMobility(ptr).toInt()]
        set(value) {
            setRuntimeMeshMobility(ptr, value.ordinal.toByte())
        }
}

private external fun getOrCreateRuntimeMesh(ptr: CPointer): CPointer

private external fun getProvider(ptr: CPointer): CPointer

private external fun getRuntimeMesh(ptr: CPointer): CPointer

private external fun getRuntimeMeshMobility(ptr: CPointer): Byte

private external fun initialize(ptr: CPointer, providerPtr: CPointer)

private external fun setRuntimeMesh(ptr: CPointer, meshPtr: CPointer)

private external fun setRuntimeMeshMobility(ptr: CPointer, newMobility: Byte)
