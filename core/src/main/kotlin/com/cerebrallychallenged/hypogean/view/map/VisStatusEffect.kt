package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.assetParameterBindings
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.asset.CompositeNode

suspend fun visualizeStatusEffects(bearer: Entity, parent: CompositeNode, assetLibrary: AssetLibrary) {
    for (statusEffect in bearer.statusEffects) {
        statusEffect.asset?.create(assetLibrary)?.apply {
            statusEffect.assetParameterBindings.apply(statusEffect, this)
            parent.attachChild(this)
        }
    }
}
