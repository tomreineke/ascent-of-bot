package com.cerebrallychallenged.jun.unreal.slate

import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.util.CPointer

interface FSlateBrush

var AnyRef<FSlateBrush>.imageSize: Vec2f
    get() = getImageSize(directPtr)
    set(value) {
        setImageSize(directPtr, value)
    }

var AnyRef<FSlateBrush>.uvRegion: Bounds<Vec2f>
    get() = getUVRegion(directPtr)
    set(value) {
        setUVRegion(directPtr, value)
    }

private external fun getImageSize(directPtr: CPointer): Vec2f

private external fun getUVRegion(directPtr: CPointer): Bounds<Vec2f>

private external fun setImageSize(directPtr: CPointer, size: Vec2f)

private external fun setUVRegion(directPtr: CPointer, bounds: Bounds<Vec2f>)