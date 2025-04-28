package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.DirectRef
import com.cerebrallychallenged.jun.util.CPointer

interface PolyNodes

operator fun AnyRef<PolyNodes>.get(index: Long): DirectRef<PolyNode> = get(directPtr, index).wrapDirectRef()

operator fun AnyRef<PolyNodes>.iterator(): Iterator<DirectRef<PolyNode>> =
        (0 until size).asSequence().map { get(it) }.iterator()

val AnyRef<PolyNodes>.size: Long
    get() = getSize(directPtr)

private external fun get(directPtr: CPointer, index: Long): CPointer

private external fun getSize(directPtr: CPointer): Long
