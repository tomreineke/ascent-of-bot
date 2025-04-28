package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject
import com.cerebrallychallenged.jun.wrapUObject
import kotlin.reflect.full.companionObjectInstance

/**
 * http://api.unrealengine.com/INT/API/Runtime/Engine/GameFramework/AActor/index.html
 */
open class AActor(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    var actorLocation: Vec3f
        get() = getActorLocation(ptr)
        set(value) {
            setActorLocation(ptr, value)
        }

    val actorQuat: Quaternion
        get() = getActorQuat(ptr)

    var actorRelativeScale3D: Vec3f
        get() = getActorRelativeScale3D(ptr)
        set(value) {
            setActorRelativeScale3D(ptr, value)
        }

    val actorScale: Vec3f
        get() = getActorScale(ptr)

    var actorScale3D: Vec3f
        get() = getActorScale3D(ptr)
        set(value) {
            setActorScale3D(ptr, value)
        }

    var actorTransform: Transform3f
        get() = getActorTransform(ptr)
        set(value) {
            setActorTransform(ptr, value)
        }

    val attachParentActor: AActor?
        get() = getAttachParentActor(ptr).wrapNullableUObject()

    /**
     * http://api.unrealengine.com/INT/API/Runtime/Engine/GameFramework/AActor/AttachToActor/index.html
     */
    fun attachToActor(parentActor: AActor, attachmentRules: FAttachmentTransformRules, socketName: String? = null) {
        attachToActor(ptr, parentActor.ptr, attachmentRules.toJunMagic(), socketName)
    }

    /**
     * http://api.unrealengine.com/INT/API/Runtime/Engine/GameFramework/AActor/AttachToComponent/index.html
     */
    fun attachToComponent(
            parent: USceneComponent,
            attachmentRules: FAttachmentTransformRules,
            socketName: String? = null
    ) {
        attachToComponent(ptr, parent.ptr, attachmentRules.toJunMagic(), socketName)
    }

    fun postInitializeComponents() {
        postInitializeComponents(ptr)
    }

    var rootComponent: USceneComponent?
        get() = getRootComponent(ptr).wrapNullableUObject()
        set(value) {
            setRootComponent(ptr, value.nullablePtr)
        }

    fun setActorLocationAndRotation(location: Vec3f, rotation: Quaternion) {
        setActorLocationAndRotation(ptr, location, rotation)
    }

    fun setActorRelativeLocation(relativeLocation: Vec3f) {
        setActorRelativeLocation(ptr, relativeLocation)
    }

    fun setActorRelativeRotation(relativeRotation: Quaternion) {
        setActorRelativeRotation(ptr, relativeRotation)
    }

    fun setActorRelativeTransform(relativeTransform: Transform3f) {
        setActorRelativeTransform(ptr, relativeTransform)
    }

    fun setActorRotation(rotation: Quaternion) {
        setActorRotation(ptr, rotation)
    }

    fun updateComponentTransforms() {
        updateComponentTransforms(ptr)
    }

    val transform: Transform3f
        get() = getTransform(ptr)

    val world: UWorld
        get() = getWorld(ptr).wrapUObject()
}

inline fun <reified T : AActor> spawnActor(
        world: UWorld = JunManager.GWorld,
        actorClass: UClass = staticClass<T>(),
        name: String? = null,
        transform: Transform3f? = null
): T = spawnActor(world.ptr, actorClass.ptr, name, transform).wrapUObject()

@Suppress("UNCHECKED_CAST")
fun <T : AActor> spawnActor(
        world: UWorld = JunManager.GWorld,
        actorClass: Class<T>,
        name: String? = null,
        transform: Transform3f? = null
) : T = spawnActor(
        world.ptr,
        (actorClass.kotlin.companionObjectInstance as UObjectCompanion).staticClass.ptr,
        name,
        transform
).wrapUObject<AActor>() as T

private external fun attachToActor(
        ptr: CPointer,
        parentPtr: CPointer,
        attachmentTransformRulesMagic: Int,
        socketName: String?
)

private external fun attachToComponent(
        ptr: CPointer,
        componentPtr: CPointer,
        attachmentTransformRulesMagic: Int,
        socketName: String?
)

private external fun getActorLocation(ptr: CPointer): Vec3f

private external fun getActorQuat(ptr: CPointer): Quaternion

private external fun getActorRelativeScale3D(ptr: CPointer): Vec3f

private external fun getActorScale(ptr: CPointer): Vec3f

private external fun getActorScale3D(ptr: CPointer): Vec3f

private external fun getActorTransform(ptr: CPointer): Transform3f

private external fun getAttachParentActor(ptr: CPointer): CPointer

private external fun getRootComponent(ptr: CPointer): CPointer

private external fun getTransform(ptr: CPointer): Transform3f

private external fun getWorld(ptr: CPointer): CPointer

private external fun postInitializeComponents(ptr: CPointer)

private external fun setActorLocation(ptr: CPointer, location: Vec3f)

private external fun setActorLocationAndRotation(ptr: CPointer, location: Vec3f, rotation: Quaternion)

private external fun setActorRelativeLocation(ptr: CPointer, relativeLocation: Vec3f)

private external fun setActorRelativeRotation(ptr: CPointer, relativeRotation: Quaternion)

private external fun setActorRelativeScale3D(ptr: CPointer, relativeScale: Vec3f)

private external fun setActorRelativeTransform(ptr: CPointer, relativeTransform: Transform3f)

private external fun setActorRotation(ptr: CPointer, rotation: Quaternion)

private external fun setActorScale3D(ptr: CPointer, scale: Vec3f)

private external fun setActorTransform(ptr: CPointer, transform: Transform3f)

private external fun setRootComponent(ptr: CPointer, componentPtr: CPointer)

@PublishedApi
internal external fun spawnActor(
        worldPtr: CPointer,
        classPtr: CPointer,
        name: String?,
        transform: Transform3f?
): CPointer

private external fun updateComponentTransforms(ptr: CPointer)