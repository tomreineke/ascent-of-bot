package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Transform3f.Companion.translation
import com.cerebrallychallenged.jun.math.geo.Vec3f

sealed class Position {
    data class Absolute(private val transform: Transform3f) : Position() {
        constructor(positionMeters: Vec3f) : this(translation(positionMeters * 100.0f))

        context(MapViewContext)
        override fun computeTransform(): Transform3f = transform

        override val estimatedTransform: Transform3f by ::transform
    }

    data class Node(
        val entity: Entity,
        val subEntity: Item? = null,
        val socketName: String? = null,
        val fallback: Transform3f? = null
    ): Position() {
        constructor(
            entity: Entity,
            subEntity: Item? = null,
            socketName: String? = null,
            fallbackMeters: Vec3f
        ) : this(entity, subEntity, socketName, translation(fallbackMeters * 100.0f))

        context(MapViewContext)
        fun findNode(): CompositeNode? = visMap[entity]?.let { visEntity ->
            subEntity?.let { visEntity.nodeForItem(it) }
        }

        context(MapViewContext)
        override fun computeTransform(): Transform3f? {
            val node = findNode() ?: return fallback
            return socketName?.let { node.sockets[it]?.getSocketTransform(it) } ?: node.componentTransform
        }

        override val estimatedTransform: Transform3f? by ::fallback
    }

    context(MapViewContext)
    abstract fun computeTransform(): Transform3f?

    abstract val estimatedTransform: Transform3f?
}
