package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.util.CPointer

interface ClipperBase

fun AnyRef<ClipperBase>.addPath(path: AnyRef<Path>, polyType: PolyType, closed: Boolean): Boolean =
        addPath(directPtr, path.directPtr, polyType.ordinal, closed)

fun AnyRef<ClipperBase>.addPaths(paths: AnyRef<Paths>, polyType: PolyType, closed: Boolean): Boolean =
        addPath(directPtr, paths.directPtr, polyType.ordinal, closed)

val AnyRef<ClipperBase>.bounds: Bounds<Vec2i>
    get() = getBounds(directPtr)

fun AnyRef<ClipperBase>.clear() {
    clear(directPtr)
}

var AnyRef<Clipper>.preserveCollinear: Boolean
    get() = getPreserveCollinear(directPtr)
    set(value) {
        setPreserveCollinear(directPtr, value)
    }

private external fun addPath(directPtr: CPointer, pathDirectPtr: CPointer, polyType: Int, closed: Boolean): Boolean

private external fun addPaths(directPtr: CPointer, pathsDirectPtr: CPointer, polyType: Int, closed: Boolean): Boolean

private external fun clear(directPtr: CPointer)

private external fun getBounds(directPtr: CPointer): Bounds<Vec2i>

private external fun getPreserveCollinear(directPtr: CPointer): Boolean

private external fun setPreserveCollinear(directPtr: CPointer, value: Boolean)