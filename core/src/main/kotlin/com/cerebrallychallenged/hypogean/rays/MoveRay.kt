package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.readVec2f
import com.cerebrallychallenged.jun.math.geo.readVec2i
import com.cerebrallychallenged.jun.math.geo.writeVec2f
import com.cerebrallychallenged.jun.math.geo.writeVec2i
import com.cerebrallychallenged.jun.stream.readList
import com.cerebrallychallenged.jun.stream.writeList
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException

class MoveRay(val internalSegments: List<InternalSegment>) {
    data class InternalSegment(val target: Vec2i, val projectedPoint: Vec2f, val blockers: List<Blocker>)

    val blockers: Sequence<Blocker>
        get() = internalSegments.asSequence().flatMap { it.blockers }

    internal fun segments(queryParameters: QueryParameters): Sequence<MoveSegment> = internalSegments.asSequence().map {
            (target, projectedPoint, blockers) ->
        MoveSegment(
                queryParameters.cell(target),
                queryParameters.sourcePosition + projectedPoint,
                blockers.map { it.createDeferred(queryParameters) }
        )
    }

    internal fun deferredBlockers(queryParameters: QueryParameters): Sequence<DeferredBlocker> =
            blockers.map { it.createDeferred(queryParameters) }

//    internal fun blockersWithValues(
//            queryParameters: QueryParameters,
//            query: RaysQuery,
//            sourcePosition: Vec2i,
//            obtainValue: Blocker.() -> Float
//    ): Sequence<BlockerWithValue> = blockers.mapNotNull { blocker ->
//        val entity = (
//                if (blocker is CellBlocker) {
//                    val cell = blocker.identifyCell(queryParameters)
//                    cell?.presentActor ?: cell
//                } else null
//        ) ?: return@mapNotNull null
//        BlockerWithValue(entity, sourcePosition + it.relativePosition, it.obtainValue())
//    }

    internal fun blockerValues(obtainValue: Blocker.() -> Float): Sequence<Float> = blockers.map(obtainValue)

//    internal fun blockers(query: RaysQuery): Sequence<LocatedEntity> = blockers.mapNotNull(query::identifyEntity)

    internal fun cellBlockers(queryParameters: QueryParameters): Sequence<Cell> =
            blockers.filterIsInstance<CellBlocker>().mapNotNull { it.identifyCell(queryParameters) }

    override fun toString(): String =
            "MoveRay:${if (blockers.none()) " empty" else blockers.joinToString(prefix="\n- ", separator="\n- ")}"
}

internal fun DataOutputStream.writeMoveRay(moveRay: MoveRay) {
    writeList(moveRay.internalSegments) { (target, projectedPoint, blockers) ->
        writeVec2i(target)
        writeVec2f(projectedPoint)
        writeList(blockers) { blocker ->
            writeShort(blocker.index)
        }
    }
}

internal fun DataInputStream.readMoveRay(blockers: List<Blocker>): MoveRay {
    return try {
        MoveRay(readList {
            MoveRay.InternalSegment(
                    readVec2i(),
                    readVec2f(),
                    readList {
                        blockers[readShort().toInt()]
                    }
            )
        })
    } catch (e: IndexOutOfBoundsException) {
        throw IOException(e)
    }
}
