package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.view.input.InputCommand
import com.cerebrallychallenged.hypogean.view.util.mouse.MouseCursor
import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.input.Key
import com.cerebrallychallenged.jun.input.KeyEvent
import com.cerebrallychallenged.jun.input.MouseEvent
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.clamp
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import com.cerebrallychallenged.jun.unreal.camera.ACameraActor
import com.cerebrallychallenged.jun.unreal.spawnActor
import kotlin.math.exp

val LOOK_VECTOR = vec(1.0f, 1.0f, -2.0f).normalized()

private const val INITIAL_DIST_EXPONENT = 7.0f

private const val MIN_DIST_EXPONENT = 4.0f

private const val MAX_DIST_EXPONENT = 8.0f

private const val DIST_EXPONENT_STEP = 0.1f

private const val PANNING_SPEED = 500.0f

private val ROTATION_SPEED = Angle.DEGREE_90

private const val ZOOM_SPEED = 10.0f

object ZoomOutCommand : InputCommand("PageDown")
object ZoomInCommand : InputCommand("PageUp")
object PanLeftCommand : InputCommand("Left")
object PanLeftCommandSecondary : InputCommand("A")
object PanRightCommand : InputCommand("Right")
object PanRightCommandSecondary : InputCommand("D")
object PanUpCommand : InputCommand("Up")
object PanUpCommandSecondary : InputCommand("W")
object PanDownCommand : InputCommand("Down")
object PanDownCommandSecondary : InputCommand("S")
object RotateLeftCommand : InputCommand("Q")
object RotateRightCommand : InputCommand("E")

internal class ScrollManager {
    private val cameraActor = spawnActor<ACameraActor>().also {
        it.cameraComponent.constraintAspectRatio = false
        UGameplayStatics.getPlayerController(playerIndex = 0).clientSetViewTarget(it)
    }

    /**
     * [cameraLookAtPosition] or [distance] have changed so that the position of [cameraActor] must be updated.
     */
    private var cameraNeedsUpdate = true

    /**
     * Position of the mouse on the x-y-plane.
     */
    private var mouseGroundPosition: Vec2f? = null

    /**
     * Point on the ground where the camera looks at.
     */
    private var cameraLookAtPosition: Vec2f = Vec2f.ZERO

    /**
     * Exponent determining the distance of the camera from the ground.
     */
    private var distanceExponent: Float = Float.NaN

    /**
     * Distance of the camera from the ground.
     */
    private var distance: Float = Float.NaN

    /**
     * Current camera rotation.
     */
    private var cameraRotationAngle: Angle = Angle.DEGREE_0

    /**
     * Stores the [mouseGroundPosition] of the time when the mouse button was pressed to start panning.
     * During the panning, the [cameraLookAtPosition] is continuously updated such that the resulting
     * [mouseGroundPosition] matches that initial position.
     *
     * Is non-`null` only during the panning.
     */
    private var panningInitialGroundPosition: Vec2f? = null

    private val pressedCommands = mutableSetOf<InputCommand>()

    init {
        updateDistance(INITIAL_DIST_EXPONENT)
        updateCamera()
    }

    fun moveCameraToPosition(targetPosition: Vec2f) {
        cameraLookAtPosition = targetPosition * 100
        cameraNeedsUpdate = true
        updateCamera()
    }

    private fun updateMouseGroundPosition(mouseEvent: MouseEvent) {
        mouseGroundPosition = mouseEvent.deprojectedPosition?.let { (base, dir) ->
            // Trace a ray starting at `base` in the direction of `dir`. Where does it hit the ground?
            // We solve the equation `base + alpha * dir = (x, y, 0)`.
            val alpha = -base.z / dir.z
            if (alpha.isFinite() && alpha > 0.0f) {
                (base + dir * alpha).xy
            } else {
                null
            }
        }
    }

    private fun updateDistance(newDistanceExponent: Float) {
        distanceExponent = clamp(newDistanceExponent, MIN_DIST_EXPONENT, MAX_DIST_EXPONENT)
        distance = exp(distanceExponent)
        cameraNeedsUpdate = true
    }

    private fun updatePanning() {
        panningInitialGroundPosition?.let { initialPosition ->
            mouseGroundPosition?.let { currentPosition ->
                cameraLookAtPosition += initialPosition - currentPosition
                cameraNeedsUpdate = true
            }
        }
    }

    private fun zoom(direction: Float) {
        updateDistance(distanceExponent + direction * DIST_EXPONENT_STEP)
        updatePanning()
        updateCamera()
    }

