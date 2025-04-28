package com.cerebrallychallenged.hypogean.npc

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.nullActionInstance
import com.cerebrallychallenged.hypogean.model.action.skipActionInstance
import com.cerebrallychallenged.hypogean.model.base.inventory
import com.cerebrallychallenged.hypogean.model.containment.ContainerPosition
import com.cerebrallychallenged.hypogean.model.containment.Placeability
import com.cerebrallychallenged.hypogean.model.containment.findFreeContainerPosition
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor
import com.cerebrallychallenged.hypogean.model.maps.Entity2FloatMap
import com.cerebrallychallenged.hypogean.model.maps.Entity2ObjectMap
import com.cerebrallychallenged.hypogean.rays.sight
import com.cerebrallychallenged.hypogean.vanilla.actions.MoveAction
import com.cerebrallychallenged.hypogean.vanilla.actions.MoveActionInstance
import com.cerebrallychallenged.hypogean.vanilla.actions.findItemSwapAction
import com.cerebrallychallenged.hypogean.vanilla.actions.transitItem
import com.cerebrallychallenged.hypogean.vanilla.attributes.Health
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.behavior.ConsideredAction
import com.cerebrallychallenged.hypogean.vanilla.behavior.ConsideredAction.CombinedAction
import com.cerebrallychallenged.hypogean.vanilla.behavior.ConsideredAction.SimpleAction
import com.cerebrallychallenged.hypogean.vanilla.behavior.ConsideredAction.TakeCoverAction
import com.cerebrallychallenged.hypogean.vanilla.behavior.isUsefulTargetFor
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.rays.VisibilityExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovement
import com.cerebrallychallenged.hypogean.view.map.events.ShoutEvent
import com.cerebrallychallenged.hypogean.view.map.events.StandardShouts
import com.cerebrallychallenged.jun.log.log
import kotlin.random.Random
import kotlin.reflect.KMutableProperty1

suspend fun NpcContext.skipTurn() {
    submit(availableActions.skipActionInstance)
}

suspend fun NpcContext.warnAndSkipTurn(warning: String) {
    log.warn { warning }
    skipTurn()
}

suspend fun NpcContext.swapItems(first: Item, second: Item) {
    require(activeActor.transitItem == null)
    require(first.anchor == activeActor)
    require(second.anchor == activeActor)
    val firstContainerPosition = requireNotNull(first.containerPosition)
    val secondContainerPosition = requireNotNull(second.containerPosition)

    // Pick up first
    submit(availableActions.findItemSwapAction(firstContainerPosition))
    check(activeActor.transitItem == first)

    // Swap first with second.
    submit(availableActions.findItemSwapAction(secondContainerPosition))
    check(activeActor.transitItem == second)

    // Put second at former position of first.
    submit(availableActions.findItemSwapAction(firstContainerPosition))
    check(activeActor.transitItem == null)
}

private fun findFittingContainerPosition(item: Item, targetContainer: Item): ContainerPosition {
    targetContainer.findFreeContainerPosition()?.let { return it }
    val container = requireNotNull(item.container)
    val otherItem = requireNotNull(targetContainer.containedItems.firstOrNull { otherItem ->
        container.itemAcceptor.evaluatePlaceability(otherItem) == Placeability.Ok
    }) { "Did not find any item to swap places with" }
    return requireNotNull(otherItem.containerPosition)
}

/**
 * @return the item previously located at the targetContainerPosition, if any.
 */
suspend fun NpcContext.swapItemToContainer(item: Item, targetContainer: Item): Item? =
    swapItemToContainer(item, findFittingContainerPosition(item, targetContainer))

/**
 * @return the item previously located at the targetContainerPosition, if any.
 */
suspend fun NpcContext.swapItemToContainer(
    item: Item,
    targetContainerPosition: ContainerPosition
): Item? {
    require(activeActor.transitItem == null)
    require(item.anchor == activeActor || item.anchor is FactionEntity)
    require(targetContainerPosition.anchor == activeActor || targetContainerPosition.anchor is FactionEntity)

    val oldContainerposition = requireNotNull(item.containerPosition) {
        "item must initially be in a container"
    }

    // Pick up item
    submit(availableActions.findItemSwapAction(oldContainerposition))
    check(activeActor.transitItem == item)

    // Place item at target position
    submit(availableActions.findItemSwapAction(targetContainerPosition))

    // If there has been another item at the target position, it is put into the initial item container.
    val otherItem = activeActor.transitItem
    if (otherItem != null) {
        submit(availableActions.findItemSwapAction(oldContainerposition))
    }
    check(activeActor.transitItem == null)
    return otherItem
}

/**
 * Temporarily sets the specified attribute to the specified value, executes `block` and then resets the attribute
 * to its previous value.
 */
suspend fun <T, R> NpcContext.withModifiedAttribute(
    attribute: KMutableProperty1<in Actor, T>,
    value: T,
    block: suspend NpcContext.() -> R
): R {
    val prevValue = attribute.get(activeActor)
    attribute.set(activeActor, value)
    submit(availableActions.nullActionInstance)
    val result = block()
    attribute.set(activeActor, prevValue)
    submit(availableActions.nullActionInstance)
    return result
}

