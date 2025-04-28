package com.cerebrallychallenged.hypogean.view.map.visualizers

import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.singleTarget
import com.cerebrallychallenged.hypogean.model.effect.areaEffect
import com.cerebrallychallenged.hypogean.model.presentCellFilling
import com.cerebrallychallenged.hypogean.model.presentHeight
import com.cerebrallychallenged.hypogean.model.size
import com.cerebrallychallenged.hypogean.vanilla.procedural.DefaultRegionSystem
import com.cerebrallychallenged.hypogean.vanilla.procedural.createActionRegionVisualization
import com.cerebrallychallenged.hypogean.vanilla.procedural.createRegionVisualization
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.view.ActionInputState
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.asset.dispose
import com.cerebrallychallenged.jun.asset.disposeAndClear
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.points
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

class GenericVisualizer(
    private val assetLibrary: AssetLibrary,
    private val selectionComponents: List<CompositeNode>,
    private val targetNode: CompositeNode,
    private val areaOfEffectMaterial: UMaterialInterface
) : ActionVisualizer {
    class Factory(private val isAttack: Boolean) : ActionVisualizerFactory {
        override suspend fun create(assetLibrary: AssetLibrary, actionInputState: ActionInputState): ActionVisualizer {
            val targetNodeMaterial = assetLibrary.load(
                if (isAttack) Hypogean.MI_PulsatingAttack else Hypogean.MI_PulsatingAction
            )
            val selected = actionInputState.selected
            return GenericVisualizer(
                assetLibrary,
                if (selected.hasInstances()) {
                    createActionRegionVisualization(
                        assetLibrary,
                        DefaultRegionSystem,
                        selected,
                        assetLibrary.load(if (isAttack) Hypogean.M_AttackRegion else Hypogean.M_Region),
                        assetLibrary.load(Hypogean.M_ExtraRegion)
                    )
                } else listOf(),
                Asset_BoxFrame.create(assetLibrary).apply {
                    walkComponents<UPrimitiveComponent> {
                        visibility = false
                        materials[0] = targetNodeMaterial
                    }
                },
                assetLibrary.load(Hypogean.M_ExtraRegion)
            )
        }
    }

    private val areaOfEffectNodes = mutableListOf<CompositeNode>()

    override suspend fun update(change: ActionInputStateChanged) {
        val hovered = change.newState.hovered
        areaOfEffectNodes.disposeAndClear()
        val isVisible = if (hovered.hasInstances() && hovered.isSingleTargetFocused) {
            val target = hovered.singleTarget
            if (target is LocatedEntity) {
                targetNode.worldLocation = target.basePoint * 100.0f
                val cellFillingFactor = if (target.presentCellFilling) 1.5f else 1.0f
                targetNode.relativeScale3D =
                    (target.size * cellFillingFactor).append(target.presentHeight.coerceAtLeast(0.2f))
                val instance = hovered.instances.first()
                val equipment = instance.equipment
                equipment.areaEffect?.let { areaEffect ->
                    val world = equipment.world
                    val raysQuery = world.queryRays(target.position, areaEffect.blockerValueExtractor, equipment)
                    val positions =
                        Bounds.centered(target.position, Vec2i.ONE * areaEffect.radius.ceilToInt()).points
                            .filter { raysQuery.computeExposure(it, areaEffect.penetrationStrength) > 0.0f }
                            .toSet()
                    createRegionVisualization(
                        assetLibrary,
                        DefaultRegionSystem,
                        areaOfEffectMaterial,
                        0.3f,
                        positions,
                        areaOfEffectNodes
                    )
                }
                true
            } else false
        } else false
        targetNode.walkComponents<USceneComponent> { visibility = isVisible }
    }



    override fun dispose() {
        selectionComponents.dispose()
        targetNode.dispose()
        areaOfEffectNodes.dispose()
    }
}
