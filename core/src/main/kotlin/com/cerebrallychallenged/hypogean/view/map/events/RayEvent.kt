package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.Animation
import com.cerebrallychallenged.hypogean.vanilla.refs.BasicShapes
import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.unreal.mesh.ESplineMeshAxis
import com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponent
import com.cerebrallychallenged.jun.unreal.newObject

class RayEvent(
    private val source: Position,
    private val target: Position,
    override val duration: Float,
    private val material: UnrealRef<UMaterialInterface>,
    private val diameter: Float,
) : MapViewEvent() {
    context(MapViewContext)
    override suspend fun execute() {
        val material = assetLibrary.load(material)
        val s = requireNotNull(source.computeTransform()).translation
        val t = requireNotNull(target.computeTransform()).translation
        val tangent = t - s
        val spline = newObject<USplineMeshComponent>().apply {
            staticMesh = assetLibrary.load(BasicShapes.Cylinder)
            forwardAxis = ESplineMeshAxis.Z
            val scale = vec(diameter, diameter)
            startScale = scale
            endScale = scale
            setStartAndEnd(s, tangent, t, tangent, updateMesh = true)
            materials[0] = material
            registerComponent()
        }
        addAnimation(object : Animation(duration) {
            override fun onEnd() {
                spline.unregisterComponent()
            }
        })
    }
}
