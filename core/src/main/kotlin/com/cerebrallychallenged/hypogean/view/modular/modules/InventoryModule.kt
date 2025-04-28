package com.cerebrallychallenged.hypogean.view.modular.modules

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.activestate.ActiveWorldState
import com.cerebrallychallenged.hypogean.gui.GuiConfig
import com.cerebrallychallenged.hypogean.gui.GuiConfig.guiScale
import com.cerebrallychallenged.hypogean.gui.ParagraphNode
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.TextStyleResource
import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.hypogean.gui.node
import com.cerebrallychallenged.hypogean.gui.paragraphNode
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.tooltip
import com.cerebrallychallenged.hypogean.gui.vBox
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.SlotBearer
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.ItemSwapAction
import com.cerebrallychallenged.hypogean.vanilla.actions.ItemSwapActionInstance
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.view.modular.ModuleContext
import com.cerebrallychallenged.hypogean.view.modular.ViewModule
import com.cerebrallychallenged.hypogean.view.tooltip.createTooltip
import com.cerebrallychallenged.hypogean.view.util.CommonGuiImages
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.Paint
import com.cerebrallychallenged.jun.skiatree.Shader
import com.cerebrallychallenged.jun.skiatree.TileMode
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import com.cerebrallychallenged.jun.skiatree.node.ImageNode
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

context(ModuleContext)
class InventoryModule(val container: Item, val boxSize: Int, val position: Vec2i = Vec2i.ZERO) : ViewModule() {
    companion object {
        context(ModuleContext)
        operator fun invoke(
            entity: SlotBearer,
            slotName: String,
            boxSize: Int,
            position: Vec2i = Vec2i.ZERO
        ): InventoryModule? = entity.slotOrNull(slotName)?.let { InventoryModule(it, boxSize, position) }
    }

    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        const val DefaultLargeBoxSize = 450

        const val DefaultSmallBoxSize = 296

        const val DefaultBoxGap = 4

        var boxGap = DefaultBoxGap

        const val DefaultBoxPadding = 25

        var boxPadding = DefaultBoxPadding

        val DefaultBoxContainerStyle = Styling<Node, Pair<Int, Int>> { (boxSize, providedBoxesX) ->
            flow = Flow.LeftToRightThenTopToBottom
            val gap = boxGap.scaled
            hgap = gap
            vgap = gap
            background[InputState.Empty] = ResourceLibrary[CommonGuiImages.ViewFrameNinePatch, guiScale * 0.2f]
            val width = (boxSize * providedBoxesX + boxGap * (providedBoxesX - 1)).scaled
            minWidth = width
            maxWidth = width
        }

        /**
         * Parameters:
         * - boxSize: Int
         * - providedBoxesX: Int
         */
        var boxContainerStyle: Styling<Node, Pair<Int, Int>> = DefaultBoxContainerStyle

        val DefaultBoxStyle = Styling<Node, Int> { boxSize ->
            val size = boxSize.scaled
            background[InputState.Empty] = ResourceLibrary[CommonGuiImages.BoxFrameNinePatch, guiScale * 0.2f]
            minWidth = size
            maxWidth = size
            minHeight = size
            maxHeight = size
        }

        /**
         * Parameters:
         * - boxSize: Int
         */
        var boxStyle: Styling<Node, Int> = DefaultBoxStyle

        val DefaultCaptionStyle = Styling<ParagraphNode, Unit> {
            horizontalAlign = Align.Center
        }

        var captionTextStyle: TextStyleResource = GuiConfig.DefaultTextStyle

        var captionStyle: Styling<ParagraphNode, Unit> = DefaultCaptionStyle

        val DefaultObstacleBackground: Background by lazy {
            Background.Rect(Paint().apply {
                shader = Shader.linearGradient(
                    vec(0.0f, 0.0f),
                    vec(10.0f, 10.0f),
                    arrayOf(
                        FLinearColor.rgba(0.0f, 0.0f, 0.0f, 0.63f),
                        FLinearColor.rgba(0.63f, 0.0f, 0.0f, 1.0f),
                    ),
                    floatArrayOf(0.66f, 0.66f),
                    TileMode.Repeat
                )
            })
        }

        val DefaultObstacleStyle = Styling<Node, Unit> {
            background[InputState.Empty] = DefaultObstacleBackground
        }

