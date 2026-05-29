package com.cerebrallychallenged.hypogean.model.cascade

import com.cerebrallychallenged.hypogean.model.Entity

sealed class EffectReason {
    data class ByEntity(val entity: Entity): EffectReason()
    data class Named(val name: String): EffectReason()
}
