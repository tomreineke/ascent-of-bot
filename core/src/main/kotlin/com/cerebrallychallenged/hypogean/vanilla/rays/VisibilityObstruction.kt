package com.cerebrallychallenged.hypogean.vanilla.rays

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.attribute

/**
 * The opacity.
 * Intermediate values for semi-transparent glass and, e.g., debris, behind which you can hide but which
 * do not completely block sight.
 *
 * @see VisibilityExtractor
 */
var Entity.visibilityBlocking: BlockingValue by attribute(BlockingValue { 0.0f })

object VisibilityExtractor : SimpleBlockerValueExtractor(Entity::visibilityBlocking)
