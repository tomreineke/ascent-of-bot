package com.cerebrallychallenged.hypogean.model.action

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

private const val MAGIC_KEEP_TURN = -1
private const val MAGIC_DESTROY = -2

internal fun DataOutput.writeInitiativeCost(initiativeCost: InitiativeCost) {
    writeInt(when (initiativeCost) {
        is InitiativeCost.Delta -> initiativeCost.rounds
        is InitiativeCost.KeepTurn -> MAGIC_KEEP_TURN
        is InitiativeCost.Destroy -> MAGIC_DESTROY
    })
}

@PublishedApi
internal fun DataInput.readInitiativeCost(): InitiativeCost {
    return when (val magic = readInt()) {
        MAGIC_KEEP_TURN -> InitiativeCost.KeepTurn
        MAGIC_DESTROY -> InitiativeCost.Destroy
        else -> if (magic >= 0) {
            InitiativeCost.Delta(magic)
        } else {
            throw IOException("Illegal magic number $magic for InitiativeCost")
        }
    }
}

sealed class InitiativeCost {
    abstract val rounds: Int

    data class Delta(override val rounds: Int) : InitiativeCost() {
        init {
            require(rounds >= 0)
        }
    }

    object KeepTurn : InitiativeCost() {
        override val rounds: Int
            get() = 0
    }

    object Destroy : InitiativeCost() {
        override val rounds: Int
            get() = 0
    }
}
