package com.cerebrallychallenged.jun.skiatree.node

import com.cerebrallychallenged.jun.CloseableKey
import com.cerebrallychallenged.jun.input.Key
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputStateMap
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.SkiaTreeWidget
import com.cerebrallychallenged.jun.skiatree.geo.IPointLayout
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.geo.toSegment
import com.cerebrallychallenged.jun.skiatree.guardedKey
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import com.cerebrallychallenged.jun.skiatree.guardedUnitArena
import com.cerebrallychallenged.jun.skiatree.input.HitModel
import com.cerebrallychallenged.jun.skiatree.input.ModifierSet
import com.cerebrallychallenged.jun.skiatree.input.TooltipHandler
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Flow
import com.cerebrallychallenged.jun.skiatree.layout.Margin
import com.cerebrallychallenged.jun.skiatree.layout.NodeBooleanParameterDelegate
import com.cerebrallychallenged.jun.skiatree.layout.NodeEnumParameterDelegate
import com.cerebrallychallenged.jun.skiatree.layout.NodeIntParameterDelegate
import com.cerebrallychallenged.jun.skiatree.layout.NodeStringParameterDelegate
import com.cerebrallychallenged.jun.skiatree.layout.NodeVec2iParameterDelegate
import com.cerebrallychallenged.jun.skiatree.layout.ReadableNodeIRectParameterDelegate
import com.cerebrallychallenged.jun.skiatree.layout.ReadableNodeVec2iParameterDelegate
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.lang.invoke.MethodHandle
import java.lang.ref.WeakReference

