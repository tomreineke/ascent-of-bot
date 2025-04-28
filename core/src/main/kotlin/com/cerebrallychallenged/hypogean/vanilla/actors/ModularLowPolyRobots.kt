package com.cerebrallychallenged.hypogean.vanilla.actors

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.view.map.ActorAsset
import com.cerebrallychallenged.hypogean.view.map.MapView
import com.cerebrallychallenged.hypogean.view.map.VisActor
import com.cerebrallychallenged.hypogean.view.map.VisModularActor
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.math.geo.vec

class VisModularLowPolyRobot(
    mapView: MapView,
    entity: Actor,
    private val asset: ModularLowPolyRobotAsset,
) : VisModularActor(mapView, entity) {
    override val bodyAsset: CompositeAsset
        get() = asset.bodyAsset

    override suspend fun SlotPresentationContext.presentSlots() {
        asset.present(this)
    }
}


abstract class ModularLowPolyRobotAsset(
    internal val bodyAsset: CompositeAsset,
    internal val present: suspend VisModularActor.SlotPresentationContext.() -> Unit
) : ActorAsset() {
    override suspend fun create(mapView: MapView, actor: Actor): VisActor? {
        return VisModularLowPolyRobot(mapView, actor, this)
    }
}

suspend fun VisModularActor.SlotPresentationContext.presentChassis() {
    present("chassis") { _, chassis, _ ->
        rootNode.relativeLocation = vec(0.0f, 0.0f, chassis.height * 100.0f)
    }
}

object SmallModularLowPolyRobotAsset1 : ModularLowPolyRobotAsset(Asset_Robot_BodySmall_Type1, {
    presentChassis()
    present("left_arm", "Socket1_(top)")
})

object SmallModularLowPolyRobotAsset3 : ModularLowPolyRobotAsset(Asset_Robot_BodySmall_Type3, {
    presentChassis()
    present("left_arm", "Socket1_(top)")
})

object SmallModularLowPolyRobotAsset4 : ModularLowPolyRobotAsset(Asset_Robot_BodySmall_Type4, {
    presentChassis()
    present("left_arm", "Socket1_(top)")
})