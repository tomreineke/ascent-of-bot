package com.cerebrallychallenged.hypogean.graphics.buttons

import java.nio.file.Paths

internal val ExtractedPath = Paths.get("gui-graphics/extracted")

internal val IconsPath = Paths.get("gui-graphics/icons")
internal val DerivedIconsPath = Paths.get("gui-graphics/derived-icons")
internal val BadgesPath = Paths.get("gui-graphics/icons/badges")

internal const val RelativePngOutputPath = "Images/generated"
internal val PngOutputPath = Paths.get("NonUEContent/").resolve(RelativePngOutputPath)
