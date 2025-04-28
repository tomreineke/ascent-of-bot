package com.cerebrallychallenged.jun.unreal.skiatree


import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.Layers
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.widget.SLeafWidget
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface SJunSkiaTreeWidget : SLeafWidget {
    companion object {
        fun createBySNew(layers: Layers): TSharedRef<SJunSkiaTreeWidget> =
            createBySNewImpl(libraryPointer.address(), layers.address.address()).wrapSharedRef()
    }
}

fun AnyRef<SJunSkiaTreeWidget>.setUpcalls(tick: CPointer, resize: CPointer) {
    setUpcalls(directPtr, tick, resize)
}

fun AnyRef<SJunSkiaTreeWidget>.isPixelCovered(position: Vec2i): Boolean = isPixelCovered(directPtr, position)

private external fun setUpcalls(
    directPtr: CPointer,
    tick: CPointer,
    resize: CPointer,
)

private external fun createBySNewImpl(libraryPtr: CPointer, layersPtr: CPointer): CPointer

private external fun isPixelCovered(directPtr: CPointer, position: Vec2i): Boolean
