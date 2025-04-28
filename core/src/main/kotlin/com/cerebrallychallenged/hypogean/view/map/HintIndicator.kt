package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.view.Material
import com.cerebrallychallenged.hypogean.view.Time
import com.cerebrallychallenged.hypogean.view.hint.HideHint
import com.cerebrallychallenged.hypogean.view.hint.Hint
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.FAttachmentTransformRules
import com.cerebrallychallenged.jun.unreal.FDetachmentTransformRules
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceDynamic

internal class HintIndicator private constructor(
    private val mapView: MapView,
    private val node: CompositeNode,
): FactionContext by mapView {
    companion object {
        suspend operator fun invoke(mapView: MapView, hintColor: FLinearColor) = HintIndicator(
                mapView,
                Asset_GroundCircleIndicator.create(mapView.assetLibrary).apply {
                    parameters[Time] = 0.0f
                    parameters[Material] = UMaterialInstanceDynamic.create(mapView.assetLibrary.load(Hypogean.M_IndicatorMaterial)).apply {
                        parameters["Color"] = hintColor
                    }
                    walkComponents<UPrimitiveComponent> {
                        visibility = false
                        renderCustomDepth = true
                        customDepthStencilValue = PostProcessOutlines.HintStencilValue
                    }
                }
        )
    }

    private var totalSeconds = 0.0f

    fun onTick(deltaSeconds: Float) {
        totalSeconds += deltaSeconds
        node.parameters[Time] = totalSeconds
    }

    fun onChange(change: WorldChange) {
        when (change) {
            is WorldChange.ViewEventHappened -> {
                when (val viewEvent = change.viewEvent) {
                    is Hint -> {
                        val entity = viewEvent.affectedEntity
                        if (entity.recon != Recon.Visible) return
                        val visEntity = mapView.visMap[entity] ?: return
                        node.component.attachToComponent(visEntity.rootComponent, FAttachmentTransformRules.KeepRelativeTransform)
                        node.walkComponents<USceneComponent> {
                            visibility = true
                        }
                        node.relativeLocation = vec(0.0f, 0.0f, 10.0f)
                    }
                    is HideHint -> {
                        node.walkComponents<USceneComponent> { visibility = false }
                        node.component.detachFromComponent(FDetachmentTransformRules.KeepRelativeTransform)
                    }
                }
            }
            else -> {}
        }
    }
}
