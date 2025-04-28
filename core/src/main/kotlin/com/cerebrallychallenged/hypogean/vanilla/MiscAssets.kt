@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla

import com.cerebrallychallenged.hypogean.vanilla.refs.StarterContent.P_Fire
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.particleSystemComponent
import com.cerebrallychallenged.jun.math.geo.vec

object Asset_Fire : CompositeAsset({
    particleSystemComponent {
        template = load(P_Fire)
        transform(translation = vec(0.0f, 0.0f, 50.0f))
    }
})