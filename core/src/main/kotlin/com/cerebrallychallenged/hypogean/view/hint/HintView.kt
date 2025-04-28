package com.cerebrallychallenged.hypogean.view.hint

import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiConfig.MainTitleTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.ParagraphNode
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.scroll.VerticalScrollView
import com.cerebrallychallenged.hypogean.gui.scroll.verticalScrollView
import com.cerebrallychallenged.hypogean.gui.title
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.util.process
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import kotlinx.coroutines.CompletableDeferred

class HintView(context: ViewFactory.Context) : View, FactionContext by context {

    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DefaultWindowStyle = Styling<Window, Unit> {
            val scale = GuiConfig.guiScale
            val width = (2000 * scale).ceilToInt()
            minWidth = width
            maxWidth = width
            horizontalAlign = Align.Center
            verticalAlign = Align.Center
            visibility = Visibility.Hidden
        }

        var windowStyle = DefaultWindowStyle

        val DefaultScrollViewStyle = Styling<VerticalScrollView, Unit> { autoScroll = false }

        var verticalScrollViewStyle: Styling<VerticalScrollView, Unit> = DefaultScrollViewStyle
    }

    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): View {
            return HintView(context)
        }
    }

    private val scrollView: VerticalScrollView

    private val mainNode = context.widget.layers[GuiLayer.Base].window(Style.windowStyle, hasCloseButton = true) {
        scrollView = verticalScrollView(Style.verticalScrollViewStyle) {
            maxHeight = context.widget.size.y / 2
        }
    }

    private val changeVisitor = object : WorldChange.SimpleSuspendVisitor {
        override suspend fun visit(change: WorldChange.ViewEventHappened) {
            val viewEvent = change.viewEvent
            if (viewEvent is Hint) {
                val dialogShown = CompletableDeferred<Unit>()
                scrollView.add(
                    ParagraphNode {
                        title("⚠ Tip", MainTitleTextStyle)
                        viewEvent.richText.process(context.viewModel)
                    }.apply {
                        horizontalAlign = Align.Center
                    }
                )
                mainNode.apply {
                    visibility = Visibility.Visible
                    closeListener = {
                        scrollView.clear()
                        dialogShown.complete(Unit)
                    }
                }
                dialogShown.await()
            }
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        if (change is ModelChange) {
            change.changes.forEach { it.accept(changeVisitor) }
        }
    }
}
