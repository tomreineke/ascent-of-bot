package com.cerebrallychallenged.hypogean.view.dialog

import com.cerebrallychallenged.hypogean.activestate.ActiveDialog
import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiConfig.DefaultItalicStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.DefaultTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.ParagraphNode
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.scroll.VerticalScrollView
import com.cerebrallychallenged.hypogean.gui.scroll.verticalScrollView
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.gui.withStyle
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.selectedDialogOptions
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.report.Report
import com.cerebrallychallenged.hypogean.view.util.toParagraphNode
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

class DialogView(context: ViewFactory.Context) : View, FactionContext by context {

    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DefaultWindowStyle = Styling<Window, Unit> {
            val scale = GuiConfig.guiScale
            val width = (2000 * scale).ceilToInt()
            minWidth = width
            maxWidth = width
            horizontalAlign = Align.Center
            verticalAlign = Align.Center
        }

        var windowStyle = DefaultWindowStyle

        val DefaultScrollViewStyle = Styling<VerticalScrollView, Unit> { autoScroll = true }

        var verticalScrollViewStyle: Styling<VerticalScrollView, Unit> = DefaultScrollViewStyle

        private val Gray = FLinearColor.rgb(0.7f, 0.7f, 0.7f)
        val AlreadySaidTextStyle = DefaultTextStyle.copy(color = Gray)
        val AlreadySaidItalicStyle = DefaultItalicStyle.copy(color = Gray)
    }

    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): View {
            return DialogView(context)
        }
    }

    private val scrollView: VerticalScrollView

    private var currentVisible = false

    private var currentDialog: Dialog? = null

    private val viewModel = context.viewModel

    private val mainNode = context.widget.layers[GuiLayer.Overlay].window(Style.windowStyle) {
        scrollView = verticalScrollView(Style.verticalScrollViewStyle) {
            maxHeight = context.widget.size.y / 2
        }
    }.apply {
        visibility = Visibility.Hidden
    }

    private val changeVisitor = object : WorldChange.SimpleVisitor {
        override fun visit(change: WorldChange.ViewEventHappened) {
            val viewEvent = change.viewEvent
            if (viewEvent is Report && viewEvent.affectedEntities.filterIsInstance<Actor>().any {
                it.world.activeState is ActiveDialog && it.recon == Recon.Visible
            }) {
                val dialog = (viewModel.world.activeState as? ActiveDialog)?.dialog
                // Remove content from previous dialogs.
                if (dialog != currentDialog) {
                    currentDialog = dialog
                    scrollView.clear()
                }
                scrollView.add(viewEvent.richText.toParagraphNode(context.viewModel))
            }
        }

        override fun visit(change: WorldChange.ActiveStateChanged) {
            val (state) = change

            if (state is ActiveDialog && ownFaction in state.roles.participatingFactions) {
                // Only show Dialog faction when it's the turn of the Protagonist faction
                // to avoid glitches in the form of quickly opening and closing windows.
                val protagonistFactionsTurn = state.activeActor?.faction is ProtagonistFaction
                updateVisibility(protagonistFactionsTurn)
                when (val continuation = state.continuation) {
                    is Dialog.Select -> {
                        for (option in continuation.options) {
                            val activeActor = state.activeActor
                            val alreadySaid = activeActor != null
                                && option.then is Dialog.Node
                                && Pair(continuation, option.then) in activeActor.selectedDialogOptions
                            val textStyle = if (alreadySaid) Style.AlreadySaidTextStyle else DefaultTextStyle
                            val italicStyle = if (alreadySaid) Style.AlreadySaidItalicStyle else DefaultItalicStyle
                            scrollView.add(ParagraphNode(textStyle) {
                                if (option.isSpeech) {
                                    +"“"
                                    withStyle(italicStyle) {
                                        +option.text
                                    }
                                    +"”"
                                } else {
                                    +option.text
                                }
                            }.apply {
                                primaryPressedListeners += { _, _ ->
                                    viewModel.submitDialogOption(option.uuid)
                                    scrollView.children[0].children.clear()
                                    scrollView.children[1].visibility = Visibility.Hidden
                                    true
                                }
                                background[InputState.Hovered] = Background.Rect(FLinearColor.Red, PaintStyle.Stroke)
                            })
                        }
                    }
                    is Dialog.End -> {
                        updateVisibility(false)
                    }
                }
            } else {
                updateVisibility(false)
            }
        }
    }

    private fun updateVisibility(visible: Boolean) {
        if (currentVisible != visible) {
            currentVisible = visible
            mainNode.visibility = Visibility.visibleIf(visible)
            viewModel.updateModalViewVisibility(this@DialogView, visible)
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        if (change is ModelChange) {
            change.changes.forEach { it.accept(changeVisitor) }
        }
    }
}
