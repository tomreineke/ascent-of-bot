package com.cerebrallychallenged.hypogean.view.map.visualizers

import com.cerebrallychallenged.hypogean.view.ActionInputState
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.jun.asset.AssetLibrary

interface ActionVisualizer {
    suspend fun update(change: ActionInputStateChanged)

    fun dispose()
}

interface ActionVisualizerFactory {
    suspend fun create(assetLibrary: AssetLibrary, actionInputState: ActionInputState): ActionVisualizer
}
