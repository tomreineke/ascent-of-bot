package com.cerebrallychallenged.jun.unreal.material

import com.cerebrallychallenged.jun.util.DelegatingList

@JvmInline
value class Materials(private val bearer: MaterialBearer) : DelegatingList<UMaterialInterface?> {
    override val size: Int
        get() = bearer.numMaterials

    override fun get(index: Int): UMaterialInterface? = bearer.getMaterial(index)
}

@JvmInline
value class MutableMaterials(private val bearer: MutableMaterialBearer) : DelegatingList<UMaterialInterface?> {
    override val size: Int
        get() = bearer.numMaterials

    override fun get(index: Int): UMaterialInterface? = bearer.getMaterial(index)

    operator fun set(index: Int, material: UMaterialInterface?) {
        bearer.setMaterial(index, material)
    }
}
