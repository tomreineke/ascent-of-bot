package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.view.Visibility
import com.cerebrallychallenged.hypogean.view.map.MapViewContext

class NodeVisibilityEvent(
    val visible: Boolean,
    val entity: Entity,
    private val subEntity: Item? = null,
) : MapViewEvent() {
    context(MapViewContext)
    override suspend fun execute() {
        val baseNode = visMap[entity] ?: return
        val node = subEntity?.let { baseNode.nodeForItem(subEntity) } ?: baseNode.node ?: return
        node.parameters[Visibility] = visible
    }
}