        var obstacleStyle: Styling<Node, Unit> = DefaultObstacleStyle
    }

    inner class BoxNode : Node() {
        private val portraitNode = ImageNode().also {
            children.add(it)
            it.horizontalAlign = Align.Center
            it.verticalAlign = Align.Center
        }

        private var currentItem: Item? = null

        internal var currentSwapAction: ItemSwapActionInstance? = null

        private var isDragging = false

        fun updateItem(item: Item?) {
            if (currentItem != item) {
                currentItem = item
                portraitNode.tooltip = currentItem?.createTooltip(context.viewModel, true)
                updatePortrait()
            }
        }

        private fun updatePortrait() {
            val portraitSize = (boxSize - 2 * Style.boxPadding).scaled
            portraitNode.image = currentItem?.icon?.let { ResourceLibrary.imageWithLongerSize(it, portraitSize) }
        }

        private fun executeSwap() {
            currentSwapAction?.let { action ->
                context.viewModel.submitAction(action)
            }
        }

        init {
            boxContainer.children.add(this)
            applyStyle(Style.boxStyle, boxSize)
            primaryPressedListeners += { _, _ ->
                isDragging = false
                true
            }
            primaryReleasedListeners += { _, hoveredNode, _ ->
                if (isDragging) {
                    // The mouse has been moved
                    isDragging = false
                    ((hoveredNode as? BoxNode) ?: (hoveredNode?.parent as? BoxNode))?.executeSwap()
                } else {
                    executeSwap()
                }
                true
            }
            mouseDragListeners += { _, _, _ ->
                if (!isDragging) {
                    isDragging = true
                    executeSwap()
                }
                true
            }
        }
    }

    private fun updateActiveState(state: ActiveWorldState?) {
        for (box in boxes.values) {
            box.currentSwapAction = null
            // Reset
        }
        if (state is ActiveActorState && state.activeActor.isOwn) {
            val availableActions = state.availableActions

//            for ((entity, powerLevel) in availableActions.attributeAdjustingInstances(Entity::adjustedPowerLevel)) {
//
//            }
            val swapActions = availableActions.groupedByAction[ItemSwapAction].groupedByTarget[container]
            for (swapAction in swapActions.instances.filterIsInstance<ItemSwapActionInstance>()) {
                boxes[swapAction.boxPosition]?.currentSwapAction = swapAction
            }
            val obstacles = swapActions.obstacleDescriptions
            if (obstacles.isNotEmpty()) {
                obstacleNode.visibility = Visibility.Visible
                obstacleNode.hitModel = HitModel.Rect(IRect.Empty)
                obstacleNode.tooltip = Tooltip {
                    +obstacles.joinToString("\n")
                }
            } else {
                obstacleNode.visibility = Visibility.Hidden
                obstacleNode.hitModel = HitModel.None
                obstacleNode.tooltip = null
            }
        }
    }

    private val boxContainer: Node

    private val obstacleNode: Node

    override val mainNode: Node = Node().apply {
        left = position.x.scaled
        top = position.y.scaled
        vBox {
            boxContainer = node {
                applyStyle(Style.boxContainerStyle, Pair(boxSize, container.providedBoxes.x))
            }
            paragraphNode(Style.captionTextStyle, Style.captionStyle) {
                +container.name
            }
        }
        obstacleNode = node {
            horizontalAlign = Align.Stretch
            verticalAlign = Align.Stretch
            hitModel = HitModel.None
            visibility = Visibility.Hidden
            applyStyle(Style.obstacleStyle)
        }
    }

//    private val boxContainer: Node = Node().apply {
//        mainNode.children.add(this)
//        applyStyle(Style.boxContainerStyle, Pair(boxSize, container.providedBoxes.x))
//    }

//    private val obstacleNode: Node = Node().apply {
//        mainNode.children.add(this)
//        horizontalAlign = Align.Stretch
//        verticalAlign = Align.Stretch
//        hitModel = HitModel.None
//        visibility = Visibility.Hidden
//    }

//    init {
//        mainNode.children.add(ParagraphNode(GuiConfig.DefaultTextStyle) {
//            +container.name
//        }.apply { horizontalAlign = Align.Center })
//    }

    private val boxes = mutableMapOf<Vec2i, BoxNode>()

    private val changeListener = object : WorldChange.SimpleVisitor {
        override fun visit(change: WorldChange.ItemMove) {
            val (item, oldPosition, newPosition) = change
            if (oldPosition?.container == container) {
                boxes[oldPosition.boxPosition]?.updateItem(null)
            }
            if (newPosition?.container == container) {
                boxes[newPosition.boxPosition]?.updateItem(item)
            }
        }

        override fun visit(change: WorldChange.ActiveStateChanged) {
            updateActiveState(change.activeState)
        }
    }

    override fun onChange(change: WorldChange) {
        change.accept(changeListener)
    }

    init {
        val providedBoxes = container.providedBoxes
        for (y in 0 until providedBoxes.y) {
            for (x in 0 until providedBoxes.x) {
                boxes[vec(x, y)] = BoxNode()
            }
        }
        for (item in container.containedItems) {
            boxes[requireNotNull(item.containerPosition).boxPosition]?.updateItem(item)
        }
        updateActiveState(context.world.activeState)
    }
}
