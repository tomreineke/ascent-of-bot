package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.jun.asset.AssetLibrary

interface GuiAssetLoader {
    suspend fun load(assetLibrary: AssetLibrary)
}

class GuiAssetLoaders : SimpleObjectRegistry<GuiAssetLoader>()
