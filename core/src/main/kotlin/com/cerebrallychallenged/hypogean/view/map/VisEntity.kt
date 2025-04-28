package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionMember
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.singleCategory
import com.cerebrallychallenged.hypogean.model.action.singleTarget
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.actionbar.createObstaclesTooltip
import com.cerebrallychallenged.hypogean.view.util.updateMaskedStencilValue
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent
import com.cerebrallychallenged.jun.unreal.USceneComponent

abstract class VisEntity<out T : Entity>(
    protected val mapView: MapView,
    val entity: T
) : FactionContext by mapView {
    protected val inputListener: (InputEvent) -> Unit = mapView.viewModel.createInputListenerFor(entity)

    abstract val rootComponent: USceneComponent

    open val node: CompositeNode?
        get() = null

    private var currentTooltip: Node? = null

    abstract suspend fun initialize()

    open fun dispose() {
        hideTooltip()
    }

    private fun hideTooltip() {
        currentTooltip?.let {
            it.detach()
            it.close()
        }
        currentTooltip = null
    }

    private val changeVisitor = object : WorldChange.SimpleSuspendVisitor {
        override suspend fun visit(change: WorldChange.ReconChanged) {
            if (change.reconByFaction.containsKey(ownFactionEntity)) {
                updateOptics()
            }
        }
    }

    open suspend fun onChange(change: WorldChange) {
        change.accept(changeVisitor)
    }

    open suspend fun onViewModelChange(change: ViewModelChange) {
        when (change) {
            is ActionInputStateChanged -> {
                hoveringActions = change.newState.hovered.takeIf {
                    it.hasInstances() && it.isSingleTargetFocused && it.singleTarget == entity
                }
                updateStencilValue()
                if (change.hasHoverChanged) {
                    hideTooltip()
                    val hovered = change.newState.hovered
                    if (
                        hovered.singleTarget == entity
                        && entity is LocatedEntity
                        && !hovered.hasInstances() && hovered.hasObstacles()
                    ) {
                        val screenPos = UGameplayStatics.getPlayerController(playerIndex = 0)
                            .projectWorldLocationToScreen(entity.centerPoint * 100)
                        if (screenPos != null) {
                            val pos = screenPos.round()
                            val radius = 10
                            currentTooltip = hovered.createObstaclesTooltip().show(
                                mapView.widget,
                                IRect(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius),
                                pos
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }

    val assetLibrary: AssetLibrary
        get() = mapView.assetLibrary

    val viewModel: ViewModel
        get() = mapView.viewModel

    var hoveringActions: ActionTable? = null
        private set

    var hidden: Boolean = false
        set(value) {
            field = value
            updateOptics()
        }

    open fun updateOptics() {}

    fun updateStencilValue() {
        val hoveringActions = hoveringActions
        val stencilValue = when {
            hoveringActions != null && hoveringActions.singleCategory == ActionCategory.Attack -> {
                mapView.postProcessOutlines.stencilValueFor(Faction.Relation.HOSTILE, true)
            }
            entity is FactionMember -> {
                mapView.postProcessOutlines.stencilValueFor(entity.factionRelation, hoveringActions != null)
            }
            hoveringActions != null -> {
                mapView.postProcessOutlines.stencilValueFor(Faction.Relation.NEUTRAL, true)
            }
            else -> 0
        }
        updateLoStencilValue(stencilValue)
    }

    protected abstract fun updateLoStencilValue(stencilValue: Int)

    /**
     * Updates the lowest 4 bit of the custom stencil value.
     */
    fun CompositeNode.walkLoStencilValue(loStencilValue: Int) {
        walkComponents<UPrimitiveComponent> {
            updateMaskedStencilValue(loStencilValue, 0b01111)
        }
    }

    open fun nodeForItem(item: Item): CompositeNode? = null
}
