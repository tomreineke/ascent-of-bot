package com.cerebrallychallenged.jun.unreal.niagara

import com.cerebrallychallenged.jun.unreal.PrimitiveComponentLike

interface NiagaraComponentLike : PrimitiveComponentLike {
    var asset: UNiagaraSystem?
}
