package com.cerebrallychallenged.hypogean.vanilla.procedural

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodec
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.boundaryPoints

private val NEIGHBORHOOD_MASKS =
        Bounds.centered(Vec2i.ZERO, Vec2i.ONE).boundaryPoints.withIndex().associate { (index, vector) ->
            vector to (1 shl index)
        }

@JvmInline
value class Neighborhood(val bits: Int) {
    companion object {
        fun create(p: Vec2i, set: Set<Vec2i>): Neighborhood {
            var bits = 0
            for ((v, mask) in NEIGHBORHOOD_MASKS) {
                if (p + v in set) {
                    bits = bits or mask
                }
            }
            return Neighborhood(bits)
        }
    }

    operator fun get(neighbor: Vec2i): Boolean = neighbor.isZero || (bits and NEIGHBORHOOD_MASKS.getValue(neighbor) != 0)

    fun unify(): Neighborhood {
        var resultBits = bits
        val deltas = Heading.deltas
        for (i in 0..3) {
            val v1 = deltas[i]
            val v2 = deltas[(i + 1) and 3]
            if (!this[v1] || !this[v2]) {
                resultBits = resultBits and NEIGHBORHOOD_MASKS.getValue(v1 + v2).inv()
            }
        }
        return Neighborhood(resultBits)
    }
}

object NeighborhoodAttributeCodec : AttributeCodec<Neighborhood>
