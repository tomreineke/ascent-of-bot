package com.cerebrallychallenged.hypogean.view.modular

import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.scroll.VerticalScrollBar
import com.cerebrallychallenged.hypogean.gui.scroll.VerticalScrollView
import com.cerebrallychallenged.hypogean.gui.scroll.verticalScrollView
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.Node

abstract class ModularView<T>(
    private val context: ViewFactory.Context,
): View {
    abstract class Factory<T>(private val constructor: (ViewFactory.Context) -> ModularView<T>) : ViewFactory {
        lateinit var structure: context(ModuleContext) Node.(T) -> Unit

        override suspend fun create(context: ViewFactory.Context): View {
            return constructor(context).also { it.structure = structure }
        }
    }

    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DefaultWindowStyle = Styling<Window, Unit> {
            val width = 6500.scaled
            maxWidth = width
        }

        var windowStyle: Styling<Window, Unit> = DefaultWindowStyle
    }

    lateinit var structure: context(ModuleContext) Node.(T) -> Unit

    protected val viewModel = context.viewModel

    protected val world = context.world

    private var currentModules: List<ViewModule> = listOf()

    protected var isVisible: Boolean = false
        private set

    abstract val windowStyle: Styling<Window, Unit>

    open val maxContentHeight: Int
        get() = context.widget.size.y - 100

    private val mainNode: Window

    private val contentNode: Node

    init {
        context.widget.layers[GuiLayer.Window].apply {
            mainNode = window(Style.windowStyle, hasCloseButton = true) {
                verticalScrollView {
                    maxHeight = maxContentHeight
                    contentNode = this
                }.apply {
                    left = (VerticalScrollView.Style.gap + VerticalScrollBar.Style.barWidth).scaled
                }
            }.apply {
                applyStyle(windowStyle)
                visibility = Visibility.Hidden
                closeListener = {
                    hide()
                }
            }
        }
    }

    fun show(parameter: T) {
        hide()
        with(ModuleContext(context)) {
            with(contentNode) {
                // This should be structure(parameter)
                // once the multiple receivers feature is stabilized for Kotlin.
                structure(this, parameter)
            }
            isVisible = true
            mainNode.visibility = Visibility.Visible
            currentModules = modules
        }
        viewModel.updateModalViewVisibility(this, true)
    }

    fun hide() {
        isVisible = false
        mainNode.visibility = Visibility.Hidden
        contentNode.children.clear()
        currentModules = listOf()
        viewModel.updateModalViewVisibility(this, false)
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        if (change is ModelChange) {
            for (worldChange in change.changes) {
                for (module in currentModules) {
                    module.onChange(worldChange)
                }
            }
        }
    }
}

class ModuleContext(val context: ViewFactory.Context) {
    internal val modules = mutableListOf<ViewModule>()

    fun Node.include(module: ViewModule?) {
        if (module != null) {
            modules.add(module)
            children.add(module.mainNode)
        }
    }
}
