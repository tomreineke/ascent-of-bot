package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.util.CPointer

interface FChildren

val AnyRef<FChildren>.num: Int
    get() = getNum(directPtr)

private external fun getNum(directPtr: CPointer): Int