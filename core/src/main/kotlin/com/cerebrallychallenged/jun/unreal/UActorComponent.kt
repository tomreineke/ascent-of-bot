package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.util.CPointer

open class UActorComponent(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun destroyComponent(promoteChildren: Boolean = false) {
        destroyComponent(ptr, promoteChildren)
        JunManager.closeUObject(ptr)
    }

    val isRegistered: Boolean
        get() = isRegistered(ptr)

    fun markRenderDynamicDataDirty() {
        markRenderDynamicDataDirty(ptr)
    }

    fun markRenderStateDirty() {
        markRenderStateDirty(ptr)
    }

    fun markRenderTransformDirty() {
        markRenderTransformDirty(ptr)
    }

    fun registerComponent() {
        registerComponent(ptr)
    }

    fun unregisterComponent() {
        unregisterComponent(ptr)
    }
}

private external fun destroyComponent(ptr: CPointer, promoteChildren: Boolean)

private external fun isRegistered(ptr: CPointer): Boolean

private external fun markRenderDynamicDataDirty(ptr: CPointer)

private external fun markRenderStateDirty(ptr: CPointer)

private external fun markRenderTransformDirty(ptr: CPointer)

private external fun registerComponent(ptr: CPointer)

private external fun unregisterComponent(ptr: CPointer)