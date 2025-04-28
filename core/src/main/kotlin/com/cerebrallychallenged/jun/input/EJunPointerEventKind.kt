package com.cerebrallychallenged.jun.input

enum class EJunPointerEventKind {
    ON_DRAG_DETECTED,
    ON_MOUSE_BUTTON_DOUBLE_CLICK,
    ON_MOUSE_BUTTON_DOWN,
    ON_MOUSE_BUTTON_UP,
    ON_MOUSE_ENTER,
    ON_MOUSE_LEAVE,
    ON_MOUSE_MOVE,
    ON_TOUCH_ENDED,
    ON_TOUCH_GESTURE,
    ON_TOUCH_MOVED,
    ON_TOUCH_STARTED;

    val isMouseButtonDown: Boolean
        get() = this == ON_MOUSE_BUTTON_DOWN || this == ON_MOUSE_BUTTON_DOUBLE_CLICK
}