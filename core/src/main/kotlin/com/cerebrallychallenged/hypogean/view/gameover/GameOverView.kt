package com.cerebrallychallenged.hypogean.view.gameover

import com.cerebrallychallenged.hypogean.activestate.GameOverReason
import com.cerebrallychallenged.hypogean.activestate.GameOverState
import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.VBox
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.node
import com.cerebrallychallenged.hypogean.gui.paragraphNode
import com.cerebrallychallenged.hypogean.gui.standardButton
import com.cerebrallychallenged.hypogean.gui.vBox
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.unreal.color.FLinearColor


class GameOverView(context: ViewFactory.Context) : View {

    private var gameOverReason: GameOverReason? = null
    private var roundCount: Int? = null

    private fun explanation(): String {
        val explanation = when (gameOverReason) {
            GameOverReason.NoEnergy -> "Party has no more energy"
            GameOverReason.NoHealth -> "Party is dead"
            else -> ""
        }
        return "$explanation\nRounds played: $roundCount"
    }

    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): View {
            return GameOverView(context)
        }
    }

    internal val mainNode = context.widget.layers[GuiLayer.Overlay].node {
        horizontalAlign = Align.Stretch
        verticalAlign = Align.Stretch
        hitModel = HitModel.None
        background[InputState.Empty] = Background.Rect(FLinearColor.rgba(0.0f, 0.0f, 0.0f, 0.3f), PaintStyle.Fill)
        visibility = Visibility.Hidden
    }

    private fun VBox.createDynamicContent(isVictory: Boolean) {
        vgap = 30
        verticalAlign = Align.Center
        horizontalAlign = Align.Center
        val paragraph = if (isVictory) {
            paragraphNode(GuiConfig.BigOverlayTextStyle.copy(color = FLinearColor.Green)) {
                +"VICTORY"
            }
        } else {
            paragraphNode(GuiConfig.BigOverlayTextStyle.copy(color = FLinearColor.Red)) {
                +"GAME OVER"
            }
        }
        paragraph.apply {
            horizontalAlign = Align.Center
        }
        paragraphNode(GuiConfig.DefaultItalicStyle) { +explanation() }.apply {
            horizontalAlign = Align.Center
        }
        standardButton("Exit Game", Align.Stretch) {
            JunManager.quitGame()
        }
    }

    private val changeVisitor = object : WorldChange.SimpleVisitor {
        override fun visit(change: WorldChange.ActiveStateChanged) {
            val activeState = change.activeState
            if (activeState is GameOverState) {
                gameOverReason = activeState.reason
                roundCount = activeState.roundCount
                mainNode.apply {
                    visibility = Visibility.Visible
                    hitModel = HitModel.Rect(IRect.Empty)
                    vBox { createDynamicContent(gameOverReason == GameOverReason.Victory) }
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
