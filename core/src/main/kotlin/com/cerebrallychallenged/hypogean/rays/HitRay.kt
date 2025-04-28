package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.cellFilling
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.readVec2f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.math.geo.writeVec2f
import com.cerebrallychallenged.jun.math.roundTowards
import com.cerebrallychallenged.jun.stream.readList
import com.cerebrallychallenged.jun.stream.writeList
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import kotlin.math.absoluteValue
import kotlin.math.max

sealed class HitResult {
    abstract val position: Vec2i

    abstract val position2f: Vec2f

    open val hitEntities: List<LocatedEntity>
        get() = listOf()

    data class NoHit(override val position: Vec2i, override val position2f: Vec2f) : HitResult()

    data class Hit(
        override val position: Vec2i,
        override val position2f: Vec2f,
        override val hitEntities: List<LocatedEntity>,
    ) : HitResult() {
        val position3f: Vec3f
            get() = position2f.append(hitEntities.maxOf { it.centerPoint.z })
    }

    data class EndOfRange(override val position: Vec2i, override val position2f: Vec2f) : HitResult()
}

class HitRay(internal val entries: List<Entry>) {
    class Entry(val blocker: Blocker, val hitPosition: Vec2f)

    internal fun computeHit(
            queryParameters: QueryParameters,
            sumThreshold: Float,
            maxThreshold: Float,
            maxDistance: Float,
            obtainValue: Blocker.() -> Float
    ): HitResult {
        var accumulatedSum = 0.0f
        var accumulatedMax = 0.0f
        var blockerCenter = queryParameters.sourcePosition
        for (entry in entries) {
            val value = entry.blocker.obtainValue()
            accumulatedSum += value
            accumulatedMax = max(accumulatedMax, value)
            val sourcePosition = queryParameters.sourcePosition
            val relativePosition = entry.blocker.relativePosition
            blockerCenter = sourcePosition + relativePosition
            when {
                accumulatedSum > sumThreshold || accumulatedMax > maxThreshold -> {
                    val hitEntities = entry.blocker.identifyEntities(queryParameters)
                    return if (hitEntities.isNotEmpty()) {
                        val (position, position2f) = if (hitEntities.any { (it as? Item)?.cellFilling == true }) {
                            val position2f = sourcePosition + entry.hitPosition
                            val cmp = relativePosition.x.absoluteValue.compareTo(relativePosition.y.absoluteValue)
                            Pair(
                                when {
                                    cmp < 0 -> {
                                        vec(blockerCenter.x, position2f.y.roundTowards(sourcePosition.y.toFloat()))
                                    }
                                    cmp > 0 -> {
                                        vec(position2f.x.roundTowards(sourcePosition.x.toFloat()), blockerCenter.y)
                                    }
                                    else -> {
                                        position2f.roundTowards(sourcePosition.toFloat())
                                    }
                                },
                                position2f
                            )
                        } else {
                            Pair(blockerCenter, blockerCenter.toFloat())
                        }
                        HitResult.Hit(position, position2f, hitEntities)
                    } else {
                        HitResult.NoHit(blockerCenter, blockerCenter.toFloat())
                    }
                }
                entry.hitPosition.length > maxDistance -> {
                    return HitResult.EndOfRange(blockerCenter, blockerCenter.toFloat())
                }
            }
        }
        // We hit nothing. Use the position where the ray left the area of potential blockers.
        return HitResult.NoHit(blockerCenter, blockerCenter.toFloat())
    }
}

internal fun DataOutputStream.writeHitRay(hitRay: HitRay) {
    fun DataOutputStream.writeHitRayEntry(entry: HitRay.Entry) {
        writeShort(entry.blocker.index)
        writeVec2f(entry.hitPosition)
    }

    writeList(hitRay.entries) { writeHitRayEntry(it) }
}

internal fun DataInputStream.readHitRay(blockers: List<Blocker>): HitRay {
    fun DataInputStream.readHitRayEntry(): HitRay.Entry
            = HitRay.Entry(blockers[readShort().toInt()], readVec2f())

    return try {
        HitRay(readList { readHitRayEntry() })
    } catch (e: IndexOutOfBoundsException) {
        throw IOException(e)
    }
}
