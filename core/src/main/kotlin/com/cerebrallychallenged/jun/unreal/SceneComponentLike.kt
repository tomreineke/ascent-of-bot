package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec3f

interface SceneComponentLike {
    val componentLocation: Vec3f

    val componentQuat: Quaternion

    val componentScale: Vec3f

    val componentTransform: Transform3f

    var mobility: EComponentMobility

    var relativeLocation: Vec3f

    var relativeRotation: Quaternion

    var relativeScale3D: Vec3f

    var relativeTransform: Transform3f

    fun setRelativeLocationAndRotation(newLocation: Vec3f, newRotation: Quaternion)

    fun setWorldLocationAndRotation(newLocation: Vec3f, newRotation: Quaternion)

    var visibility: Boolean

    fun setVisibility(visibility: Boolean, propagateToChildren: Boolean = false)

    var worldLocation: Vec3f

    var worldRotation: Quaternion

    var worldScale3D: Vec3f

    var worldTransform: Transform3f
}