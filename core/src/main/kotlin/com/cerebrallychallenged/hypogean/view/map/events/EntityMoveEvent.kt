package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.pathfinding.toCurve
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.curve.Polyline
import com.cerebrallychallenged.jun.math.geo.curve.toFollowCurve
import kotlin.math.max

class EntityMoveEvent(
    private val entity: Entity,
    private val positionCurve: Polyline<Vec3f>,
    private val rotationCurve: Polyline<Quaternion>
) : MapViewEvent() {
    companion object {
        fun linearMove(
            entity: Entity,
            source: Vec3f,
            target: Vec3f,
            constantAngle: Angle? = null,
            speed: Float = 3.0f
        ): EntityMoveEvent? {
            if (source == target) return null
            val positionCurve = Polyline.from(source).apply { this.speed = speed }.lineTo(target).build()
            return EntityMoveEvent(
                entity,
                positionCurve,
                Polyline.constantZRotation(constantAngle ?: (target - source).xy.angle(), positionCurve.endTime)
            )
        }

        fun zRotation(
            entity: Entity,
            constantPosition: Vec3f,
            fromAngle: Angle,
            toAngle: Angle,
            speed: Float = 6.0f
        ): EntityMoveEvent? {
            if (fromAngle == toAngle) return null
            val rotationCurve = Polyline.linearZRotation(fromAngle, toAngle, speed)
            return EntityMoveEvent(entity, Polyline.constant(constantPosition, rotationCurve.endTime), rotationCurve)
        }
    }

    context(MapViewContext)
    override suspend fun execute() {
        val visEntity = visMap[entity] ?: return
        addAnimation(MoveAnimation(
            visEntity.rootComponent,
            positionCurve,
            rotationCurve,
            destroyAtEnd = false
        ))
    }

    override val duration: Float
        get() = max(positionCurve.endTime, rotationCurve.endTime)
}

private const val ROTATION_SPEED = 6.0f

private const val MOVEMENT_SPEED = 3.0f

fun createMoveEvents(actor: Actor, cellPaths: List<CellPath>): Pair<EntityMoveEvent, EntityMoveEvent> {
    val initialHeading = actor.heading
    val initialRotation = Quaternion.fromNormalAxisAngle(Vec3f.UNIT_Z, initialHeading)
    val positionCurve = cellPaths.first().toCurve().reparameterizeAtUnitSpeed(0.01f, MOVEMENT_SPEED)
    val rotationCurve = positionCurve.toFollowCurve(Vec3f.UNIT_Z).flatten(0.01f)
    val preRotationCurve = Polyline
            .from(initialRotation)
            .apply { speed = ROTATION_SPEED }
            .lineTo(rotationCurve.startPoint)
            .build()
    val prePositionCurve = Polyline.constant(positionCurve.startPoint, preRotationCurve.endTime)
    return Pair(EntityMoveEvent(actor, prePositionCurve, preRotationCurve), EntityMoveEvent(actor, positionCurve, rotationCurve))
}
