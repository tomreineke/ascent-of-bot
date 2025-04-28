package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.jun.asset.CompositeAsset

class VisSimpleActor(
    mapView: MapView,
    entity: Actor,
    override val bodyAsset: CompositeAsset
) : VisModularActor(mapView, entity) {
    override suspend fun SlotPresentationContext.presentSlots() {}
}

abstract class SimpleActorAsset(private val bodyAsset: CompositeAsset) : ActorAsset() {
    override suspend fun create(mapView: MapView, actor: Actor): VisSimpleActor =
        VisSimpleActor(mapView, actor, bodyAsset)
}
