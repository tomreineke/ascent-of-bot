package com.cerebrallychallenged.hypogean.activestate

import com.cerebrallychallenged.hypogean.model.World

abstract class ActiveWorldState {
    /**
     * Is called from [World] when this is set as the [World.activeState].
     */
    open fun activate() {}
}
