package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.attribute.attribute

abstract class ActorAsset {
    abstract suspend fun create(mapView: MapView, actor: Actor): VisActor?
}

var Actor.asset: ActorAsset? by attribute(null)

class ActorAssets : SimpleObjectRegistry<ActorAsset>()
