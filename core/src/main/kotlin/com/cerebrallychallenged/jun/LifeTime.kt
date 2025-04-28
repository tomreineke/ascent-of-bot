package com.cerebrallychallenged.jun

class LifeTime @PublishedApi internal constructor() {
    fun check() {
        require(isAlive) { "Lifetime exceeded" }
    }

    var isAlive: Boolean = true
        private set

    fun close() {
        isAlive = false
    }
}

class LifeTimeBlock {
    val lifeTime = LifeTime()
}

inline fun <R> ephemeral(block: LifeTimeBlock.() -> R): R {
    val lifeTimeBlock = LifeTimeBlock()
    try {
        return lifeTimeBlock.block()
    } finally {
        lifeTimeBlock.lifeTime.close()
    }
}
