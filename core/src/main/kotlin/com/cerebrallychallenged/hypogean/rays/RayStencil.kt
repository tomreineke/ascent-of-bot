package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.floorMod
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.stream.readList
import com.cerebrallychallenged.jun.stream.writeList
import com.cerebrallychallenged.jun.util.ArrayND
import com.cerebrallychallenged.jun.util.readArray2
import com.cerebrallychallenged.jun.util.writeArray2
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.math.roundToInt

class RayStencil(
        internal val blockers: List<Blocker>,
        internal val moveRays: List<ArrayND<Vec2i, MoveRay>>,
        internal val exposureRays: ArrayND<Vec2i, ExposureRay>,
        internal val hitRays: List<HitRay>
) {
    companion object {
        const val COMPRESSED = false
    }

    val horizontalRadius: Int = exposureRays.indexBounds.size.x

    private val hitRayAngleStep = Angle.DEGREE_360 / hitRays.size.toFloat()

    internal fun moveRay(relativePosition: Vec2i, moverSize: Int): MoveRay = moveRays[moverSize - 1][relativePosition]

    internal fun exposureRay(relativePosition: Vec2i): ExposureRay = exposureRays[relativePosition]

    fun exposureRaySectionAreas(): Sequence<Float>
            = exposureRays.values.flatMap { ray -> ray.sections.asSequence().map { it.area } }

    internal fun hitRay(angle: Angle): HitRay = hitRays[(angle / hitRayAngleStep).roundToInt().floorMod(hitRays.size)]
}

fun DataOutputStream.writeRayStencil(rayStencil: RayStencil) {
    writeList(rayStencil.blockers) { writeBlocker(it) }
    writeList(rayStencil.moveRays) { array -> writeArray2(array) { writeMoveRay(it) } }
    writeArray2(rayStencil.exposureRays) { writeExposureRay(it) }
    writeList(rayStencil.hitRays) { writeHitRay(it) }
}

internal fun DataInputStream.readRayStencil(): RayStencil {
    val blockers = readList { readBlocker() }
    return RayStencil(
            blockers,
            readList { readArray2 { readMoveRay(blockers) } },
            readArray2 { readExposureRay(blockers) },
            readList { readHitRay(blockers) }
    )
}