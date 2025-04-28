package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.action.ActionObstacle
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.ViewAction
import com.cerebrallychallenged.hypogean.model.action.ViewActionInstance
import com.cerebrallychallenged.hypogean.model.action.adjacentHittableLocations
import com.cerebrallychallenged.hypogean.model.base.props
import com.cerebrallychallenged.hypogean.model.maps.EntitySet
import com.cerebrallychallenged.hypogean.vanilla.props.InventoryProp
import com.cerebrallychallenged.hypogean.vanilla.rays.BallisticExtractor

object OpenInventoryAction : ViewAction() {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val world = activeActor.world
        with (activeActor.factionEntity as FactionContext) {
            val addedProps = EntitySet<Item>(world)
            for (cell in activeActor.adjacentHittableLocations(BallisticExtractor)) {
                for (prop in cell.props) {
                    if (prop is InventoryProp) {
                        addedProps.add(prop)
                        addInstance(OpenInventoryActionInstance(activeActor, prop))
                    }
                }
            }
            for (cell in world.cells) {
                for (prop in cell.props) {
                    if (prop is InventoryProp && prop.recon == Recon.Visible && prop !in addedProps) {
                        addObstacle(ActionObstacle(OpenInventoryAction, "Out of reach.", target = prop))
                    }
                }
            }
        }
    }
}

class OpenInventoryActionInstance(
    activeActor: Actor,
    override val target: Item
) : ViewActionInstance(OpenInventoryAction, activeActor)
