package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.DirectRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface Paths {
    companion object {
        fun makeShared(count: Int = 0): TSharedRef<Paths> = makeSharedOfPaths(count).wrapSharedRef()
    }
}

operator fun AnyRef<Paths>.get(index: Long): DirectRef<Path> = get(directPtr, index).wrapDirectRef()

@Convenience
operator fun AnyRef<Paths>.iterator(): Iterator<DirectRef<Path>> =
        (0 until size).asSequence().map { get(it) }.iterator()

fun AnyRef<Paths>.pushBack(path: AnyRef<Path>) {
    pushBack(directPtr, path.directPtr)
}

val AnyRef<Paths>.size: Long
    get() = getSize(directPtr)

private external fun get(directPtr: CPointer, index: Long): CPointer

private external fun getSize(directPtr: CPointer): Long

private external fun makeSharedOfPaths(count: Int): CPointer

private external fun pushBack(directPtr: CPointer, pathDirectPtr: CPointer)