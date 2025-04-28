package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.util.CPointer

fun area(path: AnyRef<Path>): Double = area(path.directPtr)

fun cleanPolygon(inPoly: AnyRef<Path>, outPoly: AnyRef<Path> = inPoly, distance: Double = 1.415) {
    cleanPolygon(inPoly.directPtr, outPoly.directPtr, distance)
}

fun cleanPolygons(inPolys: AnyRef<Paths>, outPolys: AnyRef<Paths> = inPolys, distance: Double = 1.415) {
    cleanPolygons(inPolys.directPtr, outPolys.directPtr, distance)
}

/**
 * See [com.cerebrallychallenged.jun.clipper.closedPaths]
 */
fun closedPathsFromPolyTree(polyTree: AnyRef<PolyTree>, paths: AnyRef<Paths>) {
    closedPathsFromPolyTree(polyTree.directPtr, paths.directPtr)
}

fun minkowskiDiff(poly1: AnyRef<PolyTree>, poly2: AnyRef<PolyTree>, solution: AnyRef<PolyTree>) {
    minkowskiDiff(poly1.directPtr, poly2.directPtr, solution.directPtr)
}

fun minkowskiSum(poly1: AnyRef<PolyTree>, poly2: AnyRef<PolyTree>, solution: AnyRef<PolyTree>, pathIsClosed: Boolean) {
    minkowskiSum(poly1.directPtr, poly2.directPtr, solution.directPtr, pathIsClosed)
}

/**
 * See [com.cerebrallychallenged.jun.clipper.openPaths]
 */
fun openPathsFromPolyTree(polyTree: AnyRef<PolyTree>, paths: AnyRef<Paths>) {
    openPathsFromPolyTree(polyTree.directPtr, paths.directPtr)
}

fun orientation(poly: AnyRef<Path>): Boolean = orientation(poly.directPtr)

fun pointInPolygon(pt: Vec2i, poly: AnyRef<Path>): Int = pointInPolygon(pt, poly.directPtr)

fun polyTreeToPaths(polyTree: AnyRef<PolyTree>, paths: AnyRef<Paths>) {
    polyTreeToPaths(polyTree.directPtr, paths.directPtr)
}

fun reversePath(path: AnyRef<Path>) {
    reversePath(path.directPtr)
}

fun reversePaths(paths: AnyRef<Paths>) {
    reversePaths(paths.directPtr)
}

fun simplifyPolygon(inPoly: AnyRef<Path>, outPolys: AnyRef<Paths>, polyFillType: PolyFillType = PolyFillType.EvenOdd) {
    simplifyPolygon(inPoly.directPtr, outPolys.directPtr, polyFillType.ordinal)
}

fun simplifyPolygons(
        inPolys: AnyRef<Paths>,
        outPolys: AnyRef<Paths> = inPolys,
        polyFillType: PolyFillType = PolyFillType.EvenOdd
) {
    simplifyPolygons(inPolys.directPtr, outPolys.directPtr, polyFillType.ordinal)
}

private external fun area(pathDirectPtr: CPointer): Double

private external fun cleanPolygon(inPolyDirectPtr: CPointer, outPolyDirectPtr: CPointer, distance: Double)

private external fun cleanPolygons(inPolysDirectPtr: CPointer, outPolysDirectPtr: CPointer, distance: Double)

private external fun closedPathsFromPolyTree(polyTreeDirectPtr: CPointer, pathsDirectPtr: CPointer)

private external fun minkowskiDiff(poly1DirectPtr: CPointer, poly2DirectPtr: CPointer, solutionDirectPtr: CPointer)

private external fun minkowskiSum(
        poly1DirectPtr: CPointer,
        poly2DirectPtr: CPointer,
        solutionDirectPtr: CPointer,
        pathIsClosed: Boolean
)

private external fun openPathsFromPolyTree(polyTreeDirectPtr: CPointer, pathsDirectPtr: CPointer)

private external fun orientation(polyDirectPtr: CPointer): Boolean

private external fun pointInPolygon(pt: Vec2i, polyDirectPtr: CPointer): Int

private external fun polyTreeToPaths(polyTreeDirectPtr: CPointer, pathsDirectPtr: CPointer)

private external fun reversePath(pathDirectPtr: CPointer)

private external fun reversePaths(pathsDirectPtr: CPointer)

private external fun simplifyPolygon(inPoly: CPointer, outPolys: CPointer, fillType: Int)

private external fun simplifyPolygons(inPolys: CPointer, outPolys: CPointer, fillType: Int)