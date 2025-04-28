package com.cerebrallychallenged.jun.skiatree.text

import java.nio.file.Path

object GlobalFontRegistry {
    private val defaultFontManager = FontManager()

    private val assetFontManager = TypefaceFontProvider()

    val fontCollection = FontCollection().also {
        it.setDefaultFontManager(defaultFontManager)
        it.assetFontManager = assetFontManager
    }

    private val loadedPaths = mutableSetOf<Path>()

    fun loadFont(path: Path) {
        if (loadedPaths.add(path)) {
            assetFontManager.registerTypeface(defaultFontManager.load(path))
        }
    }
}
