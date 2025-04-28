package com.cerebrallychallenged.hypogean.view.inibar

import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.tooltip
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute
import com.cerebrallychallenged.hypogean.util.collections.TypedIntStatistic
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.BackgroundAnimation
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.attachBackgroundAnimation
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.node.Node

internal class GageNode<T : Entity>(val attribute: SimpleIntAttribute<T>, private val entity: T) : Node() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        var height: Int = 40
    }

    private val filling = Node().apply {
        this@GageNode.children.add(this)
        verticalAlign = Align.Stretch
        background[InputState.Empty] = Background.Rect(attribute.fillingColor, PaintStyle.Fill)
    }

    private val blinking = Node().apply {
        this@GageNode.children.add(this)
        verticalAlign = Align.Stretch
        background[InputState.Empty] = Background.Rect(attribute.fillingColor, PaintStyle.Fill)
        attachBackgroundAnimation(InputState.Empty, BackgroundAnimation(
            BackgroundAnimation.Driver.Wave(1.0f),
            BackgroundAnimation.Target.ColorInterpolation(attribute.fillingColor, attribute.fillingColor * 2.0f)
        ))
    }

    var expectedDelta: Float? = null

    private fun update() {
        val current = attribute.current.get(entity)
        val max = attribute.max.get(entity)
        val expectedDelta = expectedDelta

        val actualCurrent: Int
        val actualBlinking: Int
        when {
            expectedDelta == null || expectedDelta == 0.0f -> {
                actualCurrent = current
                actualBlinking = 0
            }
            expectedDelta < 0.0f -> {
                actualCurrent = (current + expectedDelta).floorToInt().coerceAtLeast(0)
                actualBlinking = current - actualCurrent
            }
            else -> {
                actualCurrent = current
                actualBlinking = expectedDelta.ceilToInt()
            }
        }
        val fillingWidth = ((size.x * actualCurrent).toFloat() / max).ceilToInt().coerceAtMost(size.x)
        filling.minWidth = fillingWidth
        filling.maxWidth = fillingWidth
        val blinkingWidth = ((size.x * actualBlinking).toFloat() / max).ceilToInt().coerceAtMost(size.x - fillingWidth)
        blinking.minWidth = blinkingWidth
        blinking.maxWidth = blinkingWidth
        tooltip = Tooltip {
            +"${attribute.name} $current / $max ${attribute.symbol}"
        }
    }

    private val changeVisitor = object : WorldChange.SimpleVisitor {
        override fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            if (change.entity == entity) {
                change.ifOf(attribute.current) {
                    update()
                }
                change.ifOf(attribute.max) {
                    update()
                }
            }
        }
    }

    fun onChange(change: WorldChange) {
        change.accept(changeVisitor)
    }

    fun updateEstimatedConsequences(estimatedConsequences: TypedIntStatistic<IntProperty>?) {
        expectedDelta = estimatedConsequences?.let { it[attribute].expectedValue }
        update()
    }

    init {
        flow = Flow.LeftToRight
        horizontalAlign = Align.Stretch
        val height = Style.height.scaled
        minHeight = height
        maxHeight = height
        update()

        resizeListeners += {
            update()
        }
    }
}
