@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Event
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.HeadedProp
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.base.heading
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.diameter
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.trigger.CurvedMoveSegment
import com.cerebrallychallenged.hypogean.model.trigger.toSegments
import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.rays.circleBresenham
import com.cerebrallychallenged.hypogean.vanilla.actions.moveActorAlongPathSegments
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.description
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovement
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovementExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.vanilla.refs.ManipulatorRobot.SM_ManipulatorRobot_Static
import com.cerebrallychallenged.hypogean.view.map.events.EntityMoveEvent
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.degrees
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.curve.Polyline
import com.cerebrallychallenged.jun.math.geo.vec
import it.unimi.dsi.fastutil.objects.ObjectArraySet

object Asset_ManipulatorRobot : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_ManipulatorRobot_Static)
        transform(scale = vec(0.25f, 0.25f, 0.25f)) {
            rotate(Vec3f.UNIT_Z, 180.degrees)
        }
    }
})

class ManipulatorRobot(initializer: Initializer) : Prop(initializer), HeadedProp {
    init {
        name = "Manipulator robot"
        asset = Asset_ManipulatorRobot
        groundMovementBlocking = BlockingValue { 1.0f }
        description = "The manipulator robot will move small characters from one place to another."
    }
}

class ManipulatorRobotActivation(initializer: Initializer) : Event(initializer) {
    init {
        name = "Manipulator robot"
        icon = Images.PortraitManipulator
    }

    var manipulatorRobotGroup: List<ManipulatorRobot> by attribute(listOf())

    context(CascadeContext)
    override suspend fun execute(): InitiativeCost {
        val movedActors: MutableSet<Actor> = ObjectArraySet()
        val (movingManipulators, nonMovingManipulators) = manipulatorRobotGroup.partition { manipulator ->
            val gripStart = manipulator.position + manipulator.heading.delta
            world.cellAt(gripStart)?.presentActor?.let { movedActors.add(it) } ?: false
        }

        for (robot in nonMovingManipulators) {
            // TODO check blockers for rotating arm
            val rotationCurve = Polyline.from(
                (robot.heading.opposite().delta.toFloat()).append(0.0f).toLookAtWith(Vec3f.UNIT_Z)
            ).apply { speed = /*ROTATION_SPEED*/ 6.0f }.build()
            val positionCurve = Polyline.constant(robot.position2f.append(0.0f), rotationCurve.endTime)

            world.notifyViewEvent(EntityMoveEvent(robot, positionCurve, rotationCurve))
        }

        for (robot in movingManipulators) {
            val position = robot.position
            val heading = robot.heading
            val gripStart = position + heading.delta
            val radius = heading.delta.length.toInt()
            world.cellAt(gripStart)?.presentActor?.let { actor ->
                val cells = circleBresenham(position, radius, heading, heading.opposite(), true)
                    .map { world.cellAt(it)!! }
                val sourceCell = cells.first()
                val waypoints = cells.drop(1).toList()
                val pathSegments: List<CurvedMoveSegment> = CellPath(sourceCell, waypoints).toSegments(
                    GroundMovementExtractor,
                    actor.diameter,
                    actor
                ).toList()
                moveActorAlongPathSegments(
                    actor,
                    pathSegments,
                    GroundMovement(actor)
                )
            }
        }
        return InitiativeCost.Delta(1)
    }
}
