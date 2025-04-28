package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.LifeTime
import com.cerebrallychallenged.jun.util.CPointer

open class TSlotBase<S : TSlotBase<S>>(ptr: CPointer, ephemeral: LifeTime) : FSlotBase(ptr, ephemeral)