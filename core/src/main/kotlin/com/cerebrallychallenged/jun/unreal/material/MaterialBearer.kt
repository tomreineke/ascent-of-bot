package com.cerebrallychallenged.jun.unreal.material

import com.cerebrallychallenged.jun.Convenience

@Convenience
interface MaterialBearer {
    val numMaterials: Int

    fun getMaterial(index: Int): UMaterialInterface?
}

@Convenience
interface MutableMaterialBearer : MaterialBearer {
    fun setMaterial(index: Int, material: UMaterialInterface?)
}