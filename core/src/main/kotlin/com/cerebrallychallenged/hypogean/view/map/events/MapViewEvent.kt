package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.ViewEvent
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.view.map.MapViewContext

abstract class MapViewEvent : ViewEvent {
    context(MapViewContext)
    abstract suspend fun execute()

    context(CascadeContext)
    suspend fun notifyWorldAndDelay() {
        world.notifyViewEvent(this)
        delay(duration)
    }
}
