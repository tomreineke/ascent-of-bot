package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.jun.math.geo.Vec2i

/**
 * An entity impeding sight, movement, etc. together with how much it blocks the sight, movement etc.
 */
data class BlockerWithValue(val blockingEntity: LocatedEntity, val blockedPosition: Vec2i, val obstruction: Float)