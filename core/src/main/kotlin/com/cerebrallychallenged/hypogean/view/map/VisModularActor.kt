package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.Slot
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.vanilla.props.NullAsset
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.view.FactionMaterial
import com.cerebrallychallenged.hypogean.view.util.Optics
import com.cerebrallychallenged.hypogean.view.util.applyOptics
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.ECollisionEnabled
import com.cerebrallychallenged.jun.unreal.FAttachmentTransformRules
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.newObject

abstract class VisModularActor(
    mapView: MapView,
    entity: Actor,
) : VisActor(mapView, entity) {
    inner class SlotPresentationContext(val rootNode: CompositeNode) {
        suspend fun present(
            slotName: String,
            socketName: String? = null,
            block: (Slot, Item, CompositeNode) -> Unit = { _, _, _ -> }
        ) {
            val slot = entity.slotOrNull(slotName) ?: return
            val item = slot.containedItems.firstOrNull() ?: return
            val asset = item.asset ?: return
            val node = asset.create(assetLibrary)
            node.setupInputAndMaterial()
            visualizeStatusEffects(item, node, assetLibrary)
            rootNode.attachChild(node, socketName)
            block(slot, item, node)
            slotItemNodes[item] = node

            ////
//            item.shootingFx?.let { fx ->
//                fx.muzzleFlashes.firstOrNull()?.execute(mapView, entity, item, Vec3f.ZERO, Quaternion.IDENTITY)
//            }
//            var time = 0.0f
//            GlobalScope.launch(Dispatchers.Unreal) {
//                JunManager.runTicker {
//                    time += it
//                    node.relativeRotation = Quaternion.fromAxisAngle(Vec3f.UNIT_Z, time.radians)
//                    true
//                }
//            }
            ////
        }
    }

    override lateinit var node: CompositeNode

    private var isLocated: Boolean = false

    abstract val bodyAsset: CompositeAsset

    override val rootComponent: USceneComponent = newObject<USceneComponent>().apply {
        registerComponent()
    }

    private val slotItemNodes = mutableMapOf<Entity, CompositeNode>()

    override suspend fun initialize() {
        rebuild()
        updateLocation(entity.location)
        updateHeading(entity.heading)
    }

    override fun dispose() {
        super.dispose()
        node.dispose()
    }

    private val changeVisitor = object : WorldChange.SimpleSuspendVisitor {
        override suspend fun visit(change: WorldChange.Removed) {
            rebuild()
        }

        override suspend fun visit(change: WorldChange.ItemMove) {
            val (_, oldContainerPosition, newContainerPosition) = change
            if (newContainerPosition?.anchor == entity || oldContainerPosition?.anchor == entity) {
                rebuild()
            }
        }

        override suspend fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            if (change.entity is StatusEffect) {
                rebuild()
            }
        }
    }

    override suspend fun onChange(change: WorldChange) {
        super.onChange(change)
        change.accept(changeVisitor)
    }

    suspend fun rebuild() {
        if (::node.isInitialized) {
            node.dispose()
        }
        slotItemNodes.clear()
        // When loading save games it can happen that an actor is alive, even though
        // he was killed in the game and has health 0.
        // I couldn't track down why this happens, and as a workaround, I make sure
        // that in case of the health being 0, a null asset is loaded.
        if (entity.health > 0) {
            node = bodyAsset.create(assetLibrary).apply {
                component.attachToComponent(rootComponent, FAttachmentTransformRules.KeepRelativeTransform)
                setupInputAndMaterial()
                visualizeStatusEffects(entity, this, assetLibrary)
                SlotPresentationContext(this).presentSlots()
                walkComponents<UPrimitiveComponent> {
                    collisionEnabled = ECollisionEnabled.QueryOnly
                }
            }
        } else {
            node = NullAsset.create(assetLibrary)
        }
        updateOptics()
        updateStencilValue()
    }

    abstract suspend fun SlotPresentationContext.presentSlots()

    override fun updateLocation(location: Cell?) {
        if (location != null) {
            rootComponent.worldLocation = entity.basePoint * 100.0f
            if (!isLocated) {
                isLocated = true
                updateOptics()
            }
        } else {
            if (isLocated) {
                isLocated = false
                updateOptics()
            }
        }
    }

    override fun updateHeading(heading: Angle) {
        rootComponent.worldRotation = Quaternion.fromAxisAngle(Vec3f.UNIT_Z, heading)
    }

    override fun updateOptics() {
        val visible = entity.recon == Recon.Visible && !hidden
        val optics = Optics(
            meshVisible = visible,
            lightVisible = true, // Could be made controllable by parameter ("Turn lights off")
            castHiddenShadow = true,
            desaturated = false
        )
        node.walkComponents<USceneComponent> {
            applyOptics(optics)
        }
    }

    override fun updateLoStencilValue(stencilValue: Int) {
        node.walkLoStencilValue(stencilValue)
    }

    private fun CompositeNode.setupInputAndMaterial() {
        addInputListener(inputListener)
        parameters[FactionMaterial] = mapView.factionRelationMaterials[entity.factionRelation]
    }

    override fun nodeForItem(item: Item): CompositeNode? = slotItemNodes[item]
}
