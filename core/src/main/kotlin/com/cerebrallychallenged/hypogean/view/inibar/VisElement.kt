package com.cerebrallychallenged.hypogean.view.inibar

import com.cerebrallychallenged.jun.skiatree.node.Node
import kotlin.math.max
import kotlin.math.min

abstract class VisElement(protected val iniBar: IniBarView) : Node() {
    protected var isNew: Boolean = true

    var isDeathCandidate: Boolean = false

    /**
     * @return if any property has changed.
     */
    abstract fun animate(deltaSeconds: Float): Boolean

    protected fun valueStep(currentValue: Float, targetValue: Float, speed: Float, deltaSeconds: Float): Float {
        return if (currentValue < targetValue) {
            min(currentValue + speed * deltaSeconds, targetValue)
        } else {
            max(currentValue - speed * deltaSeconds, targetValue)
        }
    }
}
