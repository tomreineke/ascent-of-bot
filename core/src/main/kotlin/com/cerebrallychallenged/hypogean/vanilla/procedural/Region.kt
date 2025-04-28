@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.procedural

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.singleInitiativeCost
import com.cerebrallychallenged.hypogean.view.Material
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.asset.CompositeParameter
import com.cerebrallychallenged.jun.asset.runtimeMeshComponent
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.Vec3i
import com.cerebrallychallenged.jun.math.geo.polar
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.rmc.createMeshData
import com.cerebrallychallenged.jun.rmc.createSection
import com.cerebrallychallenged.jun.unreal.ECollisionEnabled
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

private class Vertex(val index: Int, val pos: Vec2f, val uv: Vec2f, val debugText: String = "")

private class CornerSquare(
        val outerCorner: Vertex,
        val innerCorner: Vertex,
        val arc: List<Vertex>,
) {
    val front = arc.first()
    val back = arc.last()
}

private fun createMeshes(thickness: Float): List<URuntimeMesh> {
    val arcSideCount = 8

    val angle = Angle.DEGREE_90 / arcSideCount
    val arcPosArray = Array(arcSideCount + 1) { polar(thickness, angle * it) }
    val thicknessX = Vec2f.UNIT_X * thickness
    val thicknessY = Vec2f.UNIT_Y * thickness
    val thicknessXY = thicknessX + thicknessY
    val invArcPosArray = Array(arcSideCount + 1) { (thicknessXY - arcPosArray[it]).yx }

    val texStep = 1.0f / arcSideCount
    val texVArray = FloatArray(arcSideCount + 1) { texStep * it }

    val transforms = listOf<(Vec2f) -> Vec2f>(
            { vec(it.x - 50.0f, it.y - 50.0f) },
            { vec(50.0f - it.y, it.x - 50.0f) },
            { vec(50.0f - it.x, 50.0f - it.y) },
            { vec(it.y - 50.0f, 50.0f - it.x) }
    )

    val defaultMaterial = UMaterial.getDefaultMaterial(EMaterialDomain.Surface)
    val meshSectionProperties = FRuntimeMeshSectionProperties.makeShared()
    meshSectionProperties.castsShadow = false
    meshSectionProperties.isVisible = true
    meshSectionProperties.materialSlot = 0

    val list = (0 until 256).map { bits ->
        val neighborhood = Neighborhood(bits)
        if (neighborhood.unify() != neighborhood) return@map null
        val runtimeMesh = newObject<URuntimeMesh>()
        val provider = newObject<URuntimeMeshProviderStatic>()
        runtimeMesh.initialize(provider)
        provider.setupMaterialSlot(0, "Material", defaultMaterial)

        val vertices = mutableListOf<Vertex>()
        fun addVertex(pos: Vec2f, texU: Float, texV: Float, debugText: String = ""): Vertex =
                Vertex(vertices.size, pos, vec(texU, texV), debugText).also { vertices.add(it) }

        val corners = (0..3).map { cornerIndex ->
            val backVec = Heading.values()[cornerIndex].opposite().delta
            val frontVec = backVec.turnClockwise()
            val hasBackBorder = !neighborhood[backVec]
            val hasFrontBorder = !neighborhood[frontVec]
            val hasCornerBorder = !neighborhood[backVec + frontVec]
            val transform = transforms[cornerIndex]

            // texture coordinate u:
            // 0.0 outside region
            // 1.0 inside region

            CornerSquare(
                    outerCorner = addVertex(transform(Vec2f.ZERO), if (hasCornerBorder) 0.0f else 1.0f, 0.0f, "$cornerIndex|O"),
                    innerCorner = addVertex(transform(thicknessXY), 1.0f, 0.0f, "$cornerIndex|I"),
                    arc = when {
                        hasCornerBorder && !hasFrontBorder && !hasBackBorder -> arcPosArray.mapIndexed { i, pos ->
                            addVertex(transform(pos), 1.0f, texVArray[i], "$cornerIndex|A$i")
                        }
                        hasFrontBorder && hasBackBorder -> invArcPosArray.mapIndexed { i, pos ->
                            addVertex(transform(pos), 0.0f, texVArray[i], "$cornerIndex|A$i")
                        }
                        else -> listOf(
                                addVertex(transform(thicknessX), if (hasFrontBorder) 0.0f else 1.0f, 0.0f, "$cornerIndex|F"),
                                addVertex(transform(thicknessY), if (hasBackBorder) 0.0f else 1.0f, 0.0f, "$cornerIndex|B")
                        )
                    }
            )
        }

//        GlobalScope.launch {
//            File("""D:\Projects\hypogean2\tools\examples\regionmesh\mesh$bits.png""").writeImage(buildImage(vec(600.0, 600.0)) {
//                fun tr(v: Vec2f): Vec2f = (v + Vec2f.ONE * 50.0f) * 6.0f
//                fill = Color.WHITE
//                fillRect(0.0, 0.0, 600.0, 600.0)
//                for (vertex in vertices) {
//                    val uv = vertex.uv
//                    val color = Color.color(uv.x.toDouble(), 0.0, vertex.uv.y.toDouble())
//                    val pos = tr(vertex.pos)
//                    drawDot(pos, 4.0f, color, color)
//                    strokeText(vertex.debugText, pos)
//                }
//            })
//        }

        val meshData = meshSectionProperties.createMeshData(vertices.size)
        meshData.updatePositions { position ->
            for ((i, vertex) in vertices.withIndex()) {
                position[i] = vertex.pos.append(0.0f)
            }
        }
        meshData.updateTangents { tangent, normal ->
            for (i in vertices.indices) {
                tangent[i] = Vec3f.UNIT_X
                normal[i] = Vec3f.UNIT_Z
            }
        }
        meshData.updateColors { color ->
            for (i in vertices.indices) {
                color[i] = FColor.White
            }
        }
        meshData.updateTexCoords { channel ->
            val uv = channel[0]
            for ((i, vertex) in vertices.withIndex()) {
                uv[i] = vertex.uv
            }
        }
        val triangleList = mutableListOf<Vec3i>()
        fun addTriangle(v0: Vertex, v1: Vertex, v2: Vertex) {
            triangleList.add(vec(v0.index, v1.index, v2.index))
        }
        addTriangle(corners[0].innerCorner, corners[2].innerCorner, corners[1].innerCorner)
        addTriangle(corners[0].innerCorner, corners[3].innerCorner, corners[2].innerCorner)
        for (i in 0..3) {
            val first = corners[i]
            val second = corners[(i + 1) and 0b11]
            addTriangle(first.front, first.innerCorner, second.back)
            addTriangle(first.innerCorner, second.innerCorner, second.back)
        }
        for (corner in corners) {
            for ((a, b) in corner.arc.windowed(2)) {
                addTriangle(corner.outerCorner, b, a)
                addTriangle(corner.innerCorner, a, b)
            }
        }
        meshData.updateTriangles(triangleList.size) { triangles ->
            for ((i, triangle) in triangleList.withIndex()) {
                triangles[i] = triangle
            }
        }
        provider.createSection(0, 0, meshSectionProperties, meshData)

        runtimeMesh
    }
    return list.mapIndexed { index, mesh ->
        mesh ?: list[Neighborhood(index).unify().bits] ?: error("Missing mesh for unified neighborhood ")
    }
}

