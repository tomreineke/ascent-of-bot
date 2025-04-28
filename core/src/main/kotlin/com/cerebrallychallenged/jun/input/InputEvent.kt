package com.cerebrallychallenged.jun.input

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.unreal.APlayerController
import com.cerebrallychallenged.jun.unreal.ECollisionChannel
import com.cerebrallychallenged.jun.unreal.FHitResult
import com.cerebrallychallenged.jun.unreal.TSharedPtr

interface InputObservable {
    val inputListeners: MutableList<(InputEvent) -> Unit>
}

fun InputObservable.notify(inputEvent: InputEvent) {
    for (listener in inputListeners) {
        listener(inputEvent)
    }
}

sealed class InputEvent {
    var isConsumed: Boolean = false
        private set

    fun consume() {
        isConsumed = true
    }
}

class MouseEvent private constructor(
    val kind: Kind,
    val position: Vec2i,
    val button: Key?,
    val hitResult: TSharedPtr<FHitResult>,
    val deprojectedPosition: APlayerController.Deprojected?
) : InputEvent() {
    enum class Kind {
        ENTER,
        LEAVE,
        MOVE,
        PRESS,
        RELEASE,
        CLICK
    }

    companion object {
        operator fun invoke(kind: Kind, position: Vec2i, button: Key?, controller: APlayerController) = MouseEvent(
                kind,
                position,
                button,
                controller.getHitResultAtScreenPosition(
                        position.toFloat(),
                        ECollisionChannel.WorldDynamic,
                        false
                ),
                controller.deprojectMousePositionToWorld()
        )

        operator fun invoke(kind: Kind, position: Vec2i, button: Key?) = MouseEvent(
                kind,
                position,
                button,
                null,
                null
        )
    }

    fun cloneUnconsumed(newKind: Kind): MouseEvent
            = MouseEvent(newKind, position, button, hitResult, deprojectedPosition)

    override fun toString(): String = "MouseEvent($kind, $position, $button)"
}

class KeyEvent(val kind: Kind, val key: Key) : InputEvent() {
    enum class Kind {
        PRESS,
        RELEASE
    }

    override fun toString(): String = "KeyEvent($kind, $key)"
}
