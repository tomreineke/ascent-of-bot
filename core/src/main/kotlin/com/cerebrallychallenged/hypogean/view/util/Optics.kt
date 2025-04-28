package com.cerebrallychallenged.hypogean.view.util

import com.cerebrallychallenged.jun.unreal.ECollisionEnabled
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.light.ULightComponentBase

data class Optics(
    val meshVisible: Boolean,
    val lightVisible: Boolean,
    val castHiddenShadow: Boolean,
    val desaturated: Boolean
) {
    companion object {
        val DefaultHidden = Optics(
            meshVisible = false,
            lightVisible = true,
            castHiddenShadow = true,
            desaturated = false
        )
    }
}

fun USceneComponent.applyOptics(optics: Optics) {
    if (this is ULightComponentBase) {
        visibility = optics.lightVisible
    } else {
        val visible = optics.meshVisible
        visibility = visible
        if (this is UPrimitiveComponent) {
            castHiddenShadow = optics.castHiddenShadow
            useAsOccluder = visible
            collisionEnabled = if (visible) ECollisionEnabled.QueryOnly else ECollisionEnabled.NoCollision
            updateMaskedStencilValue(if (optics.desaturated) 0b10000 else 0, 0b10000)
        }
    }
}

fun UPrimitiveComponent.updateMaskedStencilValue(value: Int, mask: Int) {
    val newValue = (customDepthStencilValue and mask.inv()) or (value and mask)
    customDepthStencilValue = newValue
    renderCustomDepth = newValue != 0
}
