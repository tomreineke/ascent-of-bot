package com.cerebrallychallenged.hypogean.activestate

import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.vanilla.attributes.energy

enum class GameOverReason {
    NoEnergy,
    NoHealth,
    Victory;

    companion object {
        context(World)
        fun determine(playerControlledFactions: List<Faction>): GameOverReason? = when {
            actors.none { playerControlledFactions.contains(it.faction) } -> NoHealth
            actors.filter { it.faction in playerControlledFactions }.sumOf { it.energy } == 0 -> NoEnergy
            else -> null
        }
    }
}

data class GameOverState(val reason: GameOverReason, val roundCount: Int) : ActiveWorldState()
