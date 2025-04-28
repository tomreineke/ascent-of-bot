package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Animation
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.actions
import com.cerebrallychallenged.hypogean.model.action.equipments
import com.cerebrallychallenged.hypogean.model.action.targets
import com.cerebrallychallenged.hypogean.model.base.PropSlot
import com.cerebrallychallenged.hypogean.model.isAlive
import com.cerebrallychallenged.hypogean.rays.HitResult
import com.cerebrallychallenged.hypogean.util.ConstraintsMap
import com.cerebrallychallenged.hypogean.util.MutableConstraintsMap
import com.cerebrallychallenged.hypogean.vanilla.ActionWithAccuracy
import com.cerebrallychallenged.hypogean.vanilla.actions.BasicShotActionInstance.Companion.computeAttackSituation
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.view.ActionInputState
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.hypogean.view.Asset_CellFloor
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.input.InputCommand
import com.cerebrallychallenged.hypogean.view.map.events.MapViewEvent
import com.cerebrallychallenged.hypogean.view.map.visualizers.ActionVisualizer
import com.cerebrallychallenged.hypogean.view.map.visualizers.ActionVisualizerFactory
import com.cerebrallychallenged.hypogean.view.map.visualizers.IdleVisualizer
import com.cerebrallychallenged.hypogean.view.map.voxel.VoxelManager
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.skiatree.SkiaTreeWidget
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceDynamic
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

interface MapViewContext : WorldContext, FactionContext {
    val assetLibrary: AssetLibrary

    val viewModel: ViewModel

    val visMap: VisMap

    val widget: SkiaTreeWidget

    fun addAnimation(animation: Animation) {
        viewModel.addAnimation(animation)
    }
}

