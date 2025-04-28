package com.cerebrallychallenged.hypogean.build

import com.cerebrallychallenged.jun.build.addPluginProjectFiles
import java.nio.file.Paths

fun main() {
    addPluginProjectFiles(
        "Hypogean",
        Paths.get("."),
        Paths.get("jun")
    )
}
