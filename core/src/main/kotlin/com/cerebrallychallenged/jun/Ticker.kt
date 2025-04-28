package com.cerebrallychallenged.jun

import com.cerebrallychallenged.jun.coroutine.Unreal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun JunManager.runTicker(
        ticker: (deltaSeconds: Float) -> Boolean
) = withContext(Dispatchers.Unreal) {
    suspendCancellableCoroutine<Unit> { continuation ->
        TickerManager.addTicker { deltaSeconds ->
            try {
                val shallContinue = ticker(deltaSeconds)
                if (!shallContinue) {
                    continuation.resume(Unit)
                }
                shallContinue
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
                false
            }
        }
    }
}

internal object TickerManager {
    private val activeTickers = mutableListOf<(deltaSeconds: Float) -> Boolean>()

    internal fun addTicker(ticker: (deltaSeconds: Float) -> Boolean) {
        activeTickers.add(ticker)
    }

    internal fun runTickers(deltaSeconds: Float) {
        activeTickers.retainAll {
            it(deltaSeconds)
        }
    }
}
