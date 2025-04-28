package com.cerebrallychallenged.hypogean.view.experimental

import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.input.InputCommand
import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.input.KeyEvent
import com.cerebrallychallenged.jun.math.geo.curve.BezierCurve
import com.cerebrallychallenged.jun.math.geo.curve.toUSplineComponent
import com.cerebrallychallenged.jun.math.geo.vec
import kotlinx.coroutines.launch

object ExperimentalCommand : InputCommand("X")

class ExperimentalView(context: ViewFactory.Context) : View {
    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): View = ExperimentalView(context)
    }

    private val sessionScope = context.sessionScope

    private val assetLibrary = context.assetLibrary

    override fun onInput(inputEvent: InputEvent, commands: Collection<InputCommand>) {
        if (inputEvent is KeyEvent && inputEvent.kind == KeyEvent.Kind.PRESS) {
            if (ExperimentalCommand in commands) {
                sessionScope.launch {
                    experiment()
                }
            }
        }
    }

    private suspend fun experiment() {
        val curve = BezierCurve.from(vec(1000.0f, 0.0f, 50.0f)).quadTo(vec(0.0f, 0.0f, 50.0f), vec(0.0f, 1000.0f, 50.0f)).lineTo(vec(-100.0f, 1100.0f, 50.0f)).build()
        curve.toUSplineComponent().apply {
            drawDebug = true
        }
        val curve2 = BezierCurve.from(vec(1000.0f, 0.0f, 70.0f)).cubicTo(vec(500.0f, 0.0f, 70.0f), vec(0.0f, 500.0f, 70.0f), vec(0.0f, 1000.0f, 70.0f)).lineTo(vec(-100.0f, 1100.0f, 70.0f)).build()
        curve2.toUSplineComponent().apply {
            drawDebug = true
        }
    }
}
