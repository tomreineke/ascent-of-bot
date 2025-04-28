package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.geo.IPointLayout
import com.cerebrallychallenged.jun.skiatree.geo.decodeFromSegment
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.skiatree.SJunSkiaTreeWidget
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS

class SkiaTreeWidget {
    companion object {
        @JvmStatic
        private val getMousePosition = function(
            "skiatree_forest_get_mouse_position",
            IPointLayout,
            ADDRESS
        )
    }

    val layers = Layers(this)

    val widget: TSharedRef<SJunSkiaTreeWidget> = SJunSkiaTreeWidget.createBySNew(layers)

    @Suppress("unused") // Keep upcall stubs alive
    private val upcalls = Upcalls(
        this,
        ::tick,
        ::resize,
    )

    var size: Vec2i = vec(800, 600)
        private set

    val mousePosition: Vec2i
        get() = confinedArena {
            val segment = getMousePosition(this, layers.address) as MemorySegment
            Vec2i.decodeFromSegment(segment)
        }

    private fun <R> exceptionGuarded(errorValue: R, block: () -> R): R = try {
        block()
    } catch (e: Throwable) {
        val methodName = StackWalker.getInstance().walk { it.skip(1).findFirst().get() }.methodName
        log.error { "Exception in $methodName of SkiaTreeWidget ${widget.directPtr}: $e" }
        errorValue
    }

    private fun tick(delta: Float): Unit = exceptionGuarded(Unit) {
    }

    private fun resize(width: Int, height: Int): Unit = exceptionGuarded(Unit) {
        size = vec(width, height)
    }
}
