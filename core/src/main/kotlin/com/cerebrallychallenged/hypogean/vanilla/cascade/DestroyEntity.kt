package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.effect.destructionEffect
import com.cerebrallychallenged.hypogean.view.map.events.HideEvent


context(CascadeBlock)
suspend fun performEntityDestruction(entity: Entity) {
    if (!isReal || !entity.isAlive) return
    // deal area of effect damage before removing entity, so that we still have its anchor / position
    if (entity is LocatedEntity) {
        entity.destructionEffect?.let { areaEffect ->
            dealAreaEffects(areaEffect, entity, entity.position, EffectReason.ByEntity(entity))
        }
    }
    // entity may already have been destroyed due to area effects
    if (entity.isAlive) {
        entity.remove()
    }
    world.notifyViewEvent(HideEvent(entity))
    //FIXME if entity is prop and fills its cell, then maybe hit props in adjacent cells. For example, if a brick
    //      wall is destroyed, a painting in a neighboring cell attached to it should fall down.
}
