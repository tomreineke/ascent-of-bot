package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.adjacentHittableLocations
import com.cerebrallychallenged.hypogean.model.base.inventory
import com.cerebrallychallenged.hypogean.model.base.props
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.vanilla.props.InventoryProp
import com.cerebrallychallenged.hypogean.vanilla.rays.BallisticExtractor


object CollectAllItemsAction : Action {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val inventory = (activeActor.factionEntity ?: return).inventory()
        val freeSpace = inventory.providedBoxes.x * inventory.providedBoxes.y - inventory.containedItems.size
        for (cell in activeActor.adjacentHittableLocations(BallisticExtractor)) {
            for (prop in cell.props) {
                if (prop is InventoryProp) {
                    addInstance(CollectAllItemsActionInstance(
                        activeActor,
                        prop,
                        prop.containedItems.filter {
                            inventory.itemAcceptor.evaluatePlaceability(it) == Placeability.Ok
                        }.take(freeSpace)
                    ))
                }
            }
        }
    }

    override val category: ActionCategory
        get() = ActionCategory.ItemSwap
}

class CollectAllItemsActionInstance(
        activeActor: Actor,
        override val target: Item,
        private val pickupItems: List<Item>
) : ActionInstance(CollectAllItemsAction, activeActor) {
    override val equipment: Item
        get() = world.dummyEntity

    override val initiativeCost: InitiativeCost
        get() = InitiativeCost.KeepTurn

    context(CascadeContext)
    override suspend fun execute() {
        val inventory = (activeActor.factionEntity ?: return).inventory()
        for (item in pickupItems) {
            inventory.insert(item)
        }
    }
}
