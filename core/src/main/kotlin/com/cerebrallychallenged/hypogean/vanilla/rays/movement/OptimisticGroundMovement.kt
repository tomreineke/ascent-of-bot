package com.cerebrallychallenged.hypogean.vanilla.rays.movement

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.base.PropPlacement
import com.cerebrallychallenged.hypogean.model.base.presentProps
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.pathfinding.SimpleActorMovementGraph
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor
import com.cerebrallychallenged.hypogean.rays.RayOrientation

abstract class OptimisticExtractor(val base: BlockerValueExtractor) : BlockerValueExtractor() {
    protected abstract fun isAprioriFree(cell: Cell, actingSubject: Any?): Boolean

    override fun cellValue(cell: Cell, actingSubject: Any?): Float =
            if (isAprioriFree(cell, actingSubject)) {
                0.0f
            } else {
                base.cellValue(cell, actingSubject)
            }

    override fun borderValue(
            cell: Cell,
            placement: PropPlacement,
            orientation: RayOrientation,
            heading: Heading,
            actingSubject: Any?
    ): Float = if (isAprioriFree(cell, actingSubject)) {
        0.0f
    } else {
        base.borderValue(cell, placement, orientation, heading, actingSubject)
    }
}

class OptimisticGroundMovementExtractor(
        actor: Actor,
        override val ownFactionEntity: FactionEntity =
                actor.factionEntity ?: modelError("Cannot use faction of $actor for recon, as it has none")
) : OptimisticExtractor(GroundMovementExtractor), FactionContext {
    override fun isAprioriFree(cell: Cell, actingSubject: Any?): Boolean =
            cell.recon == Recon.Unknown && cell.presentProps.all { it.recon == Recon.Unknown }
}

class OptimisticGroundMovement(
        override val actor: Actor,
        ownFactionEntity: FactionEntity =
                actor.factionEntity ?: modelError("Cannot use faction of $actor for recon, as it has none")
) : SimpleActorMovementGraph(actor, OptimisticGroundMovementExtractor(actor, ownFactionEntity))
