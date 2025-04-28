package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.gui.NinePatchResource
import com.cerebrallychallenged.hypogean.gui.ParagraphNode
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.node
import com.cerebrallychallenged.hypogean.gui.paragraphNode
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Animation
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.hypogean.view.map.events.ShoutEvent.Style.shoutBubbleParagraphStyle
import com.cerebrallychallenged.hypogean.view.map.events.ShoutEvent.Style.shoutBubbleStyle
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.layout.Margin
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import java.util.EnumMap

object StandardShouts {
    var TalkingProbability = 0.3

    private val shouts: Map<Faction.Relation, MutableList<String>> =
        Faction.Relation.entries.toTypedArray().associateWithTo(EnumMap(Faction.Relation::class.java)) {
            mutableListOf()
        }

    operator fun get(relation: Faction.Relation): MutableList<String> = shouts.getValue(relation)
}

data class ShoutEvent(val actor: Actor, val text: String) : MapViewEvent() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val Image = ImageResource("Images/gui/shout-bubble.png")

        val NinePatchOvershoot = IRect(8, 10, 10, 8)

        val NinePatchCenter = IRect(90, 60, 91, 61)

        val Background = NinePatchResource(Image, NinePatchOvershoot, NinePatchCenter)

        val DefaultShoutBubbleStyle = Styling<Node, Unit> {
            background[InputState.Empty] = ResourceLibrary[Background, GuiConfig.guiScale]
        }

        var shoutBubbleStyle = DefaultShoutBubbleStyle

        val DefaultShoutBubbleParagraphStyle = Styling<ParagraphNode, Unit> {
            val long = 72.scaled
            val short = 40.scaled
            margin = Margin(long, short, short, long)
        }

        var shoutBubbleParagraphStyle = DefaultShoutBubbleParagraphStyle
    }

    context(MapViewContext)
    override suspend fun execute() {
        val visActor = visMap[actor] ?: return
        val node = widget.layers[GuiLayer.Overlay].node(shoutBubbleStyle) {
            paragraphNode(style = shoutBubbleParagraphStyle) {
                +text
            }
        }
        val rootComponent = visActor.rootComponent
        val height = vec(0.0f, 0.0f, actor.height * 100.0f)
        val playerController = UGameplayStatics.getPlayerController(playerIndex = 0)
        addAnimation(object : Animation(2.0f) {
            override fun onTick(deltaTime: Float): Boolean {
                val position2f = playerController.projectWorldLocationToScreen(rootComponent.worldLocation + height)
                if (position2f != null) {
                    val position = position2f.round()
                    node.left = position.x
                    node.top = position.y
                }
                return false
            }

            override fun onEnd() {
                node.detach()
            }
        })
    }
}
