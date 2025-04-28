package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.EntityType
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.model.type

@JvmInline
value class ActionMap<K>(val map: Map<K, ActionTable>) {
    operator fun get(key: K): ActionTable = map[key] ?: ActionTable.Empty

    val keys: Set<K>
        get() = map.keys

    val values: Collection<ActionTable>
        get() = map.values

    operator fun iterator(): Iterator<Map.Entry<K, ActionTable>> = map.iterator()
}

open class ActionTable internal constructor(
    val instances: List<ActionInstance>,
    val obstacles: List<ActionObstacle>,
    val isSingleTargetFocused: Boolean
) {
    companion object {
        val Empty = ActionTable(listOf(), listOf(), false)
    }

    private fun <T> groupBy(
            instanceProjection: ActionInstance.() -> T,
            obstacleProjection: ActionObstacle.() -> T,
            isSingleTargetFocused: Boolean = false
    ): ActionMap<T> {
        val groupedInstances = instances.groupBy(instanceProjection)
        val groupedObstacles = obstacles.groupBy(obstacleProjection)
        return ActionMap(groupedInstances.keys.union(groupedObstacles.keys).associateWith { key ->
            ActionTable(
                groupedInstances[key] ?: listOf(),
                groupedObstacles[key] ?: listOf(),
                isSingleTargetFocused
            )
        })
    }

    val groupedByAction: ActionMap<Action> by lazy { groupBy({ action }, { action }) }

    val groupedByCategory: ActionMap<ActionCategory> by lazy { groupBy({ action.category }, { action.category }) }

    val groupedByEquipment: ActionMap<Item?> by lazy { groupBy({ equipment }, { equipment }) }

    val groupedByTarget: ActionMap<Entity?> by lazy {
        groupBy({ target }, { target }, isSingleTargetFocused = true)
    }

    val groupedByMode: ActionMap<ActionMode?> by lazy { groupBy({ mode }, { mode }) }

    val obstacleDescriptions: Set<String> by lazy { obstacles.mapTo(mutableSetOf()) { it.description } }

    internal fun actionById(id: ActionInstanceId): ActionInstance {
        var parent = this
        for (index in id.prefixIndices) {
            parent = parent.instances[index].children
                ?: modelError("Partial action instance ${parent.instances[index]} has not been expanded.")
        }
        return parent.instances[id.index]
    }

    val activeActor: Actor?
        get() = instances.firstOrNull()?.activeActor

    fun hasInstances(): Boolean = instances.isNotEmpty()

    fun hasObstacles(): Boolean = obstacles.isNotEmpty()
}

val ActionTable.isSingleton: Boolean
    get() = instances.size == 1

val ActionTable.isIntransitive: Boolean
    get() = instances.all { it.target.isDummy }

val ActionTable.categories: Set<ActionCategory>
    get() = groupedByCategory.keys

val ActionTable.actions: Set<Action>
    get() = groupedByAction.keys

val ActionTable.equipments: Sequence<Item>
    get() = groupedByEquipment.keys.asSequence().filterNotNull()

val ActionTable.equipmentTypes: Sequence<EntityType<Item>>
    get() = equipments.map { it.type }.distinct()

val ActionTable.targets: Sequence<Entity>
    get() = groupedByTarget.keys.asSequence().filterNotNull()

val ActionTable.modes: Sequence<ActionMode>
    get() = groupedByMode.keys.asSequence().filterNotNull()

val ActionTable.initiativeCosts: Sequence<InitiativeCost>
    get() = instances.asSequence().map { it.initiativeCost }.distinct()

/**
 * Returns an [ActionCategory] that is the category of all instances in this table, or `null` if there are different
 * or no categories involved.
 */
val ActionTable.singleCategory: ActionCategory?
    get() = categories.singleOrNull()

val ActionTable.singleAction: Action?
    get() = actions.singleOrNull()

val ActionTable.singleTool: Item?
    get() = equipments.singleOrNull()

val ActionTable.singleToolType: EntityType<Item>?
    get() = equipmentTypes.singleOrNull()

val ActionTable.singleTarget: Entity?
    get() = targets.singleOrNull()

val ActionTable.singleMode: ActionMode?
    get() = modes.singleOrNull()

val ActionTable.singleInitiativeCost: InitiativeCost?
    get() = initiativeCosts.singleOrNull()

val ActionTable.maxIniDelta: Int
    get() = instances.maxOfOrNull { (it.initiativeCost as? InitiativeCost.Delta)?.rounds ?: 0 } ?: 0

class MutableActionTable private constructor(
        parentInstance: ActionInstance?,
        private val mutableInstances: MutableList<ActionInstance>,
        private val mutableObstacles: MutableList<ActionObstacle>
) : ActionTable(mutableInstances, mutableObstacles, false) {
    constructor(parentInstance: ActionInstance?) : this(parentInstance, mutableListOf(), mutableListOf())

    private val prefixIndices = parentInstance?.id?.toIndexList() ?: listOf()

    fun addInstance(actionInstance: ActionInstance) {
        actionInstance.id = ActionInstanceId(prefixIndices, mutableInstances.size)
        mutableInstances.add(actionInstance)
    }

    fun addInstances(actionInstances: Iterable<ActionInstance>) {
        actionInstances.forEach { addInstance(it) }
    }

    fun addObstacle(actionObstacle: ActionObstacle) {
        mutableObstacles.add(actionObstacle)
    }

    fun addObstacles(actionObstacles: Iterable<ActionObstacle>) {
        actionObstacles.forEach { addObstacle(it) }
    }
}
