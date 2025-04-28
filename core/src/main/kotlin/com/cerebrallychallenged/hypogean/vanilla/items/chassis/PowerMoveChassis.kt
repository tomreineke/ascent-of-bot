package com.cerebrallychallenged.hypogean.vanilla.items.chassis

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.vanilla.attributes.remainingUseCount

class PowerMoveChassis(initializer: Initializer) : IndestructibleChassis(initializer) {
    init {
        moveRange = 30
        quickMoveRange = 30
        remainingUseCount = 1
    }
}
