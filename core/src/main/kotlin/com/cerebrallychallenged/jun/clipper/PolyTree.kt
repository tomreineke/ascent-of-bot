package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.DirectRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface PolyTree : PolyNode {
    companion object {
        fun makeShared(): TSharedRef<PolyTree> = makeSharedOfPolyTree().wrapSharedRef()
    }
}

fun AnyRef<PolyTree>.clear() {
    clear(directPtr)
}

@Convenience
val AnyRef<PolyTree>.closedPaths: TSharedRef<Paths>
    get() = Paths.makeShared().also { closedPathsFromPolyTree(this, it) }

val AnyRef<PolyTree>.first: DirectRef<PolyNode>
    get() = getFirst(directPtr).wrapDirectRef()

@Convenience
val AnyRef<PolyTree>.openPaths: TSharedRef<Paths>
    get() = Paths.makeShared().also { openPathsFromPolyTree(this, it) }

@Convenience
fun AnyRef<PolyTree>.toPaths(): TSharedRef<Paths> = Paths.makeShared().also { polyTreeToPaths(this, it) }

val AnyRef<PolyTree>.total: Int
    get() = getTotal(directPtr)

private external fun clear(directPtr: CPointer)

private external fun getFirst(directPtr: CPointer): CPointer

private external fun getTotal(directPtr: CPointer): Int

private external fun makeSharedOfPolyTree(): CPointer