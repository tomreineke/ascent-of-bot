package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef
import com.cerebrallychallenged.jun.wrapUObject

open class APlayerController(ptr: CPointer) : AController(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun clientSetViewTarget(target: AActor) {
        clientSetViewTarget(ptr, target.ptr)
    }

    var currentMouseCursor: EMouseCursor
        get() = EMouseCursor.values()[getCurrentMouseCursor(ptr)]
        set(value) {
            setCurrentMouseCursor(ptr, value.ordinal)
        }

    data class Deprojected(val worldLocation: Vec3f, val worldDirection: Vec3f)

    fun deprojectMousePositionToWorld(): Deprojected?
            = deprojectMousePositionToWorld(ptr)?.let {
                (location, direction) -> Deprojected(location, direction)
            }

    @Convenience
    fun deprojectScreenPositionToWorld(screenPosition: Vec2f): Deprojected?
            = deprojectScreenPositionToWorld(screenPosition.x, screenPosition.y)

    fun deprojectScreenPositionToWorld(screenX: Float, screenY: Float): Deprojected?
            = deprojectScreenPositionToWorld(ptr, screenX, screenY)?.let {
                (location, direction) -> Deprojected(location, direction)
            }

    fun getHitResultAtScreenPosition(
            screenPosition: Vec2f,
            traceChannel: ECollisionChannel,
            traceComplex: Boolean
    ): TSharedRef<FHitResult> =
            getHitResultAtScreenPosition(ptr, screenPosition, traceChannel.ordinal, traceComplex).wrapSharedRef()

    fun getHitResultUnderCursor(traceChannel: ECollisionChannel, traceComplex: Boolean): TSharedRef<FHitResult> =
            getHitResultUnderCursor(ptr, traceChannel.ordinal, traceComplex).wrapSharedRef()

    val hud: AHUD
        get() = getHUD(ptr).wrapUObject()

    val localPlayer: ULocalPlayer
        get() = getLocalPlayer(ptr).wrapUObject()

    val viewportSize: Vec2i
        get() = getViewportSize(ptr)

    var enableClickEvents: Boolean
        get() = getEnableClickEvents(ptr)
        set(value) {
            setEnableClickEvents(ptr, value)
        }

    var enableMouseOverEvents: Boolean
        get() = getEnableMouseOverEvents(ptr)
        set(value) {
            setEnableMouseOverEvents(ptr, value)
        }

    var enableTouchEvents: Boolean
        get() = getEnableTouchEvents(ptr)
        set(value) {
            setEnableTouchEvents(ptr, value)
        }

    var enableTouchOverEvents: Boolean
        get() = getEnableTouchOverEvents(ptr)
        set(value) {
            setEnableTouchOverEvents(ptr, value)
        }

    fun projectWorldLocationToScreen(worldLocation: Vec3f, isPlayerViewportRelative: Boolean = false): Vec2f? =
        projectWorldLocationToScreen(ptr, worldLocation, isPlayerViewportRelative).takeUnless { it.x.isNaN() }

    fun setInputMode(inputMode: FInputMode) {
        when (inputMode) {
            is FInputModeGameAndUI -> setInputModeGameAndUi(
                    ptr,
                    inputMode.widgetToFocus.nullableSharedPtrPtr,
                    inputMode.mouseLockMode.ordinal.toByte(),
                    inputMode.hideCursorDuringCapture
            )
            is FInputModeGameOnly -> setInputModeGameOnly(ptr)
            is FInputModeUIOnly -> setInputModeUiOnly(
                    ptr,
                    inputMode.widgetToFocus.nullableSharedPtrPtr,
                    inputMode.mouseLockMode.ordinal.toByte()
            )
        }
    }

    var showMouseCursor: Boolean
        get() = getShowMouseCursor(ptr)
        set(value) {
            setShowMouseCursor(ptr, value)
        }
}

private external fun clientSetViewTarget(ptr: CPointer, targetPtr: CPointer)

private external fun deprojectMousePositionToWorld(ptr: CPointer): Pair<Vec3f, Vec3f>?

private external fun deprojectScreenPositionToWorld(ptr: CPointer, screenX: Float, screenY: Float): Pair<Vec3f, Vec3f>?

private external fun getCurrentMouseCursor(ptr: CPointer): Int

private external fun getEnableClickEvents(ptr: CPointer): Boolean

private external fun getEnableMouseOverEvents(ptr: CPointer): Boolean

private external fun getEnableTouchEvents(ptr: CPointer): Boolean

private external fun getEnableTouchOverEvents(ptr: CPointer): Boolean

private external fun getHitResultAtScreenPosition(
        ptr: CPointer,
        screenPosition: Vec2f,
        traceChannel: Int,
        traceComplex: Boolean
): CPointer

private external fun getHitResultUnderCursor(
        ptr: CPointer,
        traceChannel: Int,
        traceComplex: Boolean
): CPointer

private external fun getHUD(ptr: CPointer): CPointer

private external fun getLocalPlayer(ptr: CPointer): CPointer

private external fun getShowMouseCursor(ptr: CPointer): Boolean

private external fun getViewportSize(ptr: CPointer): Vec2i

private external fun projectWorldLocationToScreen(ptr: CPointer, worldLocation: Vec3f, isPlayerViewportRelative: Boolean): Vec2f

private external fun setCurrentMouseCursor(ptr: CPointer, mouseCursor: Int)

private external fun setEnableClickEvents(ptr: CPointer, enableClickEvents: Boolean)

private external fun setEnableMouseOverEvents(ptr: CPointer, enableMouseOverEvents: Boolean)

private external fun setEnableTouchEvents(ptr: CPointer, enableTouchEvents: Boolean)

private external fun setEnableTouchOverEvents(ptr: CPointer, enableTouchOverEvents: Boolean)

private external fun setInputModeGameAndUi(
        ptr: CPointer,
        widgetPtr: CPointer,
        mouseLockMode: Byte,
        hideCursorDuringCapture: Boolean
)

private external fun setInputModeGameOnly(ptr: CPointer)

private external fun setInputModeUiOnly(
        ptr: CPointer,
        widgetPtr: CPointer,
        mouseLockMode: Byte
)

private external fun setShowMouseCursor(ptr: CPointer, showMouseCursor: Boolean)
