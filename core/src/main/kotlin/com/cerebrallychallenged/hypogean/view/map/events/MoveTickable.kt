package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.Animation
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.curve.Curve
import com.cerebrallychallenged.jun.unreal.USceneComponent
import kotlin.math.max

/**
 * Some entities may be teleported, e.g. entities with recon INVISIBLE, for some reason,
 * e.g. to speed up turn transitions. For this we have the parameter teleport. If set to
 * true, the entity is instantly teleported to target location.
 */
class MoveAnimation(
    private val component: USceneComponent,
    private val positionCurve: Curve<Vec3f>,
    private val rotationCurve: Curve<Quaternion>,
    private val destroyAtEnd: Boolean
) : Animation(max(positionCurve.endTime, rotationCurve.endTime)) {
    override fun onTick(deltaTime: Float): Boolean {
        component.worldLocation = positionCurve(time) * 100.0f
        component.worldRotation = rotationCurve(time)
        return false
    }

    override fun onEnd() {
        if (destroyAtEnd) {
            component.destroyComponent()
        }
    }
}
//
///**
// * Some entities may be teleported, e. g. entities with recon INVISIBLE, for some reason,
// * e. g. to speed up turn transitions. For this we have the parameter teleport. If set to
// * true, the entity is instantly teleported to target location.
// */
//class MoveTickable(
//        private val component: USceneComponent,
//        private val positionCurve: Curve<Vec3f>,
//        private val rotationCurve: Curve<Quaternion>,
//        private val destroyAtEnd: Boolean
//) : TickingEvent.Tickable {
//    private val endTime = max(positionCurve.endTime, rotationCurve.endTime)
//
//    override fun tick(deltaSeconds: Float, totalSeconds: Float): Boolean {
//        component.worldLocation = positionCurve(totalSeconds) * 100.0f
//        component.worldRotation = rotationCurve(totalSeconds)
//        return totalSeconds < endTime
//    }
//
//    override fun end() {
//        if (destroyAtEnd) {
//            component.destroyComponent()
//        }
//    }
//}
