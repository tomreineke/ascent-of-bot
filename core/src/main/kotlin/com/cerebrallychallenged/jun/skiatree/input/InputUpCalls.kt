package com.cerebrallychallenged.jun.skiatree.input

import com.cerebrallychallenged.jun.input.Key
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.staticUpcall
import com.cerebrallychallenged.jun.skiatree.VOID
import com.cerebrallychallenged.jun.skiatree.geo.IPointLayout
import com.cerebrallychallenged.jun.skiatree.geo.decodeFromSegment
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.GroupLayout
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.MemoryLayout.structLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SegmentAllocator
import java.lang.foreign.SequenceLayout
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BOOLEAN
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG

internal object InputUpCalls {
    private val upcalls = listOf(
        staticUpcall(
            ::keyPressed,
            JAVA_BOOLEAN,
            JAVA_LONG,
            IPointLayout,
            JAVA_LONG,
            JAVA_INT,
            JAVA_BYTE
        ),
        staticUpcall(
            ::keyReleased,
            JAVA_BOOLEAN,
            JAVA_LONG,
            IPointLayout,
            JAVA_LONG,
            JAVA_INT,
            JAVA_BYTE
        ),
        staticUpcall(
            ::primaryPressed,
            JAVA_BOOLEAN,
            JAVA_LONG,
            IPointLayout,
            JAVA_BYTE
        ),
        staticUpcall(
            ::primaryReleased,
            VOID,
            JAVA_LONG,
            IPointLayout,
            JAVA_LONG,
            JAVA_BYTE
        ),
        staticUpcall(
            ::mouseMove,
            JAVA_BOOLEAN,
            JAVA_LONG,
            IPointLayout,
            JAVA_BYTE,
            JAVA_BOOLEAN
        ),
        staticUpcall(
            ::mouseDrag,
            VOID,
            JAVA_LONG,
            IPointLayout,
            JAVA_LONG,
            JAVA_BYTE
        ),
        staticUpcall(
            ::mouseWheel,
            JAVA_BOOLEAN,
            JAVA_LONG,
            IPointLayout,
            JAVA_FLOAT,
            JAVA_BYTE
        ),
        staticUpcall(
            ::hoverChanged,
            VOID,
            ADDRESS.withTargetLayout(sequenceLayout(JAVA_LONG)),
            JAVA_LONG,
            ADDRESS.withTargetLayout(sequenceLayout(JAVA_LONG)),
            JAVA_LONG
        ),
        staticUpcall(
            ::hasTooltip,
            JAVA_BOOLEAN,
            JAVA_LONG
        ),
        staticUpcall(
            ::showTooltip,
            VOID,
            JAVA_LONG,
            IPointLayout
        ),
        staticUpcall(
            ::hideTooltip,
            VOID,
            JAVA_LONG
        ),
        staticUpcall(
            ::resized,
            VOID,
            ADDRESS.withTargetLayout(sequenceLayout(JAVA_LONG)),
            JAVA_LONG
        ),
    )

    val layout: GroupLayout = structLayout(
        sequenceLayout(upcalls.size.toLong(), ADDRESS).withName("functions")
    )

    context(SegmentAllocator)
    fun toSegment(): MemorySegment {
        val segment = allocate(layout)
        val handle = layout.varHandle(groupElement("functions"), sequenceElement())
        for ((i, upcall) in upcalls.withIndex()) {
            handle.set(segment, i.toLong(), upcall)
        }
        return segment
    }

    @JvmStatic
    private fun keyPressed(
        receiverKey: Long,
        positionSegment: MemorySegment,
        hoveredKey: Long,
        keyIndex: Int,
        modifierMagic: Byte
    ): Boolean = exceptionGuarded(false) {
        val receiver = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded false
        val listeners = receiver.keyPressedListeners
        if (listeners.isNotEmpty()) {
            val position = Vec2i.decodeFromSegment(positionSegment)
            val hoveredNode = Node.nodeForKey(hoveredKey)
            val key = Key[keyIndex]
            val modifiers = ModifierSet(modifierMagic)
            listeners.any { it(position, hoveredNode, key, modifiers) }
        } else false
    }

    @JvmStatic
    private fun keyReleased(
        receiverKey: Long,
        positionSegment: MemorySegment,
        hoveredKey: Long,
        keyIndex: Int,
        modifierMagic: Byte
    ): Boolean = exceptionGuarded(false) {
        val receiver = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded false
        val listeners = receiver.keyReleasedListeners
        if (listeners.isNotEmpty()) {
            val position = Vec2i.decodeFromSegment(positionSegment)
            val hoveredNode = Node.nodeForKey(hoveredKey)
            val key = Key[keyIndex]
            val modifiers = ModifierSet(modifierMagic)
            listeners.any { it(position, hoveredNode, key, modifiers) }
        } else false
    }