class MapView private constructor(
    context: ViewFactory.Context,
    internal val postProcessOutlines: PostProcessOutlines,
    private val actionVisualizers: ConstraintsMap<ActionTable, ActionVisualizerFactory>,
    val factionRelationMaterials: Map<Faction.Relation, UMaterialInstanceDynamic>,
) : View, MapViewContext, WorldContext by context, FactionContext by context {
    class Factory : ViewFactory {
        val actionVisualizers = MutableConstraintsMap<ActionTable, ActionVisualizerFactory> { IdleVisualizer.Factory }

        override suspend fun preConfigure(context: ViewFactory.Context) {
        }

        override suspend fun create(context: ViewFactory.Context): View {
            val assetLibrary = context.assetLibrary
            val indicatorMaterial = assetLibrary.load(Hypogean.M_IndicatorMaterial)
            val factionRelationMaterials =
                EnumMap<Faction.Relation, UMaterialInstanceDynamic>(Faction.Relation::class.java)
            for ((relation, color) in GuiConfig.ColorByFactionRelation) {
                if (relation != null) {
                    factionRelationMaterials[relation] = UMaterialInstanceDynamic.create(indicatorMaterial).apply {
                        parameters["Color"] = color * 0.5f
                    }
                }
            }
            val hintColor = GuiConfig.HintColor
            val postProcessOutlines = PostProcessOutlines(assetLibrary, GuiConfig.ColorByFactionRelation, hintColor)
            return MapView(
                context,
                postProcessOutlines,
                actionVisualizers,
                factionRelationMaterials,
            ).apply {
                activeActorIndicator = ActiveActorIndicator(this)
                hintIndicator = HintIndicator(this, hintColor)
            }
        }
    }

    override val assetLibrary = context.assetLibrary

    override val viewModel = context.viewModel

    override val widget = context.widget

    override val visMap: VisMap = VisMap(world)

    private val scrollManager = ScrollManager()

    internal val voxelManager = VoxelManager(context.assetLibrary)

    private lateinit var activeActorIndicator: ActiveActorIndicator

    private lateinit var hintIndicator: HintIndicator

    private var currentActionVisualizer: ActionVisualizer? = null

    private var hitProbabilityTooltip: Node? = null

    private val changeVisitor = object : WorldChange.SimpleSuspendVisitor {
        private suspend fun dispatch(change: WorldChange, entity: Entity?) {
            if (entity != null) {
                visMap[entity]?.onChange(change)
                if (entity is StatusEffect) {
                    val bearer = entity.bearer
                    visMap[bearer]?.onChange(change)
                    if (bearer is Item) {
                        val anchor = bearer.anchor
                        if (anchor != null) {
                            visMap[anchor]?.onChange(change)
                        }
                    }
                }
            }
        }

        override suspend fun visit(change: WorldChange.Clear) {
            visMap.disposeAndClear()
            visMap.addAndInitialize(VisWorld(this@MapView, world))
        }

        override suspend fun visit(change: WorldChange.Removed) {
            val (entity) = change
            dispatch(change, entity)
            visMap.removeAndDispose(entity)
        }

        override suspend fun visit(change: WorldChange.CellCreated) {
            val (cell) = change
            visMap.addAndInitialize(VisCell(this@MapView, cell, Asset_CellFloor.create(assetLibrary)))
        }

        override suspend fun visit(change: WorldChange.ActorCreated) {
            val (actor) = change
            if (actor.isAlive()) {
                actor.asset?.create(this@MapView, actor)?.let { visActor ->
                    visMap.addAndInitialize(visActor)
                }
            }
        }

        override suspend fun visit(change: WorldChange.StatusEffectCreated) {
            dispatch(change, change.entity)
        }

        override suspend fun visit(change: WorldChange.ReconChanged) {
            dispatch(change, change.entity)
        }

        override suspend fun visit(change: WorldChange.ItemMove) {
            val (item, oldContainerPosition, newContainerPosition) = change
            if (oldContainerPosition != null) {
                val oldContainer = oldContainerPosition.container
                if (oldContainer is PropSlot) {
                    visMap.removeAndDispose(item)
                } else {
                    dispatch(change, oldContainer.anchor)
                }
            }
            if (newContainerPosition != null) {
                val newContainer = newContainerPosition.container
                if (newContainer is PropSlot) {
                    visMap.addAndInitialize(VisProp(this@MapView, item))
                } else {
                    dispatch(change, newContainer.anchor)
                }
            }
        }

        override suspend fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            val (entity, _, _, _) = change
            dispatch(change, entity)
            change.ifOf(Actor::asset) { (actor, _, actorAsset) ->
                visMap.removeAndDispose(actor)
                actorAsset?.create(this@MapView, actor as Actor)?.let { visActor ->
                    visMap.addAndInitialize(visActor)
                }
            }
            change.ifOf(FactionEntity::relations) {
                for (actor in (entity as FactionEntity).actors) {
                    visMap[actor]?.onChange(change)
                }
            }
        }

        override suspend fun visit(change: WorldChange.FactionMembershipChanged) {
            dispatch(change, change.factionMember)
        }

        override suspend fun visit(change: WorldChange.ActiveStateChanged) {
            val activeState = change.activeState
            if (activeState is ActiveActorState) {
                val activeActor = activeState.activeActor
                if (ProtagonistFaction.reconOf(activeActor) == Recon.Visible) {
                    scrollManager.moveCameraToPosition(activeActor.position2f)
                }
            }
            activeActorIndicator.onChange(change)
        }

        override suspend fun visit(change: WorldChange.ViewEventHappened) {
            val (viewEvent) = change
            if (viewEvent is MapViewEvent) {
                viewEvent.execute()
            }
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        when (change) {
            is ModelChange -> {
                for (worldChange in change.changes) {
                    worldChange.accept(changeVisitor)
                    activeActorIndicator.onChange(worldChange)
                    hintIndicator.onChange(worldChange)
                    scrollManager.onChange(worldChange)
                }
                voxelManager.processDirtyVoxels()
            }
            is ActionInputStateChanged -> {
                val (prevState, nextState) = change
                if (change.hasSelectionChanged) {
                    currentActionVisualizer?.dispose()
                    currentActionVisualizer = actionVisualizers[nextState.selected].create(assetLibrary, nextState)
                } else {
                    currentActionVisualizer?.update(change)
                }
                val affectedTargets = mutableSetOf<Entity>()
                if (change.hasSelectionChanged) {
                    affectedTargets += prevState.selected.targets
                    affectedTargets += nextState.selected.targets
                }
                if (change.hasHoverChanged) {
                    affectedTargets += prevState.hovered.targets
                    affectedTargets += nextState.hovered.targets
                    updateHitProbabilityTooltip(nextState)
                }
                for (target in affectedTargets) {
                    visMap[target]?.onViewModelChange(change)
                }
            }
            else -> {}
        }
        activeActorIndicator.onViewModelChange(change)
    }

    override fun onTick(deltaSeconds: Float) {
        activeActorIndicator.onTick(deltaSeconds)
        hintIndicator.onTick(deltaSeconds)
        scrollManager.onTick(deltaSeconds)
    }

    override fun onInput(inputEvent: InputEvent, commands: Collection<InputCommand>) {
        scrollManager.onInput(inputEvent, commands)
    }

    private fun updateHitProbabilityTooltip(state: ActionInputState) {
        hitProbabilityTooltip?.let {
            widget.layers[GuiLayer.Tooltip].children.remove(it)
        }

        val weapon = state.hovered.equipments.filterIsInstance<Weapon>().firstOrNull()
        val action = state.hovered.actions.filterIsInstance<ActionWithAccuracy>().firstOrNull()
        val activeActor = state.hovered.activeActor
        val activeActorLocation = activeActor?.checkedLocation
        val target = state.hovered.targets.filterIsInstance<LocatedEntity>().firstOrNull()

        if (activeActor == null || activeActorLocation == null || target == null || weapon == null || action == null
            || target.position in activeActor.occupiedPositions(activeActorLocation)
        ) {
            return
        }

        val situation = computeAttackSituation(activeActor, activeActorLocation, target, action, weapon)

        val random = world.random
        var hits = 0
        repeat(100) {
            val hitResult = situation.sampleHit(random)
            if (hitResult is HitResult.Hit && target in hitResult.hitEntities
                || hitResult is HitResult.EndOfRange && target.position == hitResult.position
            ) {
                ++hits
            }
        }

        val screenPos = UGameplayStatics.getPlayerController(playerIndex = 0)
            .projectWorldLocationToScreen(target.centerPoint * 100)
        if (screenPos != null) {
            val pos = screenPos.round()
            val radius = 10
            hitProbabilityTooltip = Tooltip {
                +"chance to hit: $hits%"
            }.show(
                this.widget,
                IRect(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius),
                pos
            )
        }
    }
}
