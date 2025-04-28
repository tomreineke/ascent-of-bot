package com.cerebrallychallenged.hypogean.vanilla.behavior

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.action.nearCells
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.base.presentProps
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.npc.Behavior
import com.cerebrallychallenged.hypogean.npc.NpcContext
import com.cerebrallychallenged.hypogean.npc.skipTurn
import com.cerebrallychallenged.hypogean.pathfinding.NO_EDGE
import com.cerebrallychallenged.hypogean.pathfinding.SimpleActorMovementGraph
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.props.LandMine
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.AvoidingExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovementExtractor
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.log.log

class MineAvoidingGroundMovementExtractor(
        override val ownFactionEntity: FactionEntity
) : AvoidingExtractor(GroundMovementExtractor), FactionContext {
    override fun avoidance(cell: Cell, actingSubject: Any?): Float =
            if (cell.presentProps.any { it is LandMine && it.recon != Recon.Unknown }) NO_EDGE else 0.0f
}

class MineAvoidingGroundMovement(
        activeActor: Actor
) : SimpleActorMovementGraph(
        activeActor,
        MineAvoidingGroundMovementExtractor(
                activeActor.factionEntity ?: modelError("Actor without faction cannot avoid mines")
        )
)

object CheckPointSeekingBehavior : Behavior() {
    override suspend fun NpcContext.run() {
        val nearestTarget = activeActor
                .nearCells(20.0f)
                .filter { cell -> cell.presentProps.any { it is CheckPointMarker } }
                .minByOrNull { it.position.distanceTo(activeActor.position) }
        if (nearestTarget == null) {
            skipTurn()
        } else {
            val world = activeActor.world
            val path = world
                .shortestPath(MineAvoidingGroundMovement(activeActor))
                .from(activeActor.checkedLocation).to(nearestTarget)
            if (path == null) {
                skipTurn()
            } else {
                // TODO submit MoveActionInstance for path
                log.warn { "PATH $path" }
            }
        }
    }
}

object CheckPointAsset : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(IndustryPropsPack6.SM_TrafficBarrel01)
    }
})

class CheckPointMarker(initializer: Initializer) : Prop(initializer) {
    init {
        asset = CheckPointAsset
    }
}
