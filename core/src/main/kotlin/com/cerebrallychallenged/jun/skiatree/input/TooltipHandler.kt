package com.cerebrallychallenged.jun.skiatree.input

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.node.Node

interface TooltipHandler {
    fun showTooltip(receiver: Node, mousePosition: Vec2i)

    fun hideTooltip()
}
