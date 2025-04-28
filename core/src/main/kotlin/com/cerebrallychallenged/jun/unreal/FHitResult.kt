package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

interface FHitResult

val AnyRef<FHitResult>.actor: AActor?
    get() = getActor(directPtr).wrapNullableUObject()

val AnyRef<FHitResult>.component: UPrimitiveComponent?
    get() = getComponent(directPtr).wrapNullableUObject()

val AnyRef<FHitResult>.impactPoint: Vec3f
    get() = getImpactPoint(directPtr)

private external fun getActor(directPtr: CPointer): CPointer

private external fun getComponent(directPtr: CPointer): CPointer

private external fun getImpactPoint(directPtr: CPointer): Vec3f
