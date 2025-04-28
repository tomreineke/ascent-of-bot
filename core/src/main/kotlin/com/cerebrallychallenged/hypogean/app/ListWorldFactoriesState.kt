package com.cerebrallychallenged.hypogean.app

import com.cerebrallychallenged.HypogeanApplicationFactory
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.scroll.verticalScrollView
import com.cerebrallychallenged.hypogean.gui.standardButton
import com.cerebrallychallenged.hypogean.gui.window
import com.cerebrallychallenged.hypogean.model.WorldFactories
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.jun.skiatree.layout.Align
import kotlinx.coroutines.CompletableDeferred

context(ApplicationStateContext)
class ListWorldFactoriesState : ApplicationState() {
    override suspend fun HypogeanApplicationFactory.execute(): ApplicationState? {
        val completable = CompletableDeferred<ApplicationState?>()
        lateinit var window: Window
        widget.layers[GuiLayer.Base].apply {
            window = window {
                verticalScrollView {
                    vgap = 10
                    for (factory in rulebook.feature<WorldFactories>()) {
                        standardButton(factory.javaClass.simpleName, Align.Stretch) {
                            completable.complete(GameState(factory))
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
