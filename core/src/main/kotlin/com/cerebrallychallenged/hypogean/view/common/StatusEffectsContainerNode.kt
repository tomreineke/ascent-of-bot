package com.cerebrallychallenged.hypogean.view.common

import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.tooltip
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.hypogean.view.modular.modules.StatusEffectsModule
import com.cerebrallychallenged.hypogean.view.tooltip.createTooltip
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.node.Node

class StatusEffectsContainerNode(private val viewModel: ViewModel, entity: Entity, containerWidth: Int, private val effectSize: Int) : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        const val Gap = 12

        var containerStyle: Styling<Node, Int> = Styling { containerWidth ->
            flow = Flow.LeftToRightThenTopToBottom
            val gap = Gap.scaled
            hgap = gap
            vgap = gap
            val width  = containerWidth.scaled
            minWidth = width
            maxWidth = width
        }

        var effectStyle: Styling<Node, Int> = Styling { effectSize ->
            val size = effectSize.scaled
            minWidth = size
            maxWidth = size
            minHeight = size
            maxHeight = size
        }
    }

    inner class EffectNode(private val effect: StatusEffect, effectSize: Int) : Node() {
        init {
            applyStyle(Style.effectStyle, effectSize)
            this@StatusEffectsContainerNode.children.add(this)
            updateIcon()
            updateTooltip()
        }

        internal fun updateIcon() {
            val icon = effect.icon
            background[InputState.Empty] = if (icon != null) {
                Background.Image(
                    ResourceLibrary.imageWithWidth(icon, StatusEffectsModule.Style.effectSize.scaled),
                    IRect.Empty
                )
            } else {
                Background.Empty
            }
        }

        private fun updateTooltip() {
            tooltip = effect.createTooltip(viewModel, true)
        }
    }

    private val nodes = mutableMapOf<StatusEffect, EffectNode>()

    private val changeListener = object : WorldChange.SimpleVisitor {
        override fun visit(change: WorldChange.StatusEffectCreated) {
            val (statusEffect) = change
            if (statusEffect.bearer == entity) {
                addEffect(statusEffect)
            }
        }

        override fun visit(change: WorldChange.Removed) {
            val (removedEntity) = change
            if (removedEntity is StatusEffect && removedEntity.bearer == entity) {
                nodes.remove(removedEntity)?.detach()
            }
        }

        override fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            val changedEntity = change.entity
            if (changedEntity is StatusEffect && changedEntity.bearer == entity) {
                change.ifOf(StatusEffect::icon) { _ ->
                    nodes[changedEntity]?.updateIcon()
                }
            }
        }
    }

    fun onChange(change: WorldChange) {
        change.accept(changeListener)
    }

    private fun addEffect(effect: StatusEffect) {
        nodes.getOrPut(effect) {
            EffectNode(effect, effectSize)
        }
    }

    init {
        applyStyle(Style.containerStyle, containerWidth)
        for (statusEffect in entity.statusEffects) {
            addEffect(statusEffect)
        }
    }
}
