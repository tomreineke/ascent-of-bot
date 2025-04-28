package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.readVec2i
import com.cerebrallychallenged.jun.math.geo.writeVec2i
import com.cerebrallychallenged.jun.stream.readEnum
import com.cerebrallychallenged.jun.stream.writeEnum
import java.io.DataInputStream
import java.io.DataOutputStream

sealed class Blocker {
    abstract val index: Int

    abstract val relativePosition: Vec2i

    internal abstract fun createDeferred(queryParameters: QueryParameters): DeferredBlocker

    abstract fun computeValue(parameters: QueryParameters): Float

    abstract fun identifyEntities(queryParameters: QueryParameters): List<LocatedEntity>
}

data class CellBlocker(override val index: Int, override val relativePosition: Vec2i) : Blocker() {
    override fun computeValue(parameters: QueryParameters): Float = parameters.cellValue(relativePosition)

    override fun createDeferred(queryParameters: QueryParameters): DeferredBlocker =
            DeferredCellBlocker(queryParameters, relativePosition)

    fun identifyCell(queryParameters: QueryParameters): Cell? = queryParameters.cell(relativePosition)

    override fun identifyEntities(queryParameters: QueryParameters): List<LocatedEntity> {
        val cell = identifyCell(queryParameters) ?: return listOf()
        return queryParameters.blockerValueExtractor.identifyCellEntities(cell, queryParameters.actingSubject).toList()
    }

    override fun toString(): String = "Cell at $relativePosition"
}

data class BorderBlocker(
        override val index: Int,
        override val relativePosition: Vec2i,
        val heading: Heading
) : Blocker() {
    override fun computeValue(parameters: QueryParameters): Float =
            parameters.doubleSidedBorderValue(relativePosition, heading)

    override fun createDeferred(queryParameters: QueryParameters): DeferredBlocker =
            DeferredBorderBlocker(queryParameters, relativePosition, heading)

    override fun identifyEntities(queryParameters: QueryParameters): List<LocatedEntity> = listOf()

    override fun toString(): String = "Wall $heading of $relativePosition"
}

fun DataOutputStream.writeBlocker(blocker: Blocker) {
    writeShort(blocker.index)
    writeVec2i(blocker.relativePosition)
    when (blocker) {
        is CellBlocker -> writeBoolean(false)
        is BorderBlocker -> {
            writeBoolean(true)
            writeEnum(blocker.heading)
        }
    }
}

fun DataInputStream.readBlocker(): Blocker {
    val index = readShort().toInt()
    val relativePosition = readVec2i()
    val isWallBlocker = readBoolean()
    return if (isWallBlocker) {
        val heading = readEnum<Heading>()
        BorderBlocker(index, relativePosition, heading)
    } else {
        CellBlocker(index, relativePosition)
    }
}
