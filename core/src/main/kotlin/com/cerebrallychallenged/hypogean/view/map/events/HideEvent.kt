package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.view.map.MapViewContext

class HideEvent(private val entity: Entity, private val hidden: Boolean = true) : MapViewEvent() {
    context(MapViewContext)
    override suspend fun execute() {
        visMap[entity]?.hidden = hidden
    }
}
