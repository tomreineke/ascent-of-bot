package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.readVec2f
import com.cerebrallychallenged.jun.math.geo.sumBy
import com.cerebrallychallenged.jun.math.geo.writeVec2f
import com.cerebrallychallenged.jun.stream.readList
import com.cerebrallychallenged.jun.stream.writeList
import com.cerebrallychallenged.jun.util.chunkedConsecutive
import com.cerebrallychallenged.jun.util.minAllBy
import com.cerebrallychallenged.jun.util.sumByFloat
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

class ExposureRay(internal val sections: List<Section>, internal val entries: List<Entry>) {
    /**
     * Every cell is divided into a number of convex sections,
     * which are equivalence classes of points hidden by the same subset of blockers.
     */
    class Section(val area: Float, val centroid: Vec2f)

    /**
     * Every blocker hides some subset of sections whose indices are consecutive.
     */
    class Entry(val blocker: Blocker, val sectionIndices: IntRange)

    @Suppress("ArrayInDataClass")
    internal data class ExposedOpacityPair(val exposed: BooleanArray, val opacity: FloatArray)

    private inline fun computeExposedOpacityPair(
            strength: Float,
            obtainValue: Blocker.() -> Float
    ): ExposedOpacityPair? {
        val exposed = BooleanArray(sections.size) { true }
        val opacity = FloatArray(sections.size)
        var exposedCount = sections.size
        for (entry in entries) {
            val value = entry.blocker.obtainValue()
            for (sectionIndex in entry.sectionIndices) {
                if (exposed[sectionIndex]) {
                    opacity[sectionIndex] += value
                    if (opacity[sectionIndex] > strength) {
                        exposed[sectionIndex] = false
                        --exposedCount
                        if (exposedCount == 0) {
                            return null
                        }
                    }
                }
            }
        }
        return ExposedOpacityPair(exposed, opacity)
    }

    internal inline fun computeExposure(strength: Float, obtainValue: Blocker.() -> Float): Float {
        val (exposed, _) = computeExposedOpacityPair(strength, obtainValue) ?: return 0.0f
        var exposure = 1.0f
        for (sectionIndex in exposed.indices) {
            if (!exposed[sectionIndex]) {
                exposure -= sections[sectionIndex].area
            }
        }
        return exposure
    }

    internal data class SectionsWithArea(val sections: List<Section>, val totalArea: Float)

    internal inline fun computeCentroid(strength: Float, obtainValue: Blocker.() -> Float): Vec2f? {
        val (exposed, opacity) = computeExposedOpacityPair(strength, obtainValue) ?: return null

        // Find the largest (resp. total area) list of consecutive, exposed sections of minimal opacity.
        val (sections, totalArea) = sections.indices.asSequence()
                .filter { exposed[it] }
                .minAllBy { opacity[it] }.asSequence()
                .chunkedConsecutive()
                .map {
                    val sections = it.map(sections::get)
                    SectionsWithArea(sections, sections.sumByFloat { section -> section.area })
                }
                .maxByOrNull { it.totalArea }!!
        val weightedCentroid: (Section) -> Vec2f = { section -> section.centroid * section.area }
        return sections.sumBy(weightedCentroid) / totalArea
    }
}

internal fun DataOutputStream.writeExposureRay(exposureRay: ExposureRay) {
    fun DataOutputStream.writeSection(section: ExposureRay.Section) {
        writeFloat(section.area)
        writeVec2f(section.centroid)
    }
    fun DataOutputStream.writeEntry(entry: ExposureRay.Entry) {
        writeShort(entry.blocker.index)
        writeShort(entry.sectionIndices.first)
        writeShort(entry.sectionIndices.last + 1)
    }
    writeList(exposureRay.sections) { writeSection(it) }
    writeList(exposureRay.entries) { writeEntry(it) }
}

internal fun DataInputStream.readExposureRay(blockers: List<Blocker>): ExposureRay {
    fun DataInputStream.readSection(): ExposureRay.Section =
            ExposureRay.Section(readFloat(), readVec2f())
    fun DataInputStream.readEntry(): ExposureRay.Entry = ExposureRay.Entry(
            blockers[readShort().toInt()],
            readShort().toInt() until readShort().toInt()
    )
    return try {
        ExposureRay(
                readList { readSection() },
                readList { readEntry() }
        )
    } catch (e: IndexOutOfBoundsException) {
        throw IOException(e)
    }
}