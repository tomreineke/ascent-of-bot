package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.maps.Entity2ObjectMap

class VisMap(world: World) {
    private val map: MutableMap<Entity, VisEntity<*>> = Entity2ObjectMap(world)

    suspend fun <T : Entity> addAndInitialize(visEntity: VisEntity<T>) {
        map.put(visEntity.entity, visEntity)?.let {
            error("Cannot add $visEntity because ${visEntity.entity} is already represented by $it")
        }
        visEntity.initialize()
    }

    fun removeAndDispose(entity: Entity) {
        map.remove(entity)?.dispose()
    }

    fun disposeAndClear() {
        map.values.forEach(VisEntity<*>::dispose)
        map.clear()
    }

    operator fun <T : Entity> get(entity: T): VisEntity<T>? {
        @Suppress("UNCHECKED_CAST")
        return map[entity] as? VisEntity<T>
    }
}
