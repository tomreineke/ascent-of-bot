package com.cerebrallychallenged.jun.rmc

import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.util.buffers.set

class RectangularPatch(val iCount: Int, val jCount: Int) {
    fun vertexIndex(i: Int, j: Int): Int = iCount * j + i

    fun installTriangles(meshData: MeshData) {
        meshData.updateTriangles(2 * (iCount - 1) * (jCount - 1)) { triangle ->
            var k = 0
            for (j in 0 until jCount - 1) {
                for (i in 0 until iCount - 1) {
                    val index = vertexIndex(i, j)
                    triangle[2 * k] = vec(index, index + iCount, index + 1)
                    triangle[2 * k + 1] = vec(index + iCount + 1, index + 1, index + iCount)
                    ++k
                }
            }
        }
    }

    inline fun forEachVertex(f: (index: Int, i: Int, j: Int) -> Unit) {
        for (j in 0 until jCount) {
            for (i in 0 until iCount) {
                f(vertexIndex(i, j), i, j)
            }
        }
    }
}
