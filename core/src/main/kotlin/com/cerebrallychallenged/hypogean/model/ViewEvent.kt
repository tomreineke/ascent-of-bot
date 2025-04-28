package com.cerebrallychallenged.hypogean.model

/**
 * Perceptible events stand conceptually in the middle between model and view.
 * They are created by the model, but do not have any effect on the model.
 * They are only abstract descriptions, which are then interpreted by the view.
 *
 * For example, consider the action of moving an actor from A to B. The effect on the model is the update of a position
 * attribute.
 * Additionally, the view event `EntityMoveEvent` is created to animate the continuous movement of the 3d model.
 *
 * Another example is firing a gun. Effects on the model could be the removal of ammunition items, the reduction of the
 * health attribute of the target, the removal of the destroyed target actor from the world etc.
 * Generated view events are the bang, the flying bullet, and the particle effect of the explosion of the
 * target. The view interprets them by playing a sound file, animating the bullet, and displaying an explosion special
 * effect, respectively.
 */
interface ViewEvent {
    val duration: Float
        get() = 0.0f
}

abstract class Animation(private val duration: Float? = null) {
    /**
     * Time in seconds since the start of this `Animation`.
     */
    protected var time: Float = 0.0f
        private set

    /**
     * @param deltaTime time in seconds since the last tick.
     * @return if this has finished.
     */
    internal fun tick(deltaTime: Float): Boolean {
        time += deltaTime
        if (onTick(deltaTime) || duration != null && time >= duration) {
            onEnd()
            return true
        }
        return false
    }

    open fun onStart() {}

    /**
     * @param deltaTime time in seconds since the last tick.
     * @return if this animation should end immediately (independent of duration).
     */
    open fun onTick(deltaTime: Float): Boolean = false

    open fun onEnd() {}
}