open class Node private constructor(
    internal val resource: CloseableKey,
) : AutoCloseable by resource {
    companion object {
        private val globalNodes: MutableMap<Long, WeakReference<Node>> = mutableMapOf()

        @JvmStatic
        private val nodeDelete = function(
            "skiatree_node_delete",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG
        )

        @JvmStatic
        private val nodeNew = function(
            "skiatree_node_new",
            JAVA_LONG,
            ADDRESS
        )

        @JvmStatic
        private val nodeDetach = function(
            "skiatree_node_detach",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG
        )

        @JvmStatic
        private val nodeAttachChild = function(
            "skiatree_node_attach_child",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_LONG
        )

        @JvmStatic
        private val nodeAttachChildAtPosition = function(
            "skiatree_node_attach_child_at_position",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_LONG,
            JAVA_LONG
        )

        @JvmStatic
        private val nodeSetVisualTranslation = function(
            "skiatree_node_set_visual_translation",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            IPointLayout
        )

        @JvmStatic
        private val nodeRemoveTranslation = function(
            "skiatree_node_remove_visual_translation",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE
        )

        @JvmStatic
        private val leftDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_left",
            "skiatree_node_layout_set_left"
        )

        @JvmStatic
        private val topDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_top",
            "skiatree_node_layout_set_top"
        )

        @JvmStatic
        private val rightDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_right",
            "skiatree_node_layout_set_right"
        )

        @JvmStatic
        private val bottomDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_bottom",
            "skiatree_node_layout_set_bottom"
        )

        @JvmStatic
        private val minWidthDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_min_width",
            "skiatree_node_layout_set_min_width"
        )

        @JvmStatic
        private val minHeightDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_min_height",
            "skiatree_node_layout_set_min_height"
        )

        @JvmStatic
        private val maxWidthDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_max_width",
            "skiatree_node_layout_set_max_width"
        )

        @JvmStatic
        private val maxHeightDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_max_height",
            "skiatree_node_layout_set_max_height"
        )

        @JvmStatic
        private val flowDelegate = object : NodeEnumParameterDelegate<Flow>(enumValues()) {
            override val getter: MethodHandle = createGetter("skiatree_node_layout_get_flow")

            override val setter: MethodHandle = createSetter("skiatree_node_layout_set_flow")
        }

        @JvmStatic
        private val horizontalAlignDelegate = object : NodeEnumParameterDelegate<Align>(enumValues()) {
            override val getter: MethodHandle = createGetter("skiatree_node_layout_get_horizontal_align")

            override val setter: MethodHandle = createSetter("skiatree_node_layout_set_horizontal_align")
        }

        @JvmStatic
        private val verticalAlignDelegate = object : NodeEnumParameterDelegate<Align>(enumValues()) {
            override val getter: MethodHandle = createGetter("skiatree_node_layout_get_vertical_align")

            override val setter: MethodHandle = createSetter("skiatree_node_layout_set_vertical_align")
        }

        @JvmStatic
        private val hgapDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_hgap",
            "skiatree_node_layout_set_hgap"
        )

        @JvmStatic
        private val vgapDelegate = NodeIntParameterDelegate(
            "skiatree_node_layout_get_vgap",
            "skiatree_node_layout_set_vgap"
        )

        @JvmStatic
        private val layoutTranslationDelegate = NodeVec2iParameterDelegate(
            "skiatree_node_layout_get_translation",
            "skiatree_node_layout_set_translation"
        )

        @JvmStatic
        private val isScrollViewportDelegate = NodeBooleanParameterDelegate(
            "skiatree_node_layout_get_is_scroll_viewport",
            "skiatree_node_layout_set_is_scroll_viewport"
        )

        @JvmStatic
        private val visibilityDelegate = object : NodeEnumParameterDelegate<Visibility>(enumValues()) {
            override val getter: MethodHandle = createGetter("skiatree_node_layout_get_visibility")

            override val setter: MethodHandle = createSetter("skiatree_node_layout_set_visibility")
        }

        @JvmStatic
        private val nodeForceLayout = function(
            "skiatree_node_force_layout",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            IPointLayout
        )

        @JvmStatic
        private val absolutePositionDelegate = ReadableNodeVec2iParameterDelegate(
            "skiatree_node_get_absolute_position"
        )

        @JvmStatic
        private val contentSizeDelegate = ReadableNodeVec2iParameterDelegate("skiatree_node_get_content_size")

        @JvmStatic
        private val sizeDelegate = ReadableNodeVec2iParameterDelegate("skiatree_node_get_size")

        @JvmStatic
        private val boundsDelegate = ReadableNodeIRectParameterDelegate("skiatree_node_get_bounds")

        @JvmStatic
        private val debugNameDelegate = NodeStringParameterDelegate(
            "skiatree_node_get_debug_name",
            "skiatree_node_set_debug_name"
        )

        @JvmStatic
        private val consumesHoverDelegate = NodeBooleanParameterDelegate(
            "skiatree_node_get_consumes_hover",
            "skiatree_node_set_consumes_hover"
        )

        @JvmStatic
        private val inheritsInputStateDelegate = NodeBooleanParameterDelegate(
            "skiatree_node_get_inherits_input_state",
            "skiatree_node_set_inherits_input_state"
        )

        @JvmStatic
        private val isSelectedDelegate = NodeBooleanParameterDelegate(
            "skiatree_node_get_is_selected",
            "skiatree_node_set_is_selected"
        )

        internal fun nodeForKey(key: Long): Node? = globalNodes[key]?.get()
    }

    constructor() : this(
        guardedKey(nodeDelete) {
            nodeNew(libraryPointer) as Long
        }
    )

    init {
        @Suppress("LeakingThis") // globalNodes is used only in the game thread and is not accessed during construction.
        globalNodes[resource.key] = WeakReference(this)
    }

    var parent: Node? = null

    inner class NodeChildren : AbstractList<Node>() {
        private val children: MutableList<Node> = mutableListOf()

        override val size: Int
            get() = children.size

        override fun get(index: Int): Node = children[index]

        internal fun internalRemove(child: Node) {
            children.remove(child)
        }

        fun remove(child: Node) {
            internalRemove(child)
            child.detach()
        }

        fun add(child: Node) {
            child.parent?.children?.internalRemove(child)
            guardedUnit {
                nodeAttachChild(libraryPointer, resource.key, child.resource.key) as Byte
            }
            child.parent = this@Node
            child.widget = widget
            children.add(child)
        }

        fun add(index: Int, child: Node) {
            child.parent?.children?.internalRemove(child)
            guardedUnit {
                nodeAttachChildAtPosition(libraryPointer, resource.key, child.resource.key, index.toLong()) as Byte
            }
            child.parent = this@Node
            child.widget = widget
            children.add(index, child)
        }

        fun clear() {
            for (child in children.toList()) {
                child.detach()
            }
        }
    }

    val children = NodeChildren()

    fun detach() {
        guardedUnit {
            nodeDetach(libraryPointer, resource.key) as Byte
        }
        parent?.children?.internalRemove(this)
        parent = null
        widget = null
    }

    fun isAncestorOf(node: Node?): Boolean {
        var n: Node? = node
        while (n != null) {
            if (n == this) return true
            n = n.parent
        }
        return false
    }

    override fun toString(): String = javaClass.simpleName

    var widget: SkiaTreeWidget? = null
        internal set(value) {
            field = value
            for (child in children) {
                if (child.widget != value) {
                    child.widget = value
                }
            }
            core.setWidget(value)
        }

    val background = InputStateMap<Background> { inputState, background ->
        if (background == null) {
            Background.removeFor(this, inputState)
        } else {
            background.setFor(this, inputState)
        }
    }

    val visualTranslation = InputStateMap<Vec2i> { inputState, translation ->
        val inputStateByte = inputState.value.toByte()
        if (translation == null) {
            guardedUnit {
                nodeRemoveTranslation(libraryPointer, resource.key, inputStateByte) as Byte
            }
        } else {
            guardedUnitArena {
                nodeSetVisualTranslation(libraryPointer, resource.key, inputStateByte, translation.toSegment()) as Byte
            }
        }
    }

    var left: Int by leftDelegate

    var top: Int by topDelegate

    var right: Int by rightDelegate

    var bottom: Int by bottomDelegate

    var margin: Margin
        get() = Margin(left, top, right, bottom)
        set(value) {
            left = value.left
            top = value.top
            right = value.right
            bottom = value.bottom
        }

    var minWidth: Int by minWidthDelegate

    var minHeight: Int by minHeightDelegate

    var minSize: Vec2i
        get() = vec(minWidth, minHeight)
        set(value) {
            minWidth = value.x
            minHeight = value.y
        }

    var maxSize: Vec2i
        get() = vec(maxWidth, maxHeight)
        set(value) {
            maxWidth = value.x
            maxHeight = value.y
        }

    var maxWidth: Int by maxWidthDelegate

    var maxHeight: Int by maxHeightDelegate

    var flow: Flow by flowDelegate

    var horizontalAlign: Align by horizontalAlignDelegate

    var verticalAlign: Align by verticalAlignDelegate

    var hgap: Int by hgapDelegate

    var vgap: Int by vgapDelegate

    var layoutTranslation: Vec2i by layoutTranslationDelegate

    var isScrollViewport: Boolean by isScrollViewportDelegate

    var visibility: Visibility by visibilityDelegate

    fun forceLayout(size: Vec2i) {
        guardedUnitArena {
            nodeForceLayout(libraryPointer, resource.key, size.toSegment()) as Byte
        }
    }

    val absolutePosition: Vec2i by absolutePositionDelegate

    val contentSize: Vec2i by contentSizeDelegate

    val size: Vec2i by sizeDelegate

    val bounds: IRect by boundsDelegate

    var debugName: String by debugNameDelegate

    var core: NodeCore = NodeCore.Null
        set(value) {
            field = value
            value.setFor(this)
        }

    var hitModel: HitModel = HitModel.Rect()
        set(value) {
            field = value
            value.setFor(this)
        }

    var consumesHover: Boolean by consumesHoverDelegate

    var inheritsInputState: Boolean by inheritsInputStateDelegate

    var isSelected: Boolean by isSelectedDelegate

    var keyPressedListeners: List<(position: Vec2i, hoveredNode: Node?, key: Key, modifiers: ModifierSet) -> Boolean> = listOf()

    var keyReleasedListeners: List<(position: Vec2i, hoveredNode: Node?, key: Key, modifiers: ModifierSet) -> Boolean> = listOf()

    var primaryPressedListeners: List<(position: Vec2i, modifiers: ModifierSet) -> Boolean> = listOf()

    var primaryReleasedListeners: List<(position: Vec2i, hoveredNode: Node?, modifiers: ModifierSet) -> Boolean> = listOf()

    var mouseMoveListeners: List<(position: Vec2i, modifiers: ModifierSet, isDragging: Boolean) -> Boolean> = listOf()

    var mouseDragListeners: List<(position: Vec2i, hoveredNode: Node?, modifiers: ModifierSet) -> Boolean> = listOf()

    var mouseWheelListeners: List<(position: Vec2i, delta: Float, modifiers: ModifierSet) -> Boolean> = listOf()

    var hoverListeners: List<(hovered: Boolean) -> Unit> = listOf()

    var tooltipHandler: TooltipHandler? = null

    var resizeListeners: List<() -> Unit> = listOf()

    init {
        debugName = this.javaClass.simpleName
    }
}
