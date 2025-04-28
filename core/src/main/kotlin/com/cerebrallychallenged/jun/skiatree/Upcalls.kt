package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.JunWeakReference
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.instanceUpcall
import com.cerebrallychallenged.jun.unreal.skiatree.setUpcalls
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT
import kotlin.reflect.KFunction

internal class Upcalls(
    skiaTreeWidget: SkiaTreeWidget,
    tick: KFunction<*>,
    resize: KFunction<*>,
) {
    private val tickRefs = instanceUpcall(skiaTreeWidget, tick, VOID, JAVA_FLOAT)

    private val resizeRefs = instanceUpcall(skiaTreeWidget, resize, VOID, JAVA_INT, JAVA_INT)

    init {
        val widget = skiaTreeWidget.widget
        widget.setUpcalls(
            tickRefs.address(),
            resizeRefs.address(),
        )

        JunWeakReference(this) {
            widget.setUpcalls(0, 0)
        }
    }
}
