package com.cerebrallychallenged.hypogean.view.enemyturn

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.activestate.GameOverState
import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.gui.NinePatchResource
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.paragraphNode
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.layout.Visibility.Companion.visibleIf
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

class EnemyTurnView(context: ViewFactory.Context) : View, FactionContext by context {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val BorderGradientResource = NinePatchResource(
            ImageResource("Images/gui/border_gradient.png"),
            IRect.Empty,
            IRect(200, 200, 201, 201)
        )

        val DefaultBorderStyle = Styling<Node, Unit> {
            background[InputState.Empty] = ResourceLibrary[BorderGradientResource]
        }

        var borderStyle: Styling<Node, Unit> = DefaultBorderStyle
    }

    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): View = EnemyTurnView(context)
    }

    private val mainNode = Node().apply {
        context.widget.layers[GuiLayer.Overlay].children.add(this)
        hitModel = HitModel.None
        applyStyle(Style.borderStyle)
        horizontalAlign = Align.Stretch
        verticalAlign = Align.Stretch
        paragraphNode(GuiConfig.BigOverlayTextStyle.copy(color = FLinearColor.Red)) {
            +"NPC TURN"
        }.apply {
            horizontalAlign = Align.Center
            verticalAlign = Align.Max
            bottom = 400.scaled
        }
        visibility = Visibility.Hidden
    }

    private val changeVisitor = object : WorldChange.SimpleVisitor {
        override fun visit(change: WorldChange.ActiveStateChanged) {
            when (val activeState = change.activeState) {
                is ActiveActorState -> {
                    val activeActor = activeState.activeActor
                    val activeRelation = activeActor.factionRelation
                    val visible = activeRelation != Faction.Relation.SAME
                    mainNode.visibility = visibleIf(visible)
                }
                is GameOverState -> {
                    mainNode.visibility = Visibility.Hidden
                }
                else -> mainNode.visibility = Visibility.Visible
            }
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        if (change is ModelChange) {
            change.changes.forEach { it.accept(changeVisitor) }
        }
    }
}
