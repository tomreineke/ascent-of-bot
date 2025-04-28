@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.procedural

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.base.PropPlacement
import com.cerebrallychallenged.hypogean.model.base.addProp
import com.cerebrallychallenged.hypogean.rays.RayOrientation
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.assetParameterBindings
import com.cerebrallychallenged.hypogean.vanilla.props.CellFloor
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.view.MaterialAsset
import com.cerebrallychallenged.hypogean.view.map.bindAssetParameters
import com.cerebrallychallenged.hypogean.view.map.boundTo
import com.cerebrallychallenged.hypogean.view.materialAsset
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeParameter
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.runtimeMeshComponent
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.points
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.rmc.RectangularPatch
import com.cerebrallychallenged.jun.rmc.createMeshData
import com.cerebrallychallenged.jun.rmc.createSection
import com.cerebrallychallenged.jun.unreal.color.FColor
import com.cerebrallychallenged.jun.unreal.material.EMaterialDomain
import com.cerebrallychallenged.jun.unreal.material.UMaterial
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionProperties
import com.cerebrallychallenged.jun.unreal.rmc.URuntimeMesh
import com.cerebrallychallenged.jun.unreal.rmc.castsShadow
import com.cerebrallychallenged.jun.unreal.rmc.isVisible
import com.cerebrallychallenged.jun.unreal.rmc.materialSlot
import com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStatic
import com.cerebrallychallenged.jun.util.buffers.set

private fun smoothStep(v: Float): Float = 3 * v * v - 2 * v * v * v

private fun diffSmoothStep(v: Float): Float = 6 * v - 6 * v * v

private typealias FloatFun = (Float, Float) -> Float

private val zeroFun: FloatFun = { _, _ -> 0.0f }

private operator fun FloatFun.plus(rhs: FloatFun): FloatFun = { x, y -> this(x, y) + rhs(x, y) }

private val sectorVectors = Bounds.byMinSize(vec(-1, -1), vec(2, 2)).points.toList()

private fun createMeshes(depth: Float, thickness: Float): List<URuntimeMesh> {
    val len = 16
    val sideLength = 2 * len
    val d = 1.0f / (len - 1)
    data class VertexInfo(val sectorVector: Vec2i, val rx: Float, val ry: Float, val p: Vec2f)
    fun vertexInfo(x: Int, y: Int): VertexInfo {
        val sx = x / len
        val sy = y / len
        val rx = (x % len) * d
        val ry = (y % len) * d
        return VertexInfo(
                vec(sx - 1, sy - 1),
                rx,
                ry,
                vec(sx * (1.0f - thickness) + rx * thickness, sy * (1.0f - thickness) + ry * thickness)
        )
    }

    val defaultMaterial = UMaterial.getDefaultMaterial(EMaterialDomain.Surface)
    val meshSectionProperties = FRuntimeMeshSectionProperties.makeShared()
    meshSectionProperties.castsShadow = true
    meshSectionProperties.isVisible = true
    meshSectionProperties.materialSlot = 0

    val list = (0 until 256).map { bits ->
        val neighborhood = Neighborhood(bits)
        if (neighborhood.unify() != neighborhood) return@map null
        val functions = sectorVectors.associateWith {
            var f: FloatFun = zeroFun
            var dFdX: FloatFun = zeroFun
            var dFdY: FloatFun = zeroFun
            val diffScale = depth / thickness
            if (!neighborhood[it]) {
                f += { rx, ry -> (1.0f - smoothStep(rx)) * (1.0f - smoothStep(ry)) }
                dFdX += { rx, ry -> -diffScale * diffSmoothStep(rx) * (1.0f - smoothStep(ry)) }
                dFdY += { rx, ry -> -diffScale * (1.0f - smoothStep(rx)) * diffSmoothStep(ry) }
            }
            if (!neighborhood[it + vec(1, 0)]) {
                f += { rx, ry -> smoothStep(rx) * (1.0f - smoothStep(ry)) }
                dFdX += { rx, ry -> diffScale * diffSmoothStep(rx) * (1.0f - smoothStep(ry)) }
                dFdY += { rx, ry -> -diffScale * smoothStep(rx) * diffSmoothStep(ry) }
            }
            if (!neighborhood[it + vec(0, 1)]) {
                f += { rx, ry -> (1.0f - smoothStep(rx)) * smoothStep(ry) }
                dFdX += { rx, ry -> -diffScale * diffSmoothStep(rx) * smoothStep(ry) }
                dFdY += { rx, ry -> diffScale * (1.0f - smoothStep(rx)) * diffSmoothStep(ry) }
            }
            if (!neighborhood[it + vec(1, 1)]) {
                f += { rx, ry -> smoothStep(rx) * smoothStep(ry) }
                dFdX += { rx, ry -> diffScale * diffSmoothStep(rx) * smoothStep(ry) }
                dFdY += { rx, ry -> diffScale * smoothStep(rx) * diffSmoothStep(ry) }
            }
            Triple(f, dFdX, dFdY)
        }
        //for (sectorVector in Bounds.byMinSize(vec())


        val runtimeMesh = newObject<URuntimeMesh>()
        val provider = newObject<URuntimeMeshProviderStatic>()
        runtimeMesh.initialize(provider)
        provider.setupMaterialSlot(0, "Material", defaultMaterial)
        val meshData = meshSectionProperties.createMeshData(sideLength * sideLength)
        val rectangularPatch = RectangularPatch(sideLength, sideLength)
        meshData.updatePositions { position ->
            rectangularPatch.forEachVertex { index, x, y ->
                val (sectorVector, rx, ry, p) = vertexInfo(x, y)
                val (zFunction, _, _) = functions.getValue(sectorVector)
                position[index] = p.append((zFunction(rx, ry) - 1.0f) * depth) * 100.0f
            }
        }
        meshData.updateTangents { tangent, normal ->
            rectangularPatch.forEachVertex { index, x, y ->
                val (sectorVector, rx, ry, _) = vertexInfo(x, y)
                val (_, dFdX, dFdY) = functions.getValue(sectorVector)
                val xTangent = vec(1.0f, 0.0f, dFdX(rx, ry)).normalized()
                val yTangent = vec(0.0f, 1.0f, dFdY(rx, ry)).normalized()
                normal[index] = (xTangent cross yTangent)
                tangent[index] = xTangent
            }
        }
        meshData.updateColors { color ->
            rectangularPatch.forEachVertex { index, _, _ ->
                color[index] = FColor.White
            }
        }
        meshData.updateTexCoords { channel ->
            val uv = channel[0]
            rectangularPatch.forEachVertex { index, x, y ->
                val (_, _, _, p) = vertexInfo(x, y)
                uv[index] = p
            }
        }
        rectangularPatch.installTriangles(meshData)
        provider.createSection(0, 0, meshSectionProperties, meshData)
        runtimeMesh
    }
    return list.mapIndexed { index, mesh ->
        mesh ?: list[Neighborhood(index).unify().bits] ?: error("Missing mesh for unified neighborhood ")
    }
}

