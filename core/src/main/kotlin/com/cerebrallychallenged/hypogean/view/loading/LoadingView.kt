package com.cerebrallychallenged.hypogean.view.loading

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.node
import com.cerebrallychallenged.hypogean.gui.paragraphNode
import com.cerebrallychallenged.hypogean.gui.vBox
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.audio.AudioManager
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.imageNode
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.unreal.sound.EAudioFaderCurve
import com.cerebrallychallenged.jun.unreal.sound.USoundCue


class LoadingView(context: ViewFactory.Context, private val tipsOfDay: List<String>) : View {
    class Factory : ViewFactory {
        val tipsOfDay: MutableList<String> = mutableListOf()

        val sounds: MutableList<UnrealRef<USoundCue>> = mutableListOf()

        override suspend fun create(context: ViewFactory.Context): View {
            val sound = context.assetLibrary.load(sounds.random())
            AudioManager.stop()
            AudioManager.sound = sound
            AudioManager.play()
            return LoadingView(context, tipsOfDay)
        }
    }

    internal val mainNode = context.widget.layers[GuiLayer.LoadingView].node {
        horizontalAlign = Align.Stretch
        verticalAlign = Align.Stretch
        hitModel = HitModel.None
        background[InputState.Empty] = Background.Rect(FLinearColor.Black, PaintStyle.Fill)
        vBox {
            vgap = 10
            verticalAlign = Align.Center
            horizontalAlign = Align.Center
            imageNode(image = ResourceLibrary[ImageResource("Images/loading_images/library-hero-capsule.png")].scale(0.3f)) {
                horizontalAlign = Align.Center
            }
            paragraphNode(GuiConfig.DefaultItalicStyle) { +tipsOfDay.random() }.apply {
                horizontalAlign = Align.Center
            }
        }
    }

    private val widget = context.widget

    private var isLoaded = false

    private val changeVisitor = object : WorldChange.SimpleSuspendVisitor {
        override suspend fun visit(change: WorldChange.Clear) {
            isLoaded = false
        }

        override suspend fun visit(change: WorldChange.ActiveStateChanged) {
            if (change.activeState is ActiveActorState && !isLoaded) {
                isLoaded = true
                mainNode.visibility = Visibility.Hidden
                AudioManager.fadeOut(3.0f, 0.0f, EAudioFaderCurve.SCurve)
                for (layerIndex in 0 until GuiLayer.LoadingView.layerIndex) {
                    widget.layers[layerIndex].visibility = Visibility.Visible
                }
            }
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        if (change is ModelChange) {
            change.changes.forEach { it.accept(changeVisitor) }
        }
    }
}