    private fun pan(delta: Vec2f) {
        val lookVector = Quaternion.fromNormalAxisAngle(Vec3f.UNIT_Z, cameraRotationAngle) * LOOK_VECTOR
        cameraLookAtPosition += if (delta.x * delta.y < 0) { // left or right pan
            delta.pointwiseMul(lookVector.xy.yx)
        } else { // up or down pan
            delta.pointwiseMul(lookVector.xy)
        }

        cameraNeedsUpdate = true
        updateCamera()
    }

    private fun rotate(delta: Angle) {
        cameraRotationAngle += delta
        cameraNeedsUpdate = true
        updateCamera()
    }

    private fun updateCamera() {
        if (cameraNeedsUpdate) {
            val lookVector = Quaternion.fromNormalAxisAngle(Vec3f.UNIT_Z, cameraRotationAngle) * LOOK_VECTOR
            cameraActor.actorTransform = Transform3f(
                    lookVector.toLookAtWith(Vec3f.UNIT_Z),
                    cameraLookAtPosition.append(0.0f) - lookVector * distance,
                    Vec3f.ONE
            )
            cameraNeedsUpdate = false
        }
    }

    fun onTick(deltaSeconds: Float) {
        if (ZoomOutCommand in pressedCommands) {
            zoom(ZOOM_SPEED * deltaSeconds)
        }
        if (ZoomInCommand in pressedCommands) {
            zoom(-ZOOM_SPEED * deltaSeconds)
        }
        if (PanLeftCommand in pressedCommands || PanLeftCommandSecondary in pressedCommands) {
            pan(vec(PANNING_SPEED, -PANNING_SPEED) * deltaSeconds)
        }
        if (PanRightCommand in pressedCommands || PanRightCommandSecondary in pressedCommands) {
            pan(vec(-PANNING_SPEED, PANNING_SPEED) * deltaSeconds)
        }
        if (PanUpCommand in pressedCommands || PanUpCommandSecondary in pressedCommands) {
            pan(vec(PANNING_SPEED, PANNING_SPEED) * deltaSeconds)
        }
        if (PanDownCommand in pressedCommands || PanDownCommandSecondary in pressedCommands) {
            pan(vec(-PANNING_SPEED, -PANNING_SPEED) * deltaSeconds)
        }
        if (RotateLeftCommand in pressedCommands) {
            rotate(ROTATION_SPEED * deltaSeconds)
        }
        if (RotateRightCommand in pressedCommands) {
            rotate(-ROTATION_SPEED * deltaSeconds)
        }
    }

    fun onInput(inputEvent: InputEvent, commands: Collection<InputCommand>) {
//        LOGGER.info { "INPUT $inputEvent" }
        when (inputEvent) {
            is KeyEvent -> {
                onKeyInput(inputEvent, commands)
            }
            is MouseEvent -> {
                onMouseInput(inputEvent, commands)
            }
        }
    }

    private fun onKeyInput(keyEvent: KeyEvent, commands: Collection<InputCommand>) {
        val key = keyEvent.key
        when {
            key.isMouseWheel -> {
                zoom(if (key == Key.MOUSE_SCROLL_UP) 1.0f else -1.0f)
            }
            keyEvent.kind == KeyEvent.Kind.PRESS -> {
                pressedCommands.addAll(commands)
            }
            keyEvent.kind == KeyEvent.Kind.RELEASE -> {
                pressedCommands.removeAll(commands.toSet())
            }
        }
    }



    private fun onMouseInput(mouseEvent: MouseEvent, commands: Collection<InputCommand>) {
        updateMouseGroundPosition(mouseEvent)
        when (mouseEvent.button) {
            Key.RIGHT_MOUSE_BUTTON -> {
                when (mouseEvent.kind) {
                    MouseEvent.Kind.PRESS -> {
                        panningInitialGroundPosition = mouseGroundPosition
                        MouseCursor.isPanning = true
                    }
                    MouseEvent.Kind.RELEASE -> {
                        updatePanning()
                        panningInitialGroundPosition = null
                        updateCamera()
                        MouseCursor.isPanning = false
                    }
                    else -> {}
                }
            }
            null -> {
                if (mouseEvent.kind == MouseEvent.Kind.MOVE && panningInitialGroundPosition != null) {
                    updatePanning()
                    updateCamera()
                }
            }
        }
    }

    suspend fun onChange(change: WorldChange) {

    }
}
