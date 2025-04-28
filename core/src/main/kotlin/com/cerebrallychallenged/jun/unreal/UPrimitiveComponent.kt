package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.input.InputObservable
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class UPrimitiveComponent(ptr: CPointer) : USceneComponent(ptr), PrimitiveComponentLike, InputObservable {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    override val inputListeners: MutableList<(InputEvent) -> Unit> = mutableListOf()

    final override var castCinematicShadow: Boolean
        get() = getCastCinematicShadow(ptr)
        set(value) {
            setCastCinematicShadow(ptr, value)
        }

    final override var castDynamicShadow: Boolean
        get() = getCastDynamicShadow(ptr)
        set(value) {
            setCastDynamicShadow(ptr, value)
        }

    final override var castFarShadow: Boolean
        get() = getCastFarShadow(ptr)
        set(value) {
            setCastFarShadow(ptr, value)
        }

    final override var castHiddenShadow: Boolean
        get() = getCastHiddenShadow(ptr)
        set(value) {
            setCastHiddenShadow(ptr, value)
        }

    final override var castInsetShadow: Boolean
        get() = getCastInsetShadow(ptr)
        set(value) {
            setCastInsetShadow(ptr, value)
        }

    final override var castShadow: Boolean
        get() = getCastShadow(ptr)
        set(value) {
            setCastShadow(ptr, value)
        }

    final override var castShadowAsTwoSided: Boolean
        get() = getCastShadowAsTwoSided(ptr)
        set(value) {
            setCastShadowAsTwoSided(ptr, value)
        }

    final override var castStaticShadow: Boolean
        get() = getCastStaticShadow(ptr)
        set(value) {
            setCastStaticShadow(ptr, value)
        }

    final override var castVolumetricTranslucentShadow: Boolean
        get() = getCastVolumetricTranslucentShadow(ptr)
        set(value) {
            setCastVolumetricTranslucentShadow(ptr, value)
        }

    final override var collisionEnabled: ECollisionEnabled
        get() = ECollisionEnabled.values()[getCollisionEnabled(ptr)]
        set(value) {
            setCollisionEnabled(ptr, value.ordinal)
        }

    final override var customDepthStencilValue: Int
        get() = getCustomDepthStencilValue(ptr)
        set(value) {
            setCustomDepthStencilValue(ptr, value)
        }

    override fun getMaterial(index: Int): UMaterialInterface?
            = getMaterial(ptr, index).wrapNullableUObject()

    final override val numMaterials: Int
        get() = getNumMaterials(ptr)

    final override var renderCustomDepth: Boolean
        get() = getRenderCustomDepth(ptr)
        set(value) {
            setRenderCustomDepth(ptr, value)
        }

    override fun setMaterial(index: Int, material: UMaterialInterface?) {
        setMaterial(ptr, index, material.nullablePtr)
    }

    final override var simulatePhysics: Boolean
        get() = getSimulatePhysics(ptr)
        set(value) {
            setSimulatePhysics(ptr, value)
        }

    final override var useAsOccluder: Boolean
        get() = getUseAsOccluder(ptr)
        set(value) {
            setUseAsOccluder(ptr, value)
        }

    final override var visibleInRayTracing: Boolean
        get() = getVisibleInRayTracing(ptr)
        set(value) {
            setVisibleInRayTracing(ptr, value)
        }

    final override var visibleInReflectionCaptures: Boolean
        get() = getVisibleInReflectionCaptures(ptr)
        set(value) {
            setVisibleInReflectionCaptures(ptr, value)
        }
}

private external fun getCastCinematicShadow(ptr: CPointer): Boolean

private external fun getCastDynamicShadow(ptr: CPointer): Boolean

private external fun getCastFarShadow(ptr: CPointer): Boolean

private external fun getCastHiddenShadow(ptr: CPointer): Boolean

private external fun getCastInsetShadow(ptr: CPointer): Boolean

private external fun getCastShadow(ptr: CPointer): Boolean

private external fun getCastShadowAsTwoSided(ptr: CPointer): Boolean

private external fun getCastStaticShadow(ptr: CPointer): Boolean

private external fun getCastVolumetricTranslucentShadow(ptr: CPointer): Boolean

private external fun getCollisionEnabled(ptr: CPointer): Int

private external fun getCustomDepthStencilValue(ptr: CPointer): Int

private external fun getMaterial(ptr: CPointer, index: Int): CPointer

private external fun getNumMaterials(ptr: CPointer): Int

private external fun getRenderCustomDepth(ptr: CPointer): Boolean

private external fun getSimulatePhysics(ptr: CPointer): Boolean

private external fun getUseAsOccluder(ptr: CPointer): Boolean

private external fun getVisibleInRayTracing(ptr: CPointer): Boolean

private external fun getVisibleInReflectionCaptures(ptr: CPointer): Boolean

private external fun setCastCinematicShadow(ptr: CPointer, value: Boolean)

private external fun setCastDynamicShadow(ptr: CPointer, value: Boolean)

private external fun setCastFarShadow(ptr: CPointer, value: Boolean)

private external fun setCastHiddenShadow(ptr: CPointer, value: Boolean)

private external fun setCastInsetShadow(ptr: CPointer, value: Boolean)

private external fun setCastShadow(ptr: CPointer, value: Boolean)

private external fun setCastShadowAsTwoSided(ptr: CPointer, value: Boolean)

private external fun setCastStaticShadow(ptr: CPointer, value: Boolean)

private external fun setCastVolumetricTranslucentShadow(ptr: CPointer, value: Boolean)

private external fun setCollisionEnabled(ptr: CPointer, value: Int)

private external fun setCustomDepthStencilValue(ptr: CPointer, value: Int)

private external fun setMaterial(ptr: CPointer, index: Int, materialPtr: CPointer)

private external fun setRenderCustomDepth(ptr: CPointer, value: Boolean)

private external fun setSimulatePhysics(ptr: CPointer, value: Boolean)

private external fun setUseAsOccluder(ptr: CPointer, value: Boolean)

private external fun setVisibleInRayTracing(ptr: CPointer, value: Boolean)

private external fun setVisibleInReflectionCaptures(ptr: CPointer, value: Boolean)
