package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.UObject

class PreloadAssetsEvent(val refs: List<UnrealRef<UObject>>) : MapViewEvent() {
    context(MapViewContext)
    override suspend fun execute() {
        for (ref in refs) {
            assetLibrary.load(ref)
        }
    }
}
