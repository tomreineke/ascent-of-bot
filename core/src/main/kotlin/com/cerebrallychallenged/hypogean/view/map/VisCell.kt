package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.USceneComponent

class VisCell(
    mapView: MapView,
    entity: Cell,
    private val targetableNode: CompositeNode
) : VisEntity<Cell>(mapView, entity) {
    override val rootComponent: USceneComponent
        get() = targetableNode.component

    override suspend fun initialize() {
        targetableNode.apply {
            walkComponents<USceneComponent> { visibility = false }
            transform(translation = entity.basePoint * 100.0f + Vec3f.UNIT_Z * (entity.topPoint.z * 100.0f + 0.1f))
            addInputListener(inputListener)
        }
    }

    override fun dispose() {
        super.dispose()
        targetableNode.dispose()
    }

    override fun updateLoStencilValue(stencilValue: Int) {
    }
}
