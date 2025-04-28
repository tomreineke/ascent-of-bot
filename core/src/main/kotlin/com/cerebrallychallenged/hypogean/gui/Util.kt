package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.jun.skiatree.SkiaImage
import com.cerebrallychallenged.jun.util.getResource
import java.io.File

fun SkiaImage.Companion.loadResource(path: String): SkiaImage =
    load(File(getResource(path).toURI()).toPath().toAbsolutePath())
