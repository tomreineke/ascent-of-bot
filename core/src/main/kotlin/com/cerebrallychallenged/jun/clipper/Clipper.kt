package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface Clipper : ClipperBase {
    companion object {
        fun makeShared(initOptions: Set<InitOptions> = setOf()): TSharedRef<Clipper> =
                makeSharedOfClipper(initOptions.toMagic()).wrapSharedRef()
    }
}

fun AnyRef<Clipper>.execute(
        clipType: ClipType,
        solution: AnyRef<Paths>,
        subjFillType: PolyFillType = PolyFillType.EvenOdd,
        clipFillType: PolyFillType = PolyFillType.EvenOdd
): Boolean =
        executeForPaths(directPtr, clipType.ordinal, solution.directPtr, subjFillType.ordinal, clipFillType.ordinal)

@JvmName("executeForPolyTree")
fun AnyRef<Clipper>.execute(
        clipType: ClipType,
        solution: AnyRef<PolyTree>,
        subjFillType: PolyFillType = PolyFillType.EvenOdd,
        clipFillType: PolyFillType = PolyFillType.EvenOdd
): Boolean =
        executeForPolyTree(directPtr, clipType.ordinal, solution.directPtr, subjFillType.ordinal, clipFillType.ordinal)

@Convenience
fun AnyRef<Clipper>.execute(
        clipType: ClipType,
        subjFillType: PolyFillType = PolyFillType.EvenOdd,
        clipFillType: PolyFillType = PolyFillType.EvenOdd
): TSharedRef<Paths> = Paths.makeShared().also { execute(clipType, it, subjFillType, clipFillType) }

var AnyRef<Clipper>.reverseSolution: Boolean
    get() = getReverseSolution(directPtr)
    set(value) {
        setReverseSolution(directPtr, value)
    }

var AnyRef<Clipper>.strictlySimple: Boolean
    get() = getStrictlySimple(directPtr)
    set(value) {
        setStrictlySimple(directPtr, value)
    }

private external fun executeForPaths(
        directPtr: CPointer,
        clipType: Int,
        solutionDirectPtr: CPointer,
        subjFillType: Int,
        clipFillType: Int
): Boolean

private external fun executeForPolyTree(
        directPtr: CPointer,
        clipType: Int,
        solutionDirectPtr: CPointer,
        subjFillType: Int,
        clipFillType: Int
): Boolean

private external fun getReverseSolution(directPtr: CPointer): Boolean

private external fun getStrictlySimple(directPtr: CPointer): Boolean

private external fun makeSharedOfClipper(initOptions: Int): CPointer

private external fun setReverseSolution(directPtr: CPointer, value: Boolean)

private external fun setStrictlySimple(directPtr: CPointer, value: Boolean)