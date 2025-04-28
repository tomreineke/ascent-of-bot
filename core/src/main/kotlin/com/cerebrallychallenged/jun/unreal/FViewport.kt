package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.util.CPointer

interface FViewport

val AnyRef<FViewport>.initialPositionXY: Vec2i
    get() = getInitialPositionXY(directPtr)

private external fun getInitialPositionXY(directPtr: CPointer): Vec2i