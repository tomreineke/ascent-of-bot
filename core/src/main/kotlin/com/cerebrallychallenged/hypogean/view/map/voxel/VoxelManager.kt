package com.cerebrallychallenged.hypogean.view.map.voxel

import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.view.util.Optics
import com.cerebrallychallenged.hypogean.view.util.applyOptics
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponent
import com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStatic

class VoxelManager(private val assetLibrary: AssetLibrary) {
    inner class Voxel(private val position: Vec2i, internal val blockAsset: BlockAsset, internal val height: Float, private val inputListener: (InputEvent) -> Unit) {
        // Mesh has to be (re)built
        private var isDirty: Boolean = false

        private fun markDirty() {
            if (!isDirty) {
                isDirty = true
                dirtyVoxels.add(this)
            }
        }

        private var mesh: URuntimeMeshComponent? = null

        var optics: Optics = Optics.DefaultHidden
            set(value) {
                field = value
                mesh?.applyOptics(value)
            }

        fun dispose(removeFromMap: Boolean = true) {
            mesh?.unregisterComponent()
            mesh = null
            markNeighborsDirty()
            if (removeFromMap) {
                voxels.remove(position)
            }
        }

        private fun markNeighborsDirty() {
            voxels[position + Vec2i.UNIT_X]?.markDirty()
            voxels[position - Vec2i.UNIT_X]?.markDirty()
            voxels[position + Vec2i.UNIT_Y]?.markDirty()
            voxels[position - Vec2i.UNIT_Y]?.markDirty()
        }

        internal suspend fun updateMesh() {
            isDirty = false
            mesh?.unregisterComponent()
            mesh = null
            mesh = newObject<URuntimeMeshComponent>().apply {
                registerComponent()
                relativeLocation = (position * 100.0f).append(0.0f)
                val provider = newObject<URuntimeMeshProviderStatic>()
                initialize(provider)
                provider.setupMaterialSlot(0, "Material", assetLibrary.load(blockAsset.material))
                createVoxelMesh(provider, this@Voxel, arrayOf(
                    voxels[position + Vec2i.UNIT_Y],
                    voxels[position + Vec2i.UNIT_X],
                    voxels[position - Vec2i.UNIT_Y],
                    voxels[position - Vec2i.UNIT_X],
                ), position)
                inputListeners.add(inputListener)
                applyOptics(optics)
            }
        }

        init {
            markDirty()
            markNeighborsDirty()
        }
    }

    private val voxels: MutableMap<Vec2i, Voxel> = mutableMapOf()

    private val dirtyVoxels: ArrayDeque<Voxel> = ArrayDeque()

    fun registerProp(position: Vec2i, cellBlock: Item, inputListener: (InputEvent) -> Unit): Voxel? = cellBlock.blockAsset?.let { blockAsset ->
        Voxel(position, blockAsset, cellBlock.height, inputListener).apply {
            voxels.put(position, this)?.let {
                it.dispose(removeFromMap = false)
                log.warn { "New voxel for cell block $cellBlock replaces old voxel at $position" }
            }
        }
    }

    suspend fun processDirtyVoxels() {
        for (voxel in dirtyVoxels) {
            voxel.updateMesh()
        }
        dirtyVoxels.clear()
    }
}
