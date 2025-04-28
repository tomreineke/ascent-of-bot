package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.HypogeanApplicationFactory
import com.cerebrallychallenged.hypogean.client.Client
import com.cerebrallychallenged.hypogean.client.ClientConnector
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.Rulebook
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.hypogean.settings.Settings
import com.cerebrallychallenged.hypogean.view.conf.ViewsDefinitions
import com.cerebrallychallenged.hypogean.view.input.KeyBindings
import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.coroutine.Unreal
import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.input.KeyEvent
import com.cerebrallychallenged.jun.runTicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ViewManager private constructor(
        settings: Settings,
        private val viewModel: ViewModel,
        private val views: List<View>,
) {
    companion object {
        internal suspend operator fun invoke(
                rulebook: Rulebook,
                settings: Settings,
                faction: Faction,
                sessionScope: CoroutineScope
        ): ViewManager = withContext(Dispatchers.Unreal) {
            val world = rulebook.createWorld(isPrimary = false)
            val client = Client(world, faction)
            val viewModel = ViewModel(client, world, faction, sessionScope)

            val assetLibrary = AssetLibrary(sessionScope)
            val definition = rulebook.feature<ViewsDefinitions>().single()
            val views = definition.createViews(world, viewModel, assetLibrary, HypogeanApplicationFactory.widget, sessionScope)
            ViewManager(settings, viewModel, views)
        }
    }

    private val client: Client
        get() = viewModel.client

    internal val connector: ClientConnector
        get() = client.connector

    private val keyBindings = settings[KeyBindings.Key]

    suspend fun run() = coroutineScope {
        var running = true
        JunManager.inputManager.inputListeners.add(::onInput)
        launch {
            JunManager.runTicker { deltaSeconds ->
                viewModel.onTick(deltaSeconds)
                for (view in views) {
                    view.onTick(deltaSeconds)
                }
                running
            }
        }
        launch {
            viewModel.changes.collect { change ->
                val token = Any()
                viewModel.pauseAnimation(token)
                coroutineScope {
                    for (view in views) {
                        launch {
                            view.onViewModelChange(change)
                        }
                    }
                }
                viewModel.resumeAnimation(token)
            }
        }
        try {
            client.worldUpdates.collect(viewModel::onChangeSchedule)
        } finally {
            running = false
        }
    }

    private fun onInput(inputEvent: InputEvent) {
        val commands = if (inputEvent is KeyEvent) keyBindings[inputEvent.key] else listOf()
        for (view in views) {
            view.onInput(inputEvent, commands)
        }
        viewModel.onInput(inputEvent, commands)
    }
}
