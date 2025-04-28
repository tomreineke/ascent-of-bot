package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.DirectPtr
import com.cerebrallychallenged.jun.unreal.DirectRef
import com.cerebrallychallenged.jun.util.CPointer

interface PolyNode

val AnyRef<PolyNode>.next: DirectRef<PolyNode>
    get() = getNext(directPtr).wrapDirectRef()

@Suppress("SpellCheckingInspection")
val AnyRef<PolyNode>.childs: DirectRef<PolyNodes>
    get() = getChilds(directPtr).wrapDirectRef()

val AnyRef<PolyNode>.contour: DirectRef<Path>
    get() = getContour(directPtr).wrapDirectRef()

val AnyRef<PolyNode>.isHole: Boolean
    get() = isHole(directPtr)

val AnyRef<PolyNode>.isOpen: Boolean
    get() = isOpen(directPtr)

val AnyRef<PolyNode>.parent: DirectPtr<PolyNode>
    get() = getParent(directPtr).wrapDirectPtr()

@Suppress("SpellCheckingInspection")
private external fun getChilds(directPtr: CPointer): CPointer

private external fun getContour(directPtr: CPointer): CPointer

private external fun getNext(directPtr: CPointer): CPointer

private external fun getParent(directPtr: CPointer): CPointer

private external fun isHole(directPtr: CPointer): Boolean

private external fun isOpen(directPtr: CPointer): Boolean
