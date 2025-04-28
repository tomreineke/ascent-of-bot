package com.cerebrallychallenged.hypogean.app

import com.cerebrallychallenged.HypogeanApplicationFactory
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.gui.standardButton
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel
import com.cerebrallychallenged.jun.skiatree.layout.Align
import kotlinx.coroutines.CompletableDeferred

context(ApplicationStateContext)
class MainMenuState : ApplicationState() {
    override suspend fun HypogeanApplicationFactory.execute(): ApplicationState? {
        val completable = CompletableDeferred<ApplicationState?>()
        lateinit var window: Window
        widget.layers[0].apply {
            window = window {
                standardButton("Start Game", Align.Stretch) {
                    completable.complete(GameState(FirstLevel))
                }
//                standardButton("List Levels...", Align.Stretch) {
//                    completable.complete(ListWorldFactoriesState())
//                }
                standardButton("Load Game", Align.Stretch) {
                    completable.complete(LoadGameState())
                }
                standardButton("Exit", Align.Stretch) {
                    completable.complete(null)
                }
            }
        }
        return completable.await().also {
            window.detach()
        }
    }
}
