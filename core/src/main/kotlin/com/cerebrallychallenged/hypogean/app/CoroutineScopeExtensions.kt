package com.cerebrallychallenged.hypogean.app

import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.runTicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> CoroutineScope.withTicker(block: suspend () -> T): T {
    // The tickerJob is a background loop that tells the UI to repaint and process layouts.
    // It's started when the MainMenuState begins.
    val tickerJob = launch {
        try {
            JunManager.runTicker { true }
        } catch (_: CancellationException) {
        }
    }

    return try {
        block()
    } finally {
        // When a button is clicked (e.g., "Start Game"), the completable is resolved, and
        // execute() returns the next state (GameState). Without the finally block, the
        // tickerJob from the menu would keep running in the background forever, consuming
        // CPU cycles and potentially interfering with the GameState's own UI updates.
        tickerJob.cancel()
    }
}