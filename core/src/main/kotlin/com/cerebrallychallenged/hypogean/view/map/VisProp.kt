package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.base.placement
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.assetParameterBindings
import com.cerebrallychallenged.hypogean.vanilla.attributes.transform
import com.cerebrallychallenged.hypogean.vanilla.attributes.visibleIfLostSight
import com.cerebrallychallenged.hypogean.view.map.voxel.VoxelManager.Voxel
import com.cerebrallychallenged.hypogean.view.map.voxel.blockAsset
import com.cerebrallychallenged.hypogean.view.util.Optics
import com.cerebrallychallenged.hypogean.view.util.applyOptics
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.FAttachmentTransformRules
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.newObject

class VisProp(mapView: MapView, entity: Item) : VisEntity<Item>(mapView, entity) {
    override val rootComponent = newObject<USceneComponent>().apply {
        registerComponent()
    }

    override var node: CompositeNode? = null

    private var voxel: Voxel? = null

    private fun updateVoxel() {
        voxel?.dispose()
        voxel = (entity.anchor as? Cell)?.position?.let { position ->
            mapView.voxelManager.registerProp(position, entity, inputListener)
        }
        updateOptics()
    }

    override suspend fun initialize() {
        rebuild()
        updateVoxel()
    }

    override fun dispose() {
        super.dispose()
        node?.dispose()
        rootComponent.unregisterComponent()
        voxel?.dispose()
    }

    private val changeVisitor = object : WorldChange.SimpleSuspendVisitor {
        override suspend fun visit(change: WorldChange.StatusEffectCreated) {
            rebuild()
        }

        override suspend fun visit(change: WorldChange.Removed) {
            rebuild()
        }

        override suspend fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            if (change.entity is StatusEffect) {
                rebuild()
            } else {
                change.ifOf(Item::asset) {
                    rebuild()
                }
                change.ifOf(StatusEffect::asset) {
                    rebuild()
                }
                change.ifOf(Entity::assetParameterBindings) {
                    rebuild()
                }
                change.ifOf(Entity::transform) {
                    updateAssetTransform()
                }
                change.ifOf(Item::placement) {
                    updatePlacement()
                }
                change.ifOf(Item::blockAsset) {
                    updateVoxel()
                }
                change.ifOf(Item::height) {
                    updateVoxel()
                }
                node?.let { entity.assetParameterBindings.apply(change, it) }
            }
        }
    }

    override suspend fun onChange(change: WorldChange) {
        super.onChange(change)
        change.accept(changeVisitor)
    }

    private suspend fun rebuild() {
        val asset = entity.asset
        node?.dispose()
        node = null
        node = asset?.create(mapView.assetLibrary)?.apply {
            entity.assetParameterBindings.apply(entity, this)
            component.attachToComponent(rootComponent, FAttachmentTransformRules.KeepRelativeTransform)
            addInputListener(inputListener)
            entity.transform?.let { this.transform = it }
            visualizeStatusEffects(entity, this, assetLibrary)
        }
        updateAssetTransform()
        updatePlacement()
        updateOptics()
    }

    private fun updateAssetTransform() {
        node?.transform = entity.transform ?: Transform3f.IDENTITY
    }

    private fun updatePlacement() {
        if (entity.isLocated) {
            rootComponent.worldTransform = Transform3f(
                entity.placement.rotation,
                entity.basePoint * 100.0f,
                Vec3f.ONE
            )
        }
    }

    override fun updateOptics() {
        val recon = entity.recon
        val visible = recon != Recon.Unknown && !hidden
        val optics = Optics(
            meshVisible = visible,
            lightVisible = visible,
            castHiddenShadow = true,
            desaturated = visible && recon == Recon.LostSight && entity.visibleIfLostSight
        )
        node?.walkComponents<USceneComponent> {
            applyOptics(optics)
        }
        voxel?.optics = optics
    }

    override fun updateLoStencilValue(stencilValue: Int) {
        node?.walkLoStencilValue(stencilValue)
    }
}
