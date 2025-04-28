package com.cerebrallychallenged.hypogean.model.cascade

import com.cerebrallychallenged.hypogean.vanilla.cascade.EffectResult

interface CausalChange {
    val delta: Int
    val effectResult: EffectResult
}