abstract class TrenchSystem(val depth: Float, thickness: Float) {
    private val meshes: List<URuntimeMesh> by lazy { createMeshes(depth, thickness) }

    fun obtainMesh(neighborhood: Neighborhood): URuntimeMesh = meshes[neighborhood.bits]
}

class TrenchSystems : SimpleObjectRegistry<TrenchSystem>()

object DefaultTrenchSystem : TrenchSystem(depth = 0.75f, thickness = 0.25f)

object Asset_Trench : CompositeAsset({
    runtimeMeshComponent {
        transform(translation = vec(-50.0f, -50.0f, 0.0f))
        TrenchParameter.bind { (system, neighborhood) ->
            runtimeMesh = system.obtainMesh(neighborhood)
        }
        MaterialAsset.bind { ref ->
            materials[0] = ref?.let { load(it) }
        }
    }
}) {
    object TrenchParameter : CompositeParameter<Pair<TrenchSystem, Neighborhood>>
}

var TrenchFloor.trenchParameter: Pair<TrenchSystem, Neighborhood> by attribute(Pair(DefaultTrenchSystem, Neighborhood(0)))

open class TrenchFloor(initializer: Initializer) : CellFloor(initializer) {
    init {
        asset = Asset_Trench
        assetParameterBindings = bindAssetParameters(
                Asset_Trench.TrenchParameter boundTo TrenchFloor::trenchParameter,
                MaterialAsset boundTo TrenchFloor::materialAsset
        )
    }
}

open class TrenchBorder(initializer: Initializer) : Prop(initializer) {
    init {
        groundMovementBlocking = BlockingValue {
            when (orientation) {
                RayOrientation.Inbound -> 0.0f
                RayOrientation.Outbound -> 1.0f
                else -> 0.0f
            }
        }
    }
}

fun World.createTrench(
        system: TrenchSystem,
        positions: Set<Vec2i>,
        materialFn: (Vec2i) -> UnrealRef<UMaterialInterface>
) {
    for (position in positions) {
        addProp(::TrenchFloor, position).apply {
            trenchParameter = Pair(system, Neighborhood.create(position, positions))
            materialAsset = materialFn(position)
        }
        for (heading in Heading.values()) {
            if (position + heading.delta !in positions) {
                addProp(::TrenchBorder, position, placement = PropPlacement.Border(heading))
            }
        }
    }
}
