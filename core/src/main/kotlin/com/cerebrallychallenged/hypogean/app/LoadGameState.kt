package com.cerebrallychallenged.hypogean.app

import com.cerebrallychallenged.HypogeanApplicationFactory
import com.cerebrallychallenged.hypogean.gui.*
import com.cerebrallychallenged.hypogean.gui.scroll.verticalScrollView
import com.cerebrallychallenged.hypogean.util.GameLoaderFactory
import com.cerebrallychallenged.hypogean.util.saveGamesList
import com.cerebrallychallenged.jun.skiatree.layout.Align
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlin.io.path.absolutePathString
import kotlin.io.path.nameWithoutExtension

context(ApplicationStateContext)
class LoadGameState : ApplicationState() {
    override suspend
    fun HypogeanApplicationFactory.execute(): ApplicationState? = coroutineScope {
        val completable = CompletableDeferred<ApplicationState?>()
        lateinit var window: Window

        widget.layers[GuiLayer.Base].apply {
            children.clear()
            window = window {
                verticalScrollView {
                    vgap = 10
                    for (saveGame in saveGamesList()) {
                        standardButton(saveGame.fileName.nameWithoutExtension, Align.Stretch) {
                            completable.complete(GameState(GameLoaderFactory(saveGame.absolutePathString())))
                        }
                    }
                    standardButton("Back", Align.Stretch) {
                        completable.complete(MainMenuState())
                    }
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
