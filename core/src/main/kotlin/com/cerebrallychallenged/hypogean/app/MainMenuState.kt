package com.cerebrallychallenged.hypogean.app

import com.cerebrallychallenged.HypogeanApplicationFactory
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.gui.clearLayer
import com.cerebrallychallenged.hypogean.gui.standardButton
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel
import com.cerebrallychallenged.jun.skiatree.layout.Align
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope

context(ApplicationStateContext)
class MainMenuState : ApplicationState() {
    override suspend fun HypogeanApplicationFactory.execute(): ApplicationState? = coroutineScope {
        val completable = CompletableDeferred<ApplicationState?>()
        lateinit var window: Window

        // Clear all layers to ensure no leftovers from previous states.
        for (layerIndex in 0..127) {
            widget.layers[layerIndex].clearLayer()
        }

        widget.layers[0].apply {
            window = window {
                standardButton("Start Game", Align.Stretch) {
                    completable.complete(GameState(FirstLevel))
                }
                standardButton("Select Level", Align.Stretch) {
                    completable.complete(ListWorldFactoriesState())
                }
                standardButton("Load Game", Align.Stretch) {
                    completable.complete(LoadGameState())
                }
                standardButton("Exit Game", Align.Stretch) {
                    completable.complete(null)
                }
            }
        }

        this.withTicker {
            return@withTicker completable.await().also {
                // Removes the menu window from the GUI tree so it doesn't overlap with the game UI.
                window.detach()
            }
        }
    }
}
