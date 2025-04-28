package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class USceneComponent(ptr: CPointer) : UActorComponent(ptr), SceneComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    val attachParent: USceneComponent?
        get() = getAttachParent(ptr).wrapNullableUObject()

    fun attachToComponent(
            parent: USceneComponent,
            attachmentRules: FAttachmentTransformRules,
            socketName: String? = null
    ) {
        attachToComponent(ptr, parent.ptr, attachmentRules.toJunMagic(), socketName)
    }

    fun detachFromComponent(detachmentRules: FDetachmentTransformRules) {
        detachFromComponent(ptr, detachmentRules.toJunMagic())
    }

    final override val componentLocation: Vec3f
        get() = getComponentLocation(ptr)

    final override val componentQuat: Quaternion
        get() = getComponentQuat(ptr)

    final override val componentScale: Vec3f
        get() = getComponentScale(ptr)

    final override val componentTransform: Transform3f
        get() = getComponentTransform(ptr)

    fun doesSocketExist(socketName: String): Boolean = doesSocketExist(ptr, socketName)

    fun getSocketLocation(socketName: String): Vec3f = getSocketLocation(ptr, socketName)

    fun getSocketQuaternion(socketName: String): Quaternion = getSocketQuaternion(ptr, socketName)

    fun getSocketTransform(socketName: String): Transform3f = getSocketTransform(ptr, socketName)

    final override var mobility: EComponentMobility
        get() = EComponentMobility.values()[getMobility(ptr)]
        set(value) {
            setMobility(ptr, value.ordinal)
        }

    final override var relativeLocation: Vec3f
        get() = getRelativeLocation(ptr)
        set(value) {
            setRelativeLocation(ptr, value)
        }

    final override var relativeRotation: Quaternion
        get() = getRelativeRotation(ptr)
        set(value) {
            setRelativeRotation(ptr, value)
        }

    final override var relativeScale3D: Vec3f
        get() = getRelativeScale3D(ptr)
        set(value) {
            setRelativeScale3D(ptr, value)
        }

    final override var relativeTransform: Transform3f
        get() = getRelativeTransform(ptr)
        set(value) {
            setRelativeTransform(ptr, value)
        }

    final override fun setRelativeLocationAndRotation(newLocation: Vec3f, newRotation: Quaternion) {
        setRelativeLocationAndRotation(ptr, newLocation, newRotation)
    }

    final override fun setWorldLocationAndRotation(newLocation: Vec3f, newRotation: Quaternion) {
        setWorldLocationAndRotation(ptr, newLocation, newRotation)
    }

    final override var visibility: Boolean
        get() = getVisibility(ptr)
        set(value) {
            setVisibility(ptr, value, false)
        }

    final override fun setVisibility(visibility: Boolean, propagateToChildren: Boolean) {
        setVisibility(ptr, visibility, propagateToChildren)
    }

    final override var worldLocation: Vec3f
        get() = componentLocation
        set(value) {
            setWorldLocation(ptr, value)
        }

    final override var worldRotation: Quaternion
        get() = componentQuat
        set(value) {
            setWorldRotation(ptr, value)
        }

    final override var worldScale3D: Vec3f
        get() = componentScale
        set(value) {
            setWorldScale3D(ptr, value)
        }

    final override var worldTransform: Transform3f
        get() = componentTransform
        set(value) {
            setWorldTransform(ptr, value)
        }
}

private external fun attachToComponent(
        ptr: CPointer,
        parentPtr: CPointer,
        attachmentRulesMagic: Int,
        socketName: String?
)

private external fun detachFromComponent(ptr: CPointer, detachmentRulesMagic: Int)

private external fun doesSocketExist(ptr: CPointer, socketName: String): Boolean

private external fun getAttachParent(ptr: CPointer): CPointer

private external fun getComponentLocation(ptr: CPointer): Vec3f

private external fun getComponentQuat(ptr: CPointer): Quaternion

private external fun getComponentScale(ptr: CPointer): Vec3f

private external fun getComponentTransform(ptr: CPointer): Transform3f

private external fun getMobility(ptr: CPointer): Int

private external fun getRelativeLocation(ptr: CPointer): Vec3f

private external fun getRelativeRotation(ptr: CPointer): Quaternion

private external fun getRelativeScale3D(ptr: CPointer): Vec3f

private external fun getRelativeTransform(ptr: CPointer): Transform3f

private external fun getSocketLocation(ptr: CPointer, socketName: String): Vec3f

private external fun getSocketQuaternion(ptr: CPointer, socketName: String): Quaternion

private external fun getSocketTransform(ptr: CPointer, socketName: String): Transform3f

private external fun getVisibility(ptr: CPointer): Boolean

private external fun setMobility(ptr: CPointer, newMobility: Int)

private external fun setRelativeLocation(ptr: CPointer, newLocation: Vec3f)

private external fun setRelativeLocationAndRotation(ptr: CPointer, newLocation: Vec3f, newRotation: Quaternion)

private external fun setRelativeRotation(ptr: CPointer, newRotation: Quaternion)

private external fun setRelativeScale3D(ptr: CPointer, newScale3D: Vec3f)

private external fun setRelativeTransform(ptr: CPointer, newTransform: Transform3f)

private external fun setVisibility(ptr: CPointer, newVisibility: Boolean, propagateToChildren: Boolean)

private external fun setWorldLocation(ptr: CPointer, newLocation: Vec3f)

private external fun setWorldLocationAndRotation(ptr: CPointer, newLocation: Vec3f, newRotation: Quaternion)

private external fun setWorldRotation(ptr: CPointer, newRotation: Quaternion)

private external fun setWorldScale3D(ptr: CPointer, newScale3D: Vec3f)

private external fun setWorldTransform(ptr: CPointer, newTransform: Transform3f)
