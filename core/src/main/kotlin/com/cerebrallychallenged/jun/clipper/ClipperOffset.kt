package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface ClipperOffset {
    companion object {
        fun makeShared(miterLimit: Double = 2.0, roundPrecision: Double = 0.25): TSharedRef<ClipperOffset> =
                makeSharedOfClipperOffset(miterLimit, roundPrecision).wrapSharedRef()
    }
}

fun AnyRef<ClipperOffset>.addPath(path: AnyRef<Path>, joinType: JoinType, endType: EndType) {
    addPath(directPtr, path.directPtr, joinType.ordinal, endType.ordinal)
}

fun AnyRef<ClipperOffset>.addPaths(paths: AnyRef<Paths>, joinType: JoinType, endType: EndType) {
    addPaths(directPtr, paths.directPtr, joinType.ordinal, endType.ordinal)
}

var AnyRef<ClipperOffset>.arcTolerance: Double
    get() = getArcTolerance(directPtr)
    set(value) {
        setArcTolerance(directPtr, value)
    }

fun AnyRef<ClipperOffset>.clear() {
    clear(directPtr)
}

fun AnyRef<ClipperOffset>.execute(solution: AnyRef<Paths>, delta: Double) {
    executeForPaths(directPtr, solution.directPtr, delta)
}

@JvmName("executeForPolyTree")
fun AnyRef<ClipperOffset>.execute(solution: AnyRef<PolyTree>, delta: Double) {
    executeForPolyTree(directPtr, solution.directPtr, delta)
}

@Convenience
fun AnyRef<ClipperOffset>.execute(delta: Double): TSharedRef<PolyTree> = PolyTree.makeShared().also { execute(it, delta) }

var AnyRef<ClipperOffset>.miterLimit: Double
    get() = getMiterLimit(directPtr)
    set(value) {
        setMiterLimit(directPtr, value)
    }

private external fun addPath(directPtr: CPointer, pathDirectPtr: CPointer, joinType: Int, endType: Int)

private external fun addPaths(directPtr: CPointer, pathsDirectPtr: CPointer, joinType: Int, endType: Int)

private external fun clear(directPtr: CPointer)

private external fun executeForPaths(directPtr: CPointer, solutionDirectPtr: CPointer, delta: Double)

private external fun executeForPolyTree(directPtr: CPointer, solutionDirectPtr: CPointer, delta: Double)

private external fun getArcTolerance(directPtr: CPointer): Double

private external fun getMiterLimit(directPtr: CPointer): Double

private external fun makeSharedOfClipperOffset(miterLimit: Double, roundPrecision: Double): CPointer

private external fun setArcTolerance(directPtr: CPointer, value: Double)

private external fun setMiterLimit(directPtr: CPointer, value: Double)