abstract class RegionSystem(thickness: Float) {
    private val meshes: List<URuntimeMesh> by lazy { createMeshes(thickness) }

    fun obtainMesh(neighborhood: Neighborhood): URuntimeMesh = meshes[neighborhood.bits]
}

object DefaultRegionSystem : RegionSystem(30.0f)

object Asset_RegionTile : CompositeAsset({
    runtimeMeshComponent {
        collisionEnabled = ECollisionEnabled.NoCollision
        RegionParameter.bind { (regionSystem, neighborhood) ->
            runtimeMesh = regionSystem.obtainMesh(neighborhood)
//            worldLocation = (pos * 100.0f).append(0.1f)
        }
        Material.bind {
            materials[0] = it
        }
    }
}) {
    object RegionParameter : CompositeParameter<Pair<RegionSystem, Neighborhood>>
}

suspend fun createActionRegionVisualization(
        assetLibrary: AssetLibrary,
        regionSystem: RegionSystem,
        actions: ActionTable,
        material: UMaterialInterface,
        quickMaterial: UMaterialInterface? = null,
        includedStartPosition: Vec2i? = null
): List<CompositeNode> {
    val nodes = mutableListOf<CompositeNode>()
    val positions = mutableSetOf<Vec2i>()
    val quickPositions = mutableSetOf<Vec2i>()
    for ((target, table) in actions.groupedByTarget) {
        if (target is LocatedEntity && table.hasInstances()) {
            val position = target.position
            positions.add(position)
            if (quickMaterial != null && table.singleInitiativeCost == InitiativeCost.KeepTurn) {
                quickPositions.add(position)
            }
        }
    }
    includedStartPosition?.let { positions.add(it) }
    createRegionVisualization(assetLibrary, regionSystem, material, 0.1f, positions, nodes)
    if (quickMaterial != null) {
        includedStartPosition?.let { quickPositions.add(it) }
        createRegionVisualization(assetLibrary, regionSystem, quickMaterial, 0.2f, quickPositions, nodes)
    }
    return nodes
}

suspend fun createRegionVisualization(
    assetLibrary: AssetLibrary,
    regionSystem: RegionSystem,
    material: UMaterialInterface,
    zPos: Float,
    positions: Set<Vec2i>,
    resultNodes: MutableCollection<in CompositeNode>
) {
    positions.mapTo(resultNodes) { pos ->
        val neighborhood = Neighborhood.create(pos, positions)
        Asset_RegionTile.create(assetLibrary).apply {
            parameters[Asset_RegionTile.RegionParameter] = Pair(regionSystem, neighborhood)
            parameters[Material] = material
            worldLocation = (pos * 100.0f).append(zPos)
        }
    }
}
