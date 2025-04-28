package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.Animation
import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.math.geo.Vec3f

class LinearCompositeEvent(
    private val asset: CompositeAsset,
    speed: Float,
    private val source: Position,
    private val target: Position,
) : MapViewEvent() {
    override val duration =
        requireNotNull(source.estimatedTransform).translation
            .distanceTo(requireNotNull(target.estimatedTransform).translation) * 0.01f / speed

    context(MapViewContext)
    override suspend fun execute() {
        val startPosition = requireNotNull(source.computeTransform()?.translation)
        val endPosition = requireNotNull(target.computeTransform()?.translation)
        val node = asset.create(assetLibrary).apply {
            relativeLocation = startPosition
            relativeRotation = (endPosition - startPosition).toLookAtWith(Vec3f.UNIT_Z)
        }
        addAnimation(object : Animation(duration) {
            override fun onTick(deltaTime: Float): Boolean {
                if (duration > 0.0f) {
                    node.relativeLocation = startPosition.interpolate(time / duration, endPosition)
                }
                return false
            }

            override fun onEnd() {
                node.dispose()
            }
        })
    }
}
