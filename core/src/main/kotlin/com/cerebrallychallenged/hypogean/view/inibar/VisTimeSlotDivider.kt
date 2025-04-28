package com.cerebrallychallenged.hypogean.view.inibar

import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.tooltip
import com.cerebrallychallenged.jun.skiatree.node.ImageNode
import kotlin.math.roundToInt

internal class VisTimeSlotDivider(iniBar: IniBarView, val iniTime: Int) : VisElement(iniBar) {
    private val imageNode = ImageNode(iniBar.timeSlotDividerImage)

    var currentLeft: Float = 0.0f
        set(value) {
            field = value
            left = value.roundToInt()
        }

    var targetLeft: Float = 0.0f

    override fun animate(deltaSeconds: Float): Boolean {
        val positionChange = currentLeft != targetLeft
        if (positionChange) {
            currentLeft = valueStep(currentLeft, targetLeft, iniBar.movementSpeed, deltaSeconds)
        }
        return positionChange
    }

    fun updateLayout(targetLeft: Float) {
        if (isNew) {
            currentLeft = targetLeft
            isNew = false
        }
        this.targetLeft = targetLeft
    }

    init {
        top = IniBarView.Style.top.scaled
        children.add(imageNode)
        tooltip = Tooltip {
            +"Round $iniTime"
        }
    }
}
