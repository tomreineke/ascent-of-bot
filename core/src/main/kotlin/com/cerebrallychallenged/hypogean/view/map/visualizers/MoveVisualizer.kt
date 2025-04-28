package com.cerebrallychallenged.hypogean.view.map.visualizers

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.action.ActionInstanceCompleteness
import com.cerebrallychallenged.hypogean.model.action.compatibleInstances
import com.cerebrallychallenged.hypogean.model.action.singleTarget
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.size
import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.vanilla.actions.ActionInstanceWithPaths
import com.cerebrallychallenged.hypogean.vanilla.procedural.DefaultRegionSystem
import com.cerebrallychallenged.hypogean.vanilla.procedural.createActionRegionVisualization
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.view.ActionInputState
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.dispose
import com.cerebrallychallenged.jun.asset.disposeAndClear
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

class MoveVisualizer(
    private val assetLibrary: AssetLibrary,
    private val selectionComponents: List<CompositeNode>,
    private val pathMaterial: UnrealRef<UMaterialInterface>,
    private val completeTargetAsset: CompositeAsset,
    private val partialTargetAsset: CompositeAsset,
) : ActionVisualizer {
    object Factory : ActionVisualizerFactory {
        override suspend fun create(assetLibrary: AssetLibrary, actionInputState: ActionInputState): ActionVisualizer {
            val selected = actionInputState.selected
            val selectionComponents = if (selected.hasInstances()) {
                createActionRegionVisualization(
                    assetLibrary,
                    DefaultRegionSystem,
                    selected,
                    assetLibrary.load(Hypogean.M_Region),
                    assetLibrary.load(Hypogean.M_ExtraRegion),
                    if (actionInputState.prefix == null) selected.activeActor?.position else null
                )
            } else listOf()
            return MoveVisualizer(
                assetLibrary,
                selectionComponents,
                Hypogean.M_ArrowFlow,
                Asset_BoxFrame,
                Asset_WaypointCone
            ).apply {
                updatePrefixComponents(
                    actionInputState.selected.activeActor,
                    actionInputState.prefix as? ActionInstanceWithPaths
                )
            }
        }
    }

    private val prefixNodes = mutableListOf<CompositeNode>()

    private val targetNodes = mutableListOf<CompositeNode>()

    private suspend fun updatePrefixComponents(activeActor: Actor?, prefix: ActionInstanceWithPaths?) {
        prefixNodes.disposeAndClear()
        if (activeActor != null && prefix != null) {
            for (path in prefix.paths) {
                createNodes(activeActor, path, pathMaterial, partialTargetAsset, assetLibrary, prefixNodes::add)
            }
        }
    }

    private suspend fun updateTargetComponents(newState: ActionInputState) {
        targetNodes.disposeAndClear()
        val hovered = newState.hovered
        if (hovered.singleTarget != null) {
            val completeness = newState.completeness
            val instance = hovered
                    .compatibleInstances(completeness)
                    .filterIsInstance<ActionInstanceWithPaths>()
                    .firstOrNull()
            if (instance != null) {
                val targetMesh = when (completeness) {
                    ActionInstanceCompleteness.Complete -> completeTargetAsset
                    ActionInstanceCompleteness.Partial -> partialTargetAsset
                }
                createNodes(
                    instance.activeActor,
                    instance.paths.last(),
                    pathMaterial,
                    targetMesh,
                    assetLibrary,
                    targetNodes::add
                )
            }
        }
    }

    override suspend fun update(change: ActionInputStateChanged) {
        val newState = change.newState
        val hasPrefixChanged = change.hasPrefixChanged
        if (hasPrefixChanged) {
            updatePrefixComponents(newState.selected.activeActor, newState.prefix as? ActionInstanceWithPaths)
        }
        if (hasPrefixChanged || change.hasHoverChanged || change.hasCompletenessChanged) {
            updateTargetComponents(newState)
        }
    }

    override fun dispose() {
        selectionComponents.dispose()
        prefixNodes.disposeAndClear()
        targetNodes.disposeAndClear()
    }
}

private suspend fun createNodes(
    activeActor: Actor,
    path: CellPath,
    pathMaterial: UnrealRef<UMaterialInterface>,
    targetAsset: CompositeAsset,
    assetLibrary: AssetLibrary,
    addNode: (CompositeNode) -> Unit
) {
    val target = path.target
    val actorSize = activeActor.size
    val shift = ((actorSize - Vec2i.ONE) * 50.0f).append(0.0f)
    addNode(Asset_Path.create(assetLibrary).apply {
        parameters[Asset_Path.PathParameter] = Pair(path, pathMaterial)
        relativeLocation = shift
    })
    addNode(targetAsset.create(assetLibrary).apply {
        worldLocation = target.basePoint * 100.0f + shift
        relativeScale3D = actorSize.toFloat().append(activeActor.height)
    })
}
