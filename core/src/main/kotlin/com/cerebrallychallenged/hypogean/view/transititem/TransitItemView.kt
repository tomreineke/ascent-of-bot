package com.cerebrallychallenged.hypogean.view.transititem

import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.activeActor
import com.cerebrallychallenged.hypogean.vanilla.actions.transitItem
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.node.ImageNode
import com.cerebrallychallenged.jun.skiatree.node.Node

class TransitItemView(context: ViewFactory.Context) : View, FactionContext by context {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        const val PortraitSize = 240

        var portraitSize: Int = PortraitSize
    }

    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): TransitItemView = TransitItemView(context)
    }

    private val portraitSize = Style.portraitSize.scaled

    private val widget = context.widget

    private val world = context.world

    private var activeActor: Actor? = world.activeActor

    private var transitItem: Item? = activeActor?.transitItem

    private val portraitNode = ImageNode().apply {
        context.widget.layers[GuiLayer.Overlay].children.add(this)
        hitModel = HitModel.None
    }

    private fun updateItem(transitItem: Item?) {
        val image = transitItem?.icon?.let { ResourceLibrary.imageWithLongerSize(it, portraitSize) }
        portraitNode.image = image
        if (image != null) {
            updatePosition(widget.mousePosition)
        }
    }

    private fun updatePosition(position: Vec2i) {
        portraitNode.left = position.x - portraitSize / 2
        portraitNode.top = position.y - portraitSize / 2
    }

    private val changeVisitor = object : WorldChange.SimpleVisitor {
        override fun visit(change: WorldChange.ActiveStateChanged) {
            activeActor = world.activeActor
            val transitItem = activeActor?.takeIf { it.isOwn }?.transitItem
            if (transitItem != this@TransitItemView.transitItem) {
                this@TransitItemView.transitItem = transitItem
                updateItem(transitItem)
            }
        }

        override fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            change.ifOf(Actor::transitItem) { (actor, _, value) ->
                if (actor == activeActor) {
                    transitItem = value
                    updateItem(value.takeIf { (actor as Actor).isOwn })
                }
            }
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        if (change is ModelChange) {
            change.changes.forEach { it.accept(changeVisitor) }
        }
    }

    init {
        Node().apply {
            context.widget.layers[GuiLayer.Overlay].children.add(this)
            consumesHover = false
            horizontalAlign = Align.Stretch
            verticalAlign = Align.Stretch
            mouseMoveListeners += { position, _, _ ->
                if (portraitNode.image != null) {
                    updatePosition(position)
                }
                false
            }
        }
    }
}
