package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.ViewActionInstance
import com.cerebrallychallenged.hypogean.model.base.occupiedLocations
import com.cerebrallychallenged.jun.input.InputReason
import com.cerebrallychallenged.jun.math.geo.Vec2f

internal class HoverManager(private val viewModel: ViewModel) {

    /**
     * Hovered entity according to the GUI.
     */
    private var guiEntity: Entity? = null

    /**
     * Hovered entity according to Unreal.
     */
    private var unrealEntity: Entity? = null

    private var actualEntity: Entity? = null

    private var unrealActions: ActionTable = ActionTable.Empty

    private var guiActions: ActionTable = ActionTable.Empty

    private var actualActions: ActionTable = ActionTable.Empty

    fun remove(entity: Entity) {
        if (guiEntity == entity) {
            guiEntity = null
        }
        if (unrealEntity == entity) {
            unrealEntity = null
        }
        updateEntity()
    }

    fun clear() {
        guiEntity = null
        unrealEntity = null
        updateEntity()
    }

    fun updateEntity(mouseGroundPosition: Vec2f?, entity: Entity?, reason: InputReason) {
        when (reason) {
            InputReason.GUI -> {
                if (entity != guiEntity) {
                    guiEntity = entity
                    updateEntity()
                    updateEntityActions(entity, mouseGroundPosition, reason)
                }
            }
            InputReason.Unreal3D -> {
                if (entity != unrealEntity) {
                    unrealEntity = entity
                    updateEntity()
                    updateEntityActions(entity, mouseGroundPosition, reason)
                }
            }
        }
    }

    private fun updateEntity() {
        val newEntity = guiEntity ?: unrealEntity
        if (newEntity != actualEntity) {
            actualEntity = newEntity
            viewModel.sendChannel.trySend(EntityHovered(newEntity))
        }
    }

    private fun updateEntityActions(entity: Entity?, mouseGroundPosition: Vec2f?, reason: InputReason) {
        updateActions(
            viewModel.actionInputState.selected.findHoveredActions(mouseGroundPosition, entity, viewModel.isDebugMode),
            reason
        )
    }

    fun updateActions(actions: ActionTable, reason: InputReason) {
        when (reason) {
            InputReason.GUI -> {
                if (guiActions != actions) {
                    guiActions = actions
                    updateActions()
                }
            }
            InputReason.Unreal3D -> {
                if (unrealActions != actions) {
                    unrealActions = actions
                    updateActions()
                }
            }
        }
    }

    private fun updateActions() {
        val newActions = guiActions.takeIf { it.hasInstances() } ?: unrealActions
        if (newActions != actualActions) {
            actualActions = newActions
            viewModel.actionInputState = viewModel.actionInputState.copy(
                hovered = newActions
            )
        }
    }
}

private fun ActionTable.excludeDebugOnlyActions(isDebugMode: Boolean): ActionTable =
    if (instances.all { it is ViewActionInstance && it.isDebugOnly } && !isDebugMode) {
        ActionTable.Empty
    } else {
        this
    }

fun ActionTable.findHoveredActions(mouseGroundPosition: Vec2f?, entity: Entity?, isDebugMode: Boolean): ActionTable {
    if (!hasInstances()) {
        return ActionTable.Empty
    }
    val groupedByTarget = groupedByTarget
    // First try to find actions directly targeting the entity.
    val directlyTargeted = groupedByTarget[entity]
    if (directlyTargeted.hasInstances()) {
        return directlyTargeted.excludeDebugOnlyActions(isDebugMode)
    }
    // Otherwise, target the cell which is occupied by the entity and is closest to the mouse cursor.
    if (entity !is LocatedEntity || mouseGroundPosition == null) return ActionTable.Empty
    return groupedByTarget[entity.occupiedLocations.minByOrNull { it.position2f.distanceTo(mouseGroundPosition) }]
        .excludeDebugOnlyActions(isDebugMode)
}