    @JvmStatic
    private fun primaryPressed(
        receiverKey: Long,
        positionSegment: MemorySegment,
        modifierMagic: Byte
    ): Boolean = exceptionGuarded(false) {
        val receiver = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded false
        val listeners = receiver.primaryPressedListeners
        if (listeners.isNotEmpty()) {
            val position = Vec2i.decodeFromSegment(positionSegment)
            val modifiers = ModifierSet(modifierMagic)
            listeners.any { it(position, modifiers) }
        } else false
    }

    @JvmStatic
    private fun primaryReleased(
        receiverKey: Long,
        positionSegment: MemorySegment,
        hoveredNodeKey: Long,
        modifierMagic: Byte
    ): Unit = exceptionGuarded(Unit) {
        val receiver: Node = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded
        val position = Vec2i.decodeFromSegment(positionSegment)
        val hoveredNode = Node.nodeForKey(hoveredNodeKey)
        val modifiers = ModifierSet(modifierMagic)
        for (listener in receiver.primaryReleasedListeners) {
            listener(position, hoveredNode, modifiers)
        }
    }

    @JvmStatic
    private fun mouseMove(
        receiverKey: Long,
        positionSegment: MemorySegment,
        modifierMagic: Byte,
        isDragging: Boolean
    ): Boolean = exceptionGuarded(false) {
        val receiver: Node = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded false
        val position = Vec2i.decodeFromSegment(positionSegment)
        val modifiers = ModifierSet(modifierMagic)
        receiver.mouseMoveListeners.any { it(position, modifiers, isDragging) }
    }

    @JvmStatic
    private fun mouseDrag(
        receiverKey: Long,
        positionSegment: MemorySegment,
        hoveredKey: Long,
        modifierMagic: Byte
    ): Unit = exceptionGuarded(Unit) {
        val receiver: Node = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded
        val position = Vec2i.decodeFromSegment(positionSegment)
        val hoveredNode = Node.nodeForKey(hoveredKey)
        val modifiers = ModifierSet(modifierMagic)
        receiver.mouseDragListeners.any { it(position, hoveredNode, modifiers) }
    }

    @JvmStatic
    private fun mouseWheel(
        receiverKey: Long,
        positionSegment: MemorySegment,
        delta: Float,
        modifierMagic: Byte
    ): Boolean = exceptionGuarded(false) {
        val receiver: Node = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded false
        val position = Vec2i.decodeFromSegment(positionSegment)
        val modifiers = ModifierSet(modifierMagic)
        receiver.mouseWheelListeners.any { it(position, delta, modifiers) }
    }

    @JvmStatic
    private fun hoverChanged(
        unhovered: MemorySegment,
        unhoveredLen: Long,
        newlyHovered: MemorySegment,
        newlyHoveredLen: Long
    ): Unit = exceptionGuarded(Unit) {
        if (unhoveredLen > 0) {
            for (i in 0..<unhoveredLen) {
                Node.nodeForKey(unhovered.getAtIndex(JAVA_LONG, i))?.let { node ->
                    for (listener in node.hoverListeners) {
                        listener(false)
                    }
                }
            }
        }
        if (newlyHoveredLen > 0) {
            for (i in 0 until newlyHoveredLen) {
                Node.nodeForKey(newlyHovered.getAtIndex(JAVA_LONG, i))?.let { node ->
                    for (listener in node.hoverListeners) {
                        listener(true)
                    }
                }
            }
        }
    }

    @JvmStatic
    private fun hasTooltip(
        receiverKey: Long
    ): Boolean = exceptionGuarded(false) {
        val receiver: Node = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded false
        receiver.tooltipHandler != null
    }

    @JvmStatic
    private fun showTooltip(
        receiverKey: Long,
        positionSegment: MemorySegment
    ): Unit = exceptionGuarded(Unit) {
        val receiver: Node = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded
        receiver.tooltipHandler?.showTooltip(receiver, Vec2i.decodeFromSegment(positionSegment))
    }

    @JvmStatic
    private fun hideTooltip(
        receiverKey: Long,
    ): Unit = exceptionGuarded(Unit) {
        val receiver: Node = Node.nodeForKey(receiverKey) ?: return@exceptionGuarded
        receiver.tooltipHandler?.hideTooltip()
    }

    @JvmStatic
    private fun resized(
        receiver: MemorySegment,
        size: Long
    ): Unit = exceptionGuarded(Unit) {
        for (i in 0..<size) {
            Node.nodeForKey(receiver.getAtIndex(JAVA_LONG, i))?.let { node ->
                for (listener in node.resizeListeners) {
                    listener()
                }
            }
        }
    }
}

private fun <R> exceptionGuarded(errorValue: R, block: () -> R): R = try {
    block()
} catch (e: Throwable) {
    val methodName = StackWalker.getInstance().walk { it.skip(1).findFirst().get() }.methodName
    log.error { "Exception in $methodName: $e ${e.stackTraceToString()}" }
    errorValue
}
