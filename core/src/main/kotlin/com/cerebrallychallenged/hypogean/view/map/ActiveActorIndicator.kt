@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.activestate.SimulationState
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean.SM_ActiveActorIndicator
import com.cerebrallychallenged.hypogean.view.ActionSubmitted
import com.cerebrallychallenged.hypogean.view.Material
import com.cerebrallychallenged.hypogean.view.Time
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.FAttachmentTransformRules
import com.cerebrallychallenged.jun.unreal.FDetachmentTransformRules
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent
import com.cerebrallychallenged.jun.unreal.USceneComponent

private val ROTATION_SPEED = Angle.DEGREE_180

object Asset_GroundCircleIndicator : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_ActiveActorIndicator)
        //TODO: Why does the following have no effect?
        // Why is active indicator mirroring in actual chassis of actor?
        visibleInRayTracing = false
        visibleInReflectionCaptures = false
        Material.bind { materials[0] = it }
        Time.bind {
            transform(translation = vec(0.0f, 0.0f, 1.0f)) {
                rotate(Vec3f.UNIT_Z, ROTATION_SPEED * it)
            }
        }
    }
})

internal class ActiveActorIndicator private constructor(
    private val mapView: MapView,
    private val node: CompositeNode
): FactionContext by mapView {
    companion object {
        suspend operator fun invoke(mapView: MapView) = ActiveActorIndicator(
                mapView,
                Asset_GroundCircleIndicator.create(mapView.assetLibrary).apply {
                    parameters[Time] = 0.0f
                    walkComponents<UPrimitiveComponent> {
                        renderCustomDepth = true
                        visibility = false
                    }
                }
        )
    }

    private var totalSeconds = 0.0f

    private var currentActiveActor: Actor? = null

    fun onTick(deltaSeconds: Float) {
        totalSeconds += deltaSeconds
        node.parameters[Time] = totalSeconds
    }

    fun onChange(change: WorldChange) {
        when (change) {
            is WorldChange.Removed -> {
                val (entity) = change
                if (entity == currentActiveActor) {
                    deactivate()
                }
            }
            is WorldChange.ActiveStateChanged -> {
                when (val activeState = change.activeState) {
                    is ActiveActorState -> {
                        val activeActor = activeState.activeActor
                        if (activeActor.recon != Recon.Visible) return
                        val visActor = mapView.visMap[activeActor] ?: return
                        val factionRelation = activeActor.factionRelation
                        node.component.attachToComponent(visActor.rootComponent, FAttachmentTransformRules.KeepRelativeTransform)
                        val stencilValue = mapView.postProcessOutlines.stencilValueFor(factionRelation, false)
                        node.walkComponents<USceneComponent> {
                            if (this is UPrimitiveComponent) {
                                customDepthStencilValue = stencilValue
                            }
                            visibility = true
                        }
                        node.parameters[Material] = mapView.factionRelationMaterials[factionRelation]

                        currentActiveActor = activeActor
                    }
                    is SimulationState -> {
                        deactivate()
                    }
                }
            }
            else -> {}
        }
    }

    fun onViewModelChange(change: ViewModelChange) {
        when (change) {
            is ActionSubmitted -> {
                deactivate()
            }
            else -> {}
        }
    }

    private fun deactivate() {
        node.walkComponents<USceneComponent> { visibility = false }
        node.component.detachFromComponent(FDetachmentTransformRules.KeepRelativeTransform)
        currentActiveActor = null
    }
}
