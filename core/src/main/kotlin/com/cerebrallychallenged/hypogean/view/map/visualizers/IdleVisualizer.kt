package com.cerebrallychallenged.hypogean.view.map.visualizers

import com.cerebrallychallenged.hypogean.view.ActionInputState
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.jun.asset.AssetLibrary

object IdleVisualizer : ActionVisualizer {
    object Factory : ActionVisualizerFactory {
        override suspend fun create(
            assetLibrary: AssetLibrary,
            actionInputState: ActionInputState
        ): ActionVisualizer = IdleVisualizer
    }

    override suspend fun update(change: ActionInputStateChanged) {
    }

    override fun dispose() {
    }
}
