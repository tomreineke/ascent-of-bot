package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.unreal.material.MutableMaterialBearer
import com.cerebrallychallenged.jun.unreal.material.MutableMaterials

interface PrimitiveComponentLike : SceneComponentLike, MutableMaterialBearer {
    var castCinematicShadow: Boolean

    var castDynamicShadow: Boolean

    var castFarShadow: Boolean

    var castHiddenShadow: Boolean

    var castInsetShadow: Boolean

    var castShadow: Boolean

    var castShadowAsTwoSided: Boolean

    var castStaticShadow: Boolean

    var castVolumetricTranslucentShadow: Boolean

    var collisionEnabled: ECollisionEnabled

    var customDepthStencilValue: Int

    val materials: MutableMaterials
        get() = MutableMaterials(this)

    var renderCustomDepth: Boolean

    var simulatePhysics: Boolean

    var useAsOccluder: Boolean

    var visibleInRayTracing: Boolean

    var visibleInReflectionCaptures: Boolean
}