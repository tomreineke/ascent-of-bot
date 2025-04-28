package com.cerebrallychallenged.hypogean.view.map.voxel

import com.cerebrallychallenged.hypogean.view.map.voxel.VoxelManager.Voxel
import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.rmc.MeshData
import com.cerebrallychallenged.jun.rmc.RectangularPatch
import com.cerebrallychallenged.jun.rmc.createMeshData
import com.cerebrallychallenged.jun.rmc.createSection
import com.cerebrallychallenged.jun.unreal.color.FColor
import com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionProperties
import com.cerebrallychallenged.jun.unreal.rmc.castsShadow
import com.cerebrallychallenged.jun.unreal.rmc.isVisible
import com.cerebrallychallenged.jun.unreal.rmc.materialSlot
import com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStatic
import com.cerebrallychallenged.jun.util.buffers.set
import java.util.Random
import kotlin.math.min

internal fun createVoxelMesh(
    provider: URuntimeMeshProviderStatic,
    voxel: Voxel,
    neighborVoxels: Array<Voxel?>,
    position: Vec2i
) {
    val sectionIdIterator = generateSequence(0) { it + 1 }.iterator()
    createSide(provider, sectionIdIterator, Vec3f.UNIT_Y, Vec3f.UNIT_X, voxel, neighborVoxels[0], position)
    createSide(provider, sectionIdIterator, Vec3f.UNIT_X, -Vec3f.UNIT_Y, voxel, neighborVoxels[1], position)
    createSide(provider, sectionIdIterator, -Vec3f.UNIT_Y, -Vec3f.UNIT_X, voxel, neighborVoxels[2], position)
    createSide(provider, sectionIdIterator, -Vec3f.UNIT_X, Vec3f.UNIT_Y, voxel, neighborVoxels[3], position)
    createTop(provider, sectionIdIterator, voxel)
}

private fun createSide(
    provider: URuntimeMeshProviderStatic,
    sectionIdIterator: Iterator<Int>,
    mainNormal: Vec3f,
    mainTangent: Vec3f,
    voxel: Voxel,
    neighborVoxel: Voxel?,
    position: Vec2i
) {
    val height = voxel.height
    val neighborHeight = neighborVoxel?.height ?: 0.0f
    val commonHeight = min(height, neighborHeight)
    val commonSectionId = sectionIdIterator.next()
    if (commonHeight > 0.0f) {
        createPatch(provider, commonSectionId, 2, 2) { patch, meshData ->
            meshData.updatePositions { position ->
                patch.forEachVertex { index, i, j ->
                    position[index] = (
                        mainNormal * 50.0f
                            + mainTangent * (i - 0.5f) * 100.0f
                            + Vec3f.UNIT_Z * (1 - j) * commonHeight * 100.0f
                    )
                }
            }
            meshData.updateTangents { tangent, normal ->
                patch.forEachVertex { index, _, _ ->
                    normal[index] = mainNormal
                    tangent[index] = mainTangent
                }
            }
            meshData.updateColors { color ->
                patch.forEachVertex { index, _, _ ->
                    color[index] = FColor.White
                }
            }
            meshData.updateTexCoords { channel ->
                val uv = channel[0]
                patch.forEachVertex { index, i, j ->
                    uv[index] = vec(i, j).toFloat()
                }
            }
        }
    }
    val deltaHeight = height - commonHeight
    val random = Random(((position.x * 31 + position.y) * 31 + commonSectionId).toLong())
    val roughness = voxel.blockAsset.roughness
    if (deltaHeight > 0.0) {
        val subdivisionCount = voxel.blockAsset.subdivisionCount
        val sectionCountPerMeter = subdivisionCount + 1
        val iCount = subdivisionCount + 2
        val iSectionLength = 1.0f / sectionCountPerMeter

        val jSectionCount = (sectionCountPerMeter * deltaHeight).floorToInt()
        val jCount = jSectionCount + 1
        val jSectionLength = deltaHeight / jSectionCount

        createPatch(provider, sectionIdIterator.next(), iCount, jCount) { patch, meshData ->
            meshData.updatePositions { position ->
                patch.forEachVertex { index, i, j ->
                    val delta = if (i != 0 && i != iCount - 1 && j != 0 && j != jCount - 1) {
                        (random.nextGaussian() * roughness).toFloat()
                    } else {
                        0.0f
                    }
                    position[index] = (
                            mainNormal * 50.0f
                                    + mainTangent * (i * iSectionLength * 100.0f - 50.0f)
                                    + mainNormal * delta
                                    + Vec3f.UNIT_Z * (commonHeight + (jCount - j - 1) * jSectionLength) * 100.0f
                            )
                }
            }
            meshData.updateTangents { tangent, normal ->
                patch.forEachVertex { index, _, _ ->
                    normal[index] = mainNormal
                    tangent[index] = mainTangent
                }
            }
            meshData.updateColors { color ->
                patch.forEachVertex { index, _, _ ->
                    color[index] = FColor.White
                }
            }
            meshData.updateTexCoords { channel ->
                val uv = channel[0]
                patch.forEachVertex { index, i, j ->
                    uv[index] = vec(i / (iCount - 1.0f), j / (jCount - 1.0f))
                }
            }
        }
    }
}

private fun createTop(provider: URuntimeMeshProviderStatic, sectionIdIterator: Iterator<Int>, voxel: Voxel) {
    val height = voxel.height
    createPatch(provider, sectionIdIterator.next(), 2, 2) { patch, meshData ->
        meshData.updatePositions { position ->
            patch.forEachVertex { index, i, j ->
                position[index] = vec(100.0f * i - 50.0f, 100.0f * j - 50.0f, height * 100.0f)
            }
        }
        meshData.updateTangents { tangent, normal ->
            patch.forEachVertex { index, _, _ ->
                normal[index] = Vec3f.UNIT_Z
                tangent[index] = Vec3f.UNIT_X
            }
        }
        meshData.updateColors { color ->
            patch.forEachVertex { index, _, _ ->
                color[index] = FColor.White
            }
        }
        meshData.updateTexCoords { channel ->
            val uv = channel[0]
            patch.forEachVertex { index, i, j ->
                uv[index] = vec(i, j).toFloat()
            }
        }
    }
}

private fun createPatch(provider: URuntimeMeshProviderStatic, sectionId: Int, iCount: Int, jCount: Int, f: (RectangularPatch, MeshData) -> Unit) {
    val meshSectionProperties = FRuntimeMeshSectionProperties.makeShared()
    meshSectionProperties.castsShadow = true
    meshSectionProperties.isVisible = true
    meshSectionProperties.materialSlot = 0
    val meshData = meshSectionProperties.createMeshData(iCount * jCount)
    val rectangularPatch = RectangularPatch(iCount, jCount)
    f(rectangularPatch, meshData)
    rectangularPatch.installTriangles(meshData)
    provider.createSection(0, sectionId, meshSectionProperties, meshData)
}
