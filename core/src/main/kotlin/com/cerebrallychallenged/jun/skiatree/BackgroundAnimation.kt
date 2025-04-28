package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG

data class BackgroundAnimation(val driver: Driver, val target: Target) {
    companion object {
        @JvmStatic
        private val nodeBackgroundAttachAnimation = function(
            "skiatree_node_background_attach_animation",
            JAVA_INT,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE
        )

        @JvmStatic
        private val nodeBackgroundAnimationSetDriverLinear = function(
            "skiatree_node_background_animation_set_driver_linear",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            JAVA_INT,
            JAVA_FLOAT,
            JAVA_FLOAT
        )

        @JvmStatic
        private val nodeBackgroundAnimationSetDriverWave = function(
            "skiatree_node_background_animation_set_driver_wave",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            JAVA_INT,
            JAVA_FLOAT,
            JAVA_FLOAT
        )

        @JvmStatic
        private val nodeBackgroundAnimationSetTargetDashPhase = function(
            "skiatree_node_background_animation_set_target_dash_phase",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            JAVA_INT
        )

        @JvmStatic
        private val nodeBackgroundAnimationSetTargetOpacity = function(
            "skiatree_node_background_animation_set_target_opacity",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            JAVA_INT
        )

        @JvmStatic
        private val nodeBackgroundAnimationSetTargetColorInterpolation = function(
            "skiatree_node_background_animation_set_target_color_interpolation",
            JAVA_BYTE,
            ADDRESS,
            JAVA_LONG,
            JAVA_BYTE,
            JAVA_INT,
            Color4fLayout,
            Color4fLayout
        )
    }

    sealed class Driver {
        data class Linear(val speed: Float, val maxValue: Float = Float.POSITIVE_INFINITY) : Driver() {
            override fun setFor(node: Node, inputState: InputState, animationIndex: Int) {
                guardedUnit {
                    nodeBackgroundAnimationSetDriverLinear(
                        libraryPointer,
                        node.resource.key,
                        inputState.value.toByte(),
                        animationIndex,
                        speed,
                        maxValue
                    ) as Byte
                }
            }
        }

        data class Wave(val frequency: Float, val phase: Float = 0.0f) : Driver() {
            override fun setFor(node: Node, inputState: InputState, animationIndex: Int) {
                guardedUnit {
                    nodeBackgroundAnimationSetDriverWave(
                        libraryPointer,
                        node.resource.key,
                        inputState.value.toByte(),
                        animationIndex,
                        frequency,
                        phase
                    ) as Byte
                }
            }
        }

        abstract fun setFor(node: Node, inputState: InputState, animationIndex: Int)
    }

    sealed class Target {
        data object DashPhase : Target() {
            override fun setFor(node: Node, inputState: InputState, animationIndex: Int) {
                guardedUnit {
                    nodeBackgroundAnimationSetTargetDashPhase(
                        libraryPointer,
                        node.resource.key,
                        inputState.value.toByte(),
                        animationIndex
                    ) as Byte
                }
            }
        }

        data object Opacity : Target() {
            override fun setFor(node: Node, inputState: InputState, animationIndex: Int) {
                guardedUnit {
                    nodeBackgroundAnimationSetTargetOpacity(
                        libraryPointer,
                        node.resource.key,
                        inputState.value.toByte(),
                        animationIndex
                    ) as Byte
                }
            }
        }

        data class ColorInterpolation(val firstColor: FLinearColor, val secondColor: FLinearColor) : Target() {
            override fun setFor(node: Node, inputState: InputState, animationIndex: Int) {
                guardedUnitArena {
                    nodeBackgroundAnimationSetTargetColorInterpolation(
                        libraryPointer,
                        node.resource.key,
                        inputState.value.toByte(),
                        animationIndex,
                        firstColor.toSegment(),
                        secondColor.toSegment()
                    ) as Byte
                }
            }
        }

        abstract fun setFor(node: Node, inputState: InputState, animationIndex: Int)
    }

    internal fun setFor(node: Node, inputState: InputState) {
        val animationIndex = guarded(-1) {
            nodeBackgroundAttachAnimation(
                libraryPointer,
                node.resource.key,
                inputState.value.toByte()
            ) as Int
        }
        driver.setFor(node, inputState, animationIndex)
        target.setFor(node, inputState, animationIndex)
    }
}

fun Node.attachBackgroundAnimation(inputState: InputState, animation: BackgroundAnimation) {
    animation.setFor(this, inputState)
}
