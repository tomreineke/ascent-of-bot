package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionObstacle
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.adjacentHittableLocations
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.DropSlot
import com.cerebrallychallenged.hypogean.model.base.PROP_SLOT_NAME
import com.cerebrallychallenged.hypogean.model.base.dropSlot
import com.cerebrallychallenged.hypogean.model.base.inventory
import com.cerebrallychallenged.hypogean.model.base.propSlot
import com.cerebrallychallenged.hypogean.model.base.props
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.containment.ContainerPosition
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.containedItemAt
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.model.transformWhenDropped
import com.cerebrallychallenged.hypogean.vanilla.attributes.transform
import com.cerebrallychallenged.hypogean.vanilla.props.InventoryProp
import com.cerebrallychallenged.hypogean.vanilla.rays.BallisticExtractor
import com.cerebrallychallenged.jun.math.FLOAT_PI
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Quaternion.Companion.fromNormalAxisAngle
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.points
import kotlin.random.Random

var Actor.transitItem: Item? by attribute(null)

object ItemSwapAction : Action {
    private fun reachableCells(activeActor: Actor): Sequence<Cell> = sequence {
        activeActor.location?.let { yield(it) }
        yieldAll(activeActor.adjacentHittableLocations(BallisticExtractor))
    }

    private fun reachableContainers(activeActor: Actor): Sequence<Item> = sequence {
        for (cell in reachableCells(activeActor)) {
            yield(cell.propSlot)
            for (prop in cell.props) {
                if (prop is InventoryProp) {
                    yield(prop)
                }
            }
        }

        for (actor in activeActor.factionEntity?.actors ?: setOf(activeActor)) {
            yieldAll(actor.slots)
        }
        activeActor.factionEntity?.inventory()?.let { yield(it) }
        activeActor.factionEntity?.dropSlot()?.let { yield(it) }
    }

    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val transitItem = activeActor.transitItem
        for (container in reachableContainers(activeActor)) {
            if (transitItem == null) {
                for (containedItem in container.containedItems) {
                    addInstance(ItemSwapActionInstance(activeActor, requireNotNull(containedItem.containerPosition)))
                }
            } else {
                when (val placeability = container.itemAcceptor.evaluatePlaceability(transitItem)) {
                    is Placeability.Ok -> {
                        for (boxPosition in Bounds.byMinSize(Vec2i.ZERO, container.providedBoxes).points) {
                            addInstance(ItemSwapActionInstance(
                                activeActor,
                                ContainerPosition(container, boxPosition)
                            ))
                        }
                    }
                    is Placeability.Unplaceable -> {
                        for (obstacle in placeability.obstacles) {
                            addObstacle(ActionObstacle(ItemSwapAction, obstacle, target = container))
                        }
                    }
                }
            }
        }
    }

    override val category: ActionCategory
        get() = ActionCategory.ItemSwap
}

class ItemSwapActionInstance(
    activeActor: Actor,
    val containerPosition: ContainerPosition
) : ActionInstance(ItemSwapAction, activeActor) {
    override val target: Item
        get() = containerPosition.container

    val boxPosition: Vec2i
        get() = containerPosition.boxPosition

    override val equipment: Item
        get() = world.dummyEntity

    override val initiativeCost: InitiativeCost
        get() = InitiativeCost.KeepTurn

    context(CascadeContext)
    override suspend fun execute() {
        val transitItem = activeActor.transitItem
        if (containerPosition.container is DropSlot && transitItem != null) {
            transitItem.apply {
                // add random rotation on drop to make it less uniform; factor 2 to get interval [0; 2pi[
                val rnd = 2 * Random.nextFloat()
                val pos = activeActor.position
                transform = transformWhenDropped(pos.x, pos.y).withRotation(fromNormalAxisAngle(Vec3f.UNIT_Z, rnd * FLOAT_PI))
            }.let { activeActor.location?.slot(PROP_SLOT_NAME)?.insert(it) }
            activeActor.transitItem = null
        } else {
            val prevItem = target.containedItemAt(containerPosition.boxPosition)
            prevItem?.containerPosition = null
            if (transitItem != null) {
                transitItem.containerPosition = containerPosition
            }
            activeActor.transitItem = prevItem
        }
    }
}

fun ActionTable.findItemSwapAction(
    containerPosition: ContainerPosition
): ActionInstance = groupedByAction[ItemSwapAction].instances.firstOrNull { instance ->
    instance as ItemSwapActionInstance
    instance.target == containerPosition.container && instance.boxPosition == containerPosition.boxPosition
} ?: modelError("Could not find action for swapping transientItem into $containerPosition.container at $containerPosition.boxPosition")

fun ActionTable.itemSwapActions(container: Item): Map<Vec2i, ActionInstance> {
    val swapActions = groupedByAction[ItemSwapAction]
    val containerActions = swapActions.groupedByTarget[container]
    return containerActions.instances.associateBy {
        (it as ItemSwapActionInstance).boxPosition
    }
}

fun ActionTable.itemSwapObstacles(container: Item): Set<String> {
    val swapActions = groupedByAction[ItemSwapAction]
    val containerActions = swapActions.groupedByTarget[container]
    return containerActions.obstacleDescriptions
}
