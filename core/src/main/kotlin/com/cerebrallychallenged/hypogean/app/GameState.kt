package com.cerebrallychallenged.hypogean.app

import com.cerebrallychallenged.HypogeanApplicationFactory
import com.cerebrallychallenged.hypogean.client.launchSocketClient
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.model.ModelException
import com.cerebrallychallenged.hypogean.model.WorldFactory
import com.cerebrallychallenged.hypogean.model.setupBy
import com.cerebrallychallenged.hypogean.server.PORT_INTERVAL
import com.cerebrallychallenged.hypogean.server.launchServer
import com.cerebrallychallenged.hypogean.settings.loadSettings
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.view.ViewManager
import com.cerebrallychallenged.jun.ProgramArguments
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.skiatree.layout.Visibility
import io.ktor.network.sockets.*
import kotlinx.coroutines.coroutineScope

context(ApplicationStateContext)
class GameState(private val worldFactory: WorldFactory) : ApplicationState() {
    override suspend fun HypogeanApplicationFactory.execute(): ApplicationState = coroutineScope {
        val widget = HypogeanApplicationFactory.widget
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

        val connectAddress = ProgramArguments["--connect"]?.let { InetSocketAddress(it, PORT_INTERVAL.first) }
        if (connectAddress == null) {
            val server = launchServer(rulebook)
            server.addClient(viewManager.connector, true)

            server.loadWorld {
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
            launchSocketClient(viewManager.connector, connectAddress)
        }

        viewManager.run()
        MainMenuState()
    }
}
