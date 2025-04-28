package com.cerebrallychallenged.hypogean.view.report

import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.paragraphNode
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.scroll.VerticalScrollView
import com.cerebrallychallenged.hypogean.gui.scroll.verticalScrollView
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.vanilla.cascade.showHealthChangesInDamageReport
import com.cerebrallychallenged.hypogean.view.ModalViewVisibilityChanged
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.util.toParagraphNode
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.PaintStyle
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

class ReportView(context: ViewFactory.Context) : View, FactionContext by context {

    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DefaultWindowStyle = Styling<Window, Unit> {
            val scale = GuiConfig.guiScale
            val width = (2000 * scale).ceilToInt()
            minWidth = width
            maxWidth = width
            val overshoot = (-400 * scale).ceilToInt()
            right = overshoot
            bottom = overshoot
            horizontalAlign = Align.Max
            verticalAlign = Align.Max
        }

        var windowStyle = DefaultWindowStyle

        val DefaultScrollViewStyle = Styling<VerticalScrollView, Unit> {
            bottom = 350.scaled
            right = 350.scaled
            autoScroll = true
//            background[InputState.Empty] = Background.Rect(Paint().apply { color = FLinearColor.Blue })
        }

        var verticalScrollViewStyle: Styling<VerticalScrollView, Unit> = DefaultScrollViewStyle

        private val LightGray = FLinearColor.rgb(0.6f, 0.6f, 0.6f)

        val DefaultHLineStyle = Styling<Node, Unit> {
            background[InputState.Empty] = Background.Rect(LightGray, PaintStyle.Fill)
            minHeight = 3
            maxHeight = 3
            top = 5
            bottom = 5
            left = 10
            right = 10
            horizontalAlign = Align.Stretch
        }

        var hLineStyle: Styling<Node, Unit> = DefaultHLineStyle
    }

    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): View {
            return ReportView(context)
        }
    }

    private val scrollView: VerticalScrollView

    private val mainNode = context.widget.layers[GuiLayer.Base].window(Style.windowStyle) {
        scrollView = verticalScrollView(Style.verticalScrollViewStyle) {
            maxHeight = context.widget.size.y / 2
        }
    }

    private val changeVisitor = object : WorldChange.SimpleSuspendVisitor {
        override suspend fun visit(change: WorldChange.IniIncTime) {
            scrollView.add(Node().apply {
                children.add(Node().apply {
                    applyStyle(Style.hLineStyle)
                })
                children.add(Node().apply {
                    top = 30.scaled
                    horizontalAlign = Align.Center
                    this.paragraphNode {
                        +"Round ${change.newIniTime}"
                    }
                })
                horizontalAlign = Align.Stretch
            })
        }
        override suspend fun visit(change: WorldChange.ViewEventHappened) {
            val viewEvent = change.viewEvent
            if (viewEvent is Report && viewEvent.affectedEntities.any {
                it.showHealthChangesInDamageReport && it.recon == Recon.Visible
            }) {
                scrollView.add(viewEvent.richText.toParagraphNode(context.viewModel))
            }
        }
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        when (change) {
            is ModelChange -> change.changes.forEach { it.accept(changeVisitor) }
            is ModalViewVisibilityChanged -> {
                mainNode.visibility = Visibility.visibleIf(!change.isAnyModalViewVisible)
            }
            else -> {}
        }
    }
}
