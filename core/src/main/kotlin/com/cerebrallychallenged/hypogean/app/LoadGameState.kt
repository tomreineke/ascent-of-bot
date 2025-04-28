package com.cerebrallychallenged.hypogean.app

import com.cerebrallychallenged.HypogeanApplicationFactory
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.scroll.verticalScrollView
import com.cerebrallychallenged.hypogean.gui.standardButton
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.util.GameLoaderFactory
import com.cerebrallychallenged.hypogean.util.saveGamesList
import com.cerebrallychallenged.jun.skiatree.layout.Align
import kotlinx.coroutines.CompletableDeferred
import kotlin.io.path.absolutePathString
import kotlin.io.path.nameWithoutExtension

context(ApplicationStateContext)
class LoadGameState : ApplicationState() {
    override suspend
    fun HypogeanApplicationFactory.execute(): ApplicationState? {
        val completable = CompletableDeferred<ApplicationState?>()
        lateinit var window: Window
        widget.layers[GuiLayer.Base].apply {
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
        return completable.await().also {
            window.detach()
        }
    }
}
