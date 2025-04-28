package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.jun.math.geo.Vec2i

class CachingRays(private val rayStencil: RayStencil, private val world: World) {
    val horizontalRadius = rayStencil.horizontalRadius

    private data class QueryParameter(
            val sourcePosition: Vec2i,
            val blockerValueExtractor: BlockerValueExtractor,
            val actingSubject: Any?
    )

    private val cache = mutableMapOf<QueryParameter, RaysQuery>()

    private var isCacheEmpty: Boolean = true

    internal fun invalidateCache() {
        if (!isCacheEmpty) {
            cache.clear()
            isCacheEmpty = true
        }
    }

    internal fun query(
            sourcePosition: Vec2i,
            blockerValueExtractor: BlockerValueExtractor,
            actingSubject: Any?
    ): RaysQuery {
        isCacheEmpty = false
        return cache.computeIfAbsent(QueryParameter(sourcePosition, blockerValueExtractor, actingSubject)) {
            RaysQuery(QueryParameters(sourcePosition, blockerValueExtractor, actingSubject, world), rayStencil)
        }
    }
}