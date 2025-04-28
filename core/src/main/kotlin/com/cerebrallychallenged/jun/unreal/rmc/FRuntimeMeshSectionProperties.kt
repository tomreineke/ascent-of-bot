package com.cerebrallychallenged.jun.unreal.rmc

import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface FRuntimeMeshSectionProperties {
    companion object {
        fun makeShared(): TSharedRef<FRuntimeMeshSectionProperties> = makeSharedImpl().wrapSharedRef()
    }
}

var AnyRef<FRuntimeMeshSectionProperties>.castsShadow: Boolean
    get() = castsShadow(directPtr)
    set(value) {
        setCastsShadow(directPtr, value)
    }

var AnyRef<FRuntimeMeshSectionProperties>.isVisible: Boolean
    get() = isVisible(directPtr)
    set(value) {
        setVisible(directPtr, value)
    }

var AnyRef<FRuntimeMeshSectionProperties>.materialSlot: Int
    get() = getMaterialSlot(directPtr)
    set(value) {
        setMaterialSlot(directPtr, value)
    }

var AnyRef<FRuntimeMeshSectionProperties>.numTexCoords: Int
    get() = getNumTexCoords(directPtr)
    set(value) {
        setNumTexCoords(directPtr, value)
    }

var AnyRef<FRuntimeMeshSectionProperties>.updateFrequency: ERuntimeMeshUpdateFrequency
    get() = ERuntimeMeshUpdateFrequency.values()[getUpdateFrequency(directPtr).toInt()]
    set(value) {
        setUpdateFrequency(directPtr, value.ordinal.toByte())
    }

var AnyRef<FRuntimeMeshSectionProperties>.useHighPrecisionTangents: Boolean
    get() = getUseHighPrecisionTangents(directPtr)
    set(value) {
        setUseHighPrecisionTangents(directPtr, value)
    }

var AnyRef<FRuntimeMeshSectionProperties>.useHighPrecisionTexCoords: Boolean
    get() = getUseHighPrecisionTexCoords(directPtr)
    set(value) {
        setUseHighPrecisionTexCoords(directPtr, value)
    }

var AnyRef<FRuntimeMeshSectionProperties>.wants32BitIndices: Boolean
    get() = wants32BitIndices(directPtr)
    set(value) {
        setWants32BitIndices(directPtr, value)
    }

private external fun castsShadow(directPtr: CPointer): Boolean

private external fun getMaterialSlot(directPtr: CPointer): Int

private external fun getNumTexCoords(directPtr: CPointer): Int

private external fun getUpdateFrequency(directPtr: CPointer): Byte

private external fun getUseHighPrecisionTangents(directPtr: CPointer): Boolean

private external fun getUseHighPrecisionTexCoords(directPtr: CPointer): Boolean

private external fun isVisible(directPtr: CPointer): Boolean

private external fun makeSharedImpl(): CPointer

private external fun setCastsShadow(directPtr: CPointer, newValue: Boolean)

private external fun setMaterialSlot(directPtr: CPointer, newValue: Int)

private external fun setNumTexCoords(directPtr: CPointer, newValue: Int)

private external fun setUpdateFrequency(directPtr: CPointer, newUpdateFrequency: Byte)

private external fun setUseHighPrecisionTangents(directPtr: CPointer, newValue: Boolean)

private external fun setUseHighPrecisionTexCoords(directPtr: CPointer, newValue: Boolean)

private external fun setVisible(directPtr: CPointer, newValue: Boolean)

private external fun setWants32BitIndices(directPtr: CPointer, newValue: Boolean)

private external fun wants32BitIndices(directPtr: CPointer): Boolean