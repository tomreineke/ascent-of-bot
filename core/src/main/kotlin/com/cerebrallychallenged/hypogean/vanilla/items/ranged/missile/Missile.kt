@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile

import com.cerebrallychallenged.hypogean.vanilla.refs.MediumMechStriker.ScifiMechHeavyCanonShell
import com.cerebrallychallenged.hypogean.vanilla.refs.MediumMechStriker.ScifiMechLighCanonShell
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent

object Asset_Missile : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ScifiMechHeavyCanonShell)
    }
})

object Asset_HomingMissile : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ScifiMechLighCanonShell)
    }
})
