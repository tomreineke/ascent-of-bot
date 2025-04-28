package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry

class EffectKinds : SimpleObjectRegistry<EffectKind>() {
    //FIXME[A] Temporary construct until registries become static objects.
    companion object : Iterable<EffectKind> {
        private lateinit var singleton: EffectKinds

        override fun iterator(): Iterator<EffectKind> = singleton.iterator()
    }

    init {
        singleton = this
    }
}
