package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.view.globalDirectionalBrightness
import com.cerebrallychallenged.jun.math.degrees
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.light.UDirectionalLightComponent
import com.cerebrallychallenged.jun.unreal.newObject

class VisWorld(mapView: MapView, entity: World) : VisEntity<World>(mapView, entity) {
    override val rootComponent: UDirectionalLightComponent = newObject<UDirectionalLightComponent>().apply {
        castShadows = false
        relativeRotation = Quaternion.fromAxisAngle(Vec3f.UNIT_Y, 46.degrees)
        registerComponent()
    }

    override suspend fun initialize() {
        updateBrightness(entity.globalDirectionalBrightness)
    }

    override suspend fun onChange(change: WorldChange) {
        if (change is WorldChange.AttributeChanged<*>) {
            change.ifOf(World::globalDirectionalBrightness) { (_, _, brightness) ->
                updateBrightness(brightness)
            }
        }
    }

    private fun updateBrightness(brightness: Float) {
        rootComponent.intensity = brightness
    }

    override fun updateLoStencilValue(stencilValue: Int) {
    }
}
