package com.cerebrallychallenged.hypogean.view.util.mouse

import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.unreal.EMouseCursor
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import com.cerebrallychallenged.jun.unreal.slate.EFocusCause
import com.cerebrallychallenged.jun.unreal.slate.FSlateApplication

sealed class MouseCursor {
    private class HardwareCursor(private val cursor: EMouseCursor) : MouseCursor() {
        override fun makeCurrent() {
            playerController.currentMouseCursor = cursor
            FSlateApplication.setUserFocusToGameViewport(0, EFocusCause.SetDirectly)
        }

        fun setData(path: String, hotspot: Vec2f) {
            gameViewport.setHardwareCursor(cursor, path, hotspot)
        }
    }

    private class LRUCursor(private val path: String, private val hotspot: Vec2f) : MouseCursor() {
        override fun makeCurrent() {
            val foundEntry = lruDeque.firstOrNull { it.lruCursor == this }
            val hardwareCursor = if (foundEntry != null) {
                foundEntry.hardwareCursor
            } else {
                val recycledEntry = lruDeque.removeLast()
                recycledEntry.lruCursor = this
                lruDeque.addFirst(recycledEntry)
                recycledEntry.hardwareCursor.apply {
                    setData(path, hotspot)
                }
            }
            hardwareCursor.makeCurrent()
        }
    }

    private class LRUEntry(val hardwareCursor: HardwareCursor, var lruCursor: LRUCursor? = null)

    companion object {
        val Default: MouseCursor = HardwareCursor(EMouseCursor.Default)
        private val Pan: MouseCursor = HardwareCursor(EMouseCursor.CardinalCross)
        val Obstacle: MouseCursor = HardwareCursor(EMouseCursor.SlashedCircle)

        operator fun invoke(path: String, hotspot: Vec2f): MouseCursor = LRUCursor(path, hotspot)

        var currentCursor: MouseCursor = Default
            set(value) {
                field = value
                update()
            }

        var isPanning: Boolean = false
            set(value) {
                field = value
                update()
            }

        private var actualCurrentCursor: MouseCursor = Default

        private fun update() {
            val newCursor = if (isPanning) Pan else currentCursor
            if (newCursor != actualCurrentCursor) {
                actualCurrentCursor = newCursor
                newCursor.makeCurrent()
            }
        }

        private val playerController = UGameplayStatics.getPlayerController(playerIndex = 0)

        private val gameViewport = requireNotNull(JunManager.GEngine.gameViewport)

        private val lruDeque = ArrayDeque<LRUEntry>().apply {
            val cursors = listOf(
                EMouseCursor.ResizeLeftRight,
                EMouseCursor.ResizeUpDown,
                EMouseCursor.ResizeSouthEast,
                EMouseCursor.ResizeSouthWest,
                EMouseCursor.Crosshairs,
                EMouseCursor.Hand,
                EMouseCursor.GrabHand,
                EMouseCursor.GrabHandClosed,
                EMouseCursor.EyeDropper
            )
            for (cursor in cursors) {
                add(LRUEntry(HardwareCursor(cursor)))
            }
        }
    }

    internal abstract fun makeCurrent()
}
