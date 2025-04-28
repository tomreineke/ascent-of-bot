package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject
import kotlin.reflect.full.companionObjectInstance

open class UObject(val ptr: CPointer) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun addToRoot() {
        addToRoot(ptr)
    }

    fun closeWrapper() {
        JunManager.closeUObject(ptr)
    }

    fun isA(unrealClass: UClass): Boolean = isA(ptr, unrealClass.ptr)

    val isRooted: Boolean
        get() = isRooted(ptr)

    val name: String
        get() = getName(ptr)

    fun removeFromRoot() {
        removeFromRoot(ptr)
    }

    val unrealClass: UClass
        get() = getClass(ptr).wrapUObject()

    override fun equals(other: Any?): Boolean = other === this || other is UObject && ptr == other.ptr

    override fun hashCode(): Int = ptr.hashCode()
}

interface UObjectCompanion {
    var staticClass: UClass
}

val UObject?.nullablePtr: CPointer
    get() = this?.ptr ?: 0L

inline fun <reified T : UObject> staticClass(): UClass {
    return (T::class.companionObjectInstance as UObjectCompanion).staticClass
}

inline fun <reified T : UObject> newObject(
        outerObject: UObject? = null,
        clazz: UClass = staticClass<T>(),
        name: String? = null
): T = newObject(outerObject.nullablePtr, clazz.ptr, name).wrapUObject()

@Suppress("UNCHECKED_CAST")
fun <T : UObject> newObject(
        outerObject: UObject? = null,
        clazz: Class<T>,
        name: String? = null
): T = newObject(
        outerObject.nullablePtr,
        (clazz.kotlin.companionObjectInstance as UObjectCompanion).staticClass.ptr,
        name
).wrapUObject<UObject>() as T

private external fun addToRoot(ptr: CPointer)

private external fun isA(ptr: CPointer, classPtr: CPointer): Boolean

private external fun isRooted(ptr: CPointer): Boolean

internal external fun getClass(ptr: CPointer): CPointer

internal external fun getName(ptr: CPointer): String

@PublishedApi
internal external fun newObject(outerPtr: CPointer, classPtr: CPointer, name: String?): CPointer

private external fun removeFromRoot(ptr: CPointer)