inline fun <reified T: Item> NpcContext.findNullableItemInInventory(predicate: (T) -> Boolean = { true }): T? =
    activeActor.faction?.inventory()?.containedItems?.filterIsInstance<T>()?.firstOrNull(predicate)

inline fun <reified T: Item> NpcContext.findItemInInventory(predicate: (T) -> Boolean = { true }): T =
    requireNotNull(findNullableItemInInventory(predicate)) { "Expected to have a ${T::class.simpleName}" }

fun NpcContext.shout(text: String) {
    if (ProtagonistFaction.reconOf(activeActor) == Recon.Visible) {
        world.notifyViewEvent(ShoutEvent(activeActor, text))
    }
}

fun NpcContext.maybeShoutStandard() {
    if (Random.nextFloat() < StandardShouts.TalkingProbability) {
        shout(StandardShouts[activeActor.faction.relationTo(ProtagonistFaction)].random())
    }
}

fun NpcContext.consideredActions(actionFilter: (Action) -> Boolean) : List<ConsideredAction> =
    buildList {
        val knownHostileActorsWithSight = hostileActors.asSequence()
            .filter { it.recon == Recon.Visible }
            .associateWithTo(Entity2ObjectMap(world)) {
                it.sight(VisibilityExtractor, GroundMovement(it), it.checkedLocation)
            }

        fun computeOwnExposure(ownLocation: Cell): Map<Actor, Float> {
            return knownHostileActorsWithSight.mapValuesTo(Entity2FloatMap(world)) { (_, hostileSightQuery) ->
                hostileSightQuery.of(ownLocation)?.exposure ?: 0.0f
            }
        }

        // Add actions from locations to which the active actor has to move first.
        for (move in availableActions.groupedByAction[MoveAction].instances.filter {
            it.initiativeCost == InitiativeCost.KeepTurn
        }) {
            val futureLocation = move.target as Cell
            val ownExposure = computeOwnExposure(futureLocation)

            // Add actions from the future location.
            val availableActions = world.actions.computeAvailableActions(activeActor, futureLocation, actionFilter)
            val actionsByTarget = availableActions.groupedByTarget

            for (hostileActor in hostileActors) {
                for (actionInstance in actionsByTarget[hostileActor].instances) {
                    add(CombinedAction(actionInstance, move, ownExposure))
                }
            }

            // Additionally, consider the case that no favorable action is available from the future location,
            // but the actor moves there just to take cover.
            add(TakeCoverAction(move, ownExposure))
        }

        // Add actions from the active actor's current location.
        val ownExposure = computeOwnExposure(activeActor.checkedLocation)
        for ((action, actions) in availableActions.groupedByAction) {
            if (actionFilter(action)) {
                for (actionInstance in actions.instances) {
                    add(SimpleAction(actionInstance, ownExposure))
                }
            }
        }
    }

fun NpcContext.gatherActions(consideredActions: List<ConsideredAction>): List<ConsideredAction> {
    val takeCoverActions = consideredActions.filterIsInstance<TakeCoverAction>()

    val combinedActions = consideredActions
        .filterIsInstance<CombinedAction>()
        .filter { it.action.target.isUsefulTargetFor(ownFaction) }

    val simpleActions = consideredActions
        .filterIsInstance<SimpleAction>()
        .filter { it.action.target.isUsefulTargetFor(ownFaction) }
    return takeCoverActions + combinedActions + simpleActions
}

fun NpcContext.evaluate(
    consideredAction: ConsideredAction,
    additionalConsiderations: NpcContext.() -> Float = { 0.0f }
): Float {
    val damageWeight = 10000.0f
    val killWeight = 50000.0f
    val ownExposureWeight = -10.0f
    val delayBeforeActionWeight = -1.0f
    var totalScore = 0.0f

    val attack = consideredAction.action
    val estimatedConsequences = attack.estimatedConsequences
    val actorsProbablyDestroyed = mutableListOf<Actor>()
    if (estimatedConsequences != null) {
        for (actor in locatedActors) {
            val factor = when (actor.factionRelation) {
                Faction.Relation.SAME, Faction.Relation.ALLIED -> -1.0f
                Faction.Relation.HOSTILE -> 1.0f
                Faction.Relation.NEUTRAL -> -0.1f
            }
            val actorHealth = actor.health

            val consequencesForActor = estimatedConsequences[actor]
            val damageConsequences = consequencesForActor[Health].flip()
            val expectedDamage = damageConsequences.expectedValue

            totalScore += factor * damageWeight * expectedDamage
            totalScore += factor * killWeight * damageConsequences.probabilityOfAtMost(actorHealth)
            if (expectedDamage >= actorHealth) {
                actorsProbablyDestroyed.add(actor)
            }
            // TODO: also consider energy and initiative consequences
        }
    }

    // Own exposure
    val ownExposure = consideredAction.ownExposure.maxOfOrNull { (actor, exposure) ->
        if (actor in actorsProbablyDestroyed) 0.0f else exposure
    } ?: 0.0f
    totalScore += ownExposureWeight * ownExposure

    totalScore += delayBeforeActionWeight * consideredAction.delayBeforeAction

    return totalScore + additionalConsiderations()
}