package com.cerebrallychallenged.hypogean.app

import com.cerebrallychallenged.HypogeanApplicationFactory
import com.cerebrallychallenged.hypogean.client.launchSocketClient
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.clearLayer
import com.cerebrallychallenged.hypogean.model.ModelException
import com.cerebrallychallenged.hypogean.model.WorldFactory
import com.cerebrallychallenged.hypogean.model.setupBy
import com.cerebrallychallenged.hypogean.server.PORT_INTERVAL
import com.cerebrallychallenged.hypogean.server.Server
import com.cerebrallychallenged.hypogean.server.launchServer
import com.cerebrallychallenged.hypogean.settings.loadSettings
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.view.ViewManager
import com.cerebrallychallenged.hypogean.view.audio.AudioManager
import com.cerebrallychallenged.jun.ProgramArguments
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import io.ktor.network.sockets.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.plus

context(ApplicationStateContext)
class GameState(private val worldFactory: WorldFactory) : ApplicationState() {
    override suspend fun HypogeanApplicationFactory.execute(): ApplicationState = coroutineScope {
        val widget = HypogeanApplicationFactory.widget
        // Ensure a clean slate for the UI at the start of the game session.
        for (layerIndex in 0..127) {
            widget.layers[layerIndex].clearLayer()
        }

        // During the transition from the menu to the game, the LoadingView is displayed on a high layer (index 100).
        // To prevent "pop-in" of game UI elements while they are still being initialized behind the loading screen,
        // all layers below the LoadingView are hidden.
        // If we only cleared the layers, they would still be technically "Visible" but empty. When the game starts
        // spawning UI (like the ActionBarView), those elements would immediately appear on top of the loading
        // screen if the layers weren't hidden. The LoadingView is responsible for setting them back to
        // Visibility.Visible once the game world is fully loaded and ready to be displayed.
        for (layerIndex in 0 until GuiLayer.LoadingView.layerIndex) {
            widget.layers[layerIndex].visibility = Visibility.Hidden
        }

        val settings = rulebook.loadSettings()
        val factionName = ProgramArguments["--faction"]
        val playerFaction = if (factionName != null) {
            rulebook.factions.firstOrNull { it.name == factionName } ?: error("No faction found with id '$factionName'")
        } else {
            ProtagonistFaction
        }
        val viewManager = ViewManager(rulebook, settings, playerFaction, this)

        var server: Server? = null
        val gameScope = this + coroutineContext
        val connectAddress = ProgramArguments["--connect"]?.let { InetSocketAddress(it, PORT_INTERVAL.first) }
        if (connectAddress == null) {
            val s = gameScope.launchServer(rulebook)
            server = s
            s.addClient(viewManager.connector, true)

            s.loadWorld {
                setupBy(worldFactory)

                // When loading save games it can happen that an entity is alive, even though
                // it was removed from the game and has health 0.
                // They again have to be removed / placed to the graveyard where they've been in the previous
                // play through. Cf. Entity.isAlive()
                for (entity in entities) {
                    if (entity.health < 1) {
                        log.info { "Removing entity with 0 health: $entity." }
                        try {
                            entity.remove()
                        } catch (e: ModelException) {
                            log.info { "${e.message}" }
                        }
                    }
                }
                updateRecon()
            }
        } else {
            // By using gameScope, we tie the lifetime of the SocketClient (and the Server) directly to the execution of the GameState
            // If we used a global scope or a parent scope that doesn't terminate, the network connections would stay open in the
            // background, potentially preventing the application from truly returning to the menu or causing conflicts when you
            // try to start a new game.
            gameScope.launchSocketClient(viewManager.connector, connectAddress)
        }

        try {
            viewManager.run()
        } catch (e: Exception) {
            log.info { "viewManager.run() failed with $e" }
            throw e
        } finally {
            server?.stop()
            // Do NOT cancel gameScope here, as it's the scope of execute itself and will throw JobCancellationException.
            // The coroutineScope will naturally cancel all child jobs (server, socket client) when this function returns.
            AudioManager.stop()
            for (layerIndex in 0..127) {
                widget.layers[layerIndex].clearLayer()
            }
        }
        MainMenuState()
    }
}
