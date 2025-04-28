package com.cerebrallychallenged.hypogean.view.inibar

import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.tooltip
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.FactionMember
import com.cerebrallychallenged.hypogean.model.IniHolder
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttributes
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.util.collections.TypedIntStatistic
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.view.common.PortraitNode
import com.cerebrallychallenged.hypogean.view.common.StatusEffectsContainerNode
import com.cerebrallychallenged.hypogean.view.common.installEntityListeners
import com.cerebrallychallenged.jun.math.geo.curve.buildPolyline
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.BackgroundAnimation
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.Paint
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.PathEffect
import com.cerebrallychallenged.jun.skiatree.attachBackgroundAnimation
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import kotlin.math.roundToInt

internal class VisIniHolder(iniBar: IniBarView, private val iniHolder: IniHolder) : VisElement(iniBar), FactionContext by iniBar {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        var vgap: Int = 4

        var effectSize: Int = 128

        var actorBorder: Int = 44

        var eventBorder: Int = 28

        var portraitContainerStyle: Styling<Node, FLinearColor> = Styling { factionColor ->
            background[InputState.Selected] = Background.Rect(Paint().apply {
                color = factionColor
                style = PaintStyle.Stroke
                strokeWidth = 6.0f
                pathEffect = PathEffect.dashPattern(floatArrayOf(20.0f, 20.0f), 0.0f)
            }, overshoot = IRect(3, 3, 3, -3))
            attachBackgroundAnimation(InputState.Selected, BackgroundAnimation(
                BackgroundAnimation.Driver.Linear(-40.0f),
                BackgroundAnimation.Target.DashPhase
            ))
        }

        var portraitStyle: Styling<PortraitNode, FLinearColor> = Styling { factionColor ->
            bottom = 6
            backgroundColor = factionColor * 0.25f
        }
    }

    internal val portraitContainerNode = Node()

    internal val portraitNode = PortraitNode(
        IniBarView.Style.iniHolderWidth,
        if (iniHolder is Actor) Style.actorBorder else Style.eventBorder
    ).apply {
        portraitContainerNode.children.add(this)
    }

    var currentAnimation: Animation? = null

    var lastIniTime: Int = iniHolder.scheduledIniTime

    var opacity: Float = 1.0f

    var targetOpacity: Float = 1.0f

    val gageNodes: List<GageNode<IniHolder>>

    private val statusEffectsContainer =
        StatusEffectsContainerNode(iniBar.viewModel, iniHolder, IniBarView.Style.iniHolderWidth, Style.effectSize)

    override fun animate(deltaSeconds: Float): Boolean {
        var anyChange = false
        if (opacity != targetOpacity) {
            opacity = valueStep(opacity, targetOpacity, IniBarView.Style.opacitySpeed, deltaSeconds)
            if (opacity == 0.0f) {
                visibility = Visibility.Hidden
                hitModel = HitModel.None
            } else {
                visibility = Visibility.Visible
                hitModel = HitModel.Rect()
            }
            anyChange = true
        }
        currentAnimation?.let { animation ->
            val animationChange = animation.animate(deltaSeconds)
            if (animationChange) {
                val position = animation.current
                left = position.x.roundToInt()
                top = position.y.roundToInt()
                anyChange = true
            } else {
                currentAnimation = null
            }
        }
        return anyChange
    }

    fun updateLayout(targetLeft: Float) {
        if (isNew) {
            left = targetLeft.roundToInt()
            top = IniBarView.Style.top.scaled
            isNew = false
        } else {
            val startLeft = left.toFloat()
            currentAnimation = Animation(buildPolyline(vec(startLeft, top.toFloat())) {
                speed = iniBar.movementSpeed
                if (iniHolder.scheduledIniTime > lastIniTime) {
                    val y = IniBarView.Style.complexMovementY.scaled.toFloat()
                    lineTo(vec(startLeft, y))
                    lineTo(vec(targetLeft, y))
                }
                lineTo(vec(targetLeft, IniBarView.Style.top.scaled.toFloat()))
            })
        }
        lastIniTime = iniHolder.scheduledIniTime
    }

    private fun updatePortraitTooltip() {
        portraitNode.tooltip = Tooltip {
            +iniHolder.name
            if (iniHolder is FactionMember) {
                iniHolder.factionEntity?.let { faction ->
                    newLine()
                    +faction.name
                }
            }
        }
    }

    private val changeVisitor = object : WorldChange.SimpleVisitor {
        override fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            change.ifOf(Entity::icon) { (entity, _, newValue, _) ->
                if (entity == iniHolder) {
                    portraitNode.image = newValue
                }
            }
            change.ifOf(Entity::name) {
                updatePortraitTooltip()
            }
            change.ifOf(FactionEntity::relations) {
                updateFaction()
            }
        }

        override fun visit(change: WorldChange.FactionMembershipChanged) {
            if (change.factionMember == iniHolder) {
                updateFaction()
            }
        }
    }

    fun onChange(change: WorldChange) {
        change.accept(changeVisitor)
        for (node in gageNodes) {
            node.onChange(change)
        }
        statusEffectsContainer.onChange(change)
    }

    fun updateEstimatedConsequences(estimatedConsequences: TypedIntStatistic<IntProperty>?) {
        for (gageNode in gageNodes) {
            gageNode.updateEstimatedConsequences(estimatedConsequences)
        }
    }

    fun updateFaction() {
        val factionColor = if (iniHolder is FactionMember) {
            GuiConfig.ColorByFactionRelation.getValue(iniHolder.factionRelation)
        } else {
            FLinearColor.Black
        }
        portraitContainerNode.applyStyle(Style.portraitContainerStyle, factionColor)
        portraitNode.applyStyle(Style.portraitStyle, factionColor)
    }

    var isHovered: Boolean = false
        set(value) {
            field = value
            portraitNode.isSelected = value
        }

    var isActive: Boolean = false
        set(value) {
            field = value
            portraitContainerNode.isSelected = value
        }

    init {
        debugName = "VisIniHolder($iniHolder)"
        children.add(portraitContainerNode)
        gageNodes = iniHolder.rulebook.feature<SimpleIntAttributes>().mapNotNull { attribute ->
            attribute.asAttributeFor(iniHolder)?.let {
                GageNode(it, iniHolder).also { node ->
                    children.add(node)
                }
            }
        }
        children.add(statusEffectsContainer)
        flow = Flow.Vertical
        vgap = Style.vgap.scaled
        portraitNode.image = iniHolder.icon
        portraitNode.installEntityListeners(iniBar.viewModel, iniHolder)
        updateFaction()
        updatePortraitTooltip()
    }
}
