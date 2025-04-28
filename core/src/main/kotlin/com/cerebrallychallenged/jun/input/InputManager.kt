package com.cerebrallychallenged.jun.input

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent
import com.cerebrallychallenged.jun.unreal.UWidgetLayoutLibrary
import com.cerebrallychallenged.jun.unreal.component

class InputManager: InputObservable {
    private val playerController = UGameplayStatics.getPlayerController(playerIndex = 0)

    override val inputListeners: MutableList<(InputEvent) -> Unit> = mutableListOf()

    var pixelOccluders: List<(position: Vec2i) -> Boolean> = listOf()

    private var mousePosition: Vec2i = Vec2i.ZERO

    private var hoveredComponent: UPrimitiveComponent? = null

    /**
     * Is `true` if the left mouse button has been pressed and the mouse has not moved since then.
     * Hence, a following release of the left mouse button can be seen as a left click.
     */
    private var canBeLeftClick = false

    /**
     * Is `true` if the right mouse button has been pressed and the mouse has not moved since then.
     * Hence, a following release of the right mouse button can be seen as a right click.
     */
    private var canBeRightClick = false

    fun isPixelOccluded(position: Vec2i): Boolean = pixelOccluders.any { it(position) }

    fun onTick() {
        updateMousePosition()
    }

    private fun updateMousePosition() {
        val newMousePosition = UWidgetLayoutLibrary.getMousePositionOnViewport().round()
        if (newMousePosition != mousePosition) {
            canBeLeftClick = false
            canBeRightClick = false
            mousePosition = newMousePosition
            val mouseEvent = MouseEvent(MouseEvent.Kind.MOVE, mousePosition, null, playerController)
            val newHoveredComponent = if (isPixelOccluded(mousePosition)) null else mouseEvent.hitResult?.component
            if (hoveredComponent != newHoveredComponent) {
                hoveredComponent?.notify(mouseEvent.cloneUnconsumed(MouseEvent.Kind.LEAVE))
                hoveredComponent = newHoveredComponent
                hoveredComponent?.notify(mouseEvent.cloneUnconsumed(MouseEvent.Kind.ENTER))
            }
            hoveredComponent?.notify(mouseEvent)
            notify(mouseEvent)
        }
    }

    internal fun onKeyPressed(key: Key) {
        updateMousePosition()
        if (key.isMouseButton) {
            if (isPixelOccluded(mousePosition)) {
                canBeLeftClick = false
                canBeRightClick = false
                return
            }
            val mouseEvent = MouseEvent(MouseEvent.Kind.PRESS, mousePosition, key, playerController)
            hoveredComponent?.notify(mouseEvent)
            notify(mouseEvent)
            when (key) {
                Key.LEFT_MOUSE_BUTTON -> canBeLeftClick = true
                Key.RIGHT_MOUSE_BUTTON -> canBeRightClick = true
            }
        } else {
            val keyEvent = KeyEvent(KeyEvent.Kind.PRESS, key)
            hoveredComponent?.notify(keyEvent)
            notify(keyEvent)
        }
    }

    internal fun onKeyReleased(key: Key) {
        updateMousePosition()
        if (key.isMouseButton) {
            val mouseEvent = MouseEvent(MouseEvent.Kind.RELEASE, mousePosition, key, playerController)
            hoveredComponent?.notify(mouseEvent)
            notify(mouseEvent)
            when (key) {
                Key.LEFT_MOUSE_BUTTON -> {
                    if (canBeLeftClick) {
                        val clickEvent = mouseEvent.cloneUnconsumed(MouseEvent.Kind.CLICK)
                        hoveredComponent?.notify(clickEvent)
                        notify(clickEvent)
                    }
                    canBeLeftClick = false
                }
                Key.RIGHT_MOUSE_BUTTON -> {
                    if (canBeRightClick) {
                        val clickEvent = mouseEvent.cloneUnconsumed(MouseEvent.Kind.CLICK)
                        hoveredComponent?.notify(clickEvent)
                        notify(clickEvent)
                    }
                    canBeRightClick = false
                }
            }
        } else {
            val keyEvent = KeyEvent(KeyEvent.Kind.RELEASE, key)
            hoveredComponent?.notify(keyEvent)
            notify(keyEvent)
        }
    }
}
