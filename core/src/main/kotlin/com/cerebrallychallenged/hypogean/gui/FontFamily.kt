package com.cerebrallychallenged.hypogean.gui

/**
 * A font family. Instances must not be constructed directly,
 * but only be obtained by querying instances from [ResourceLibrary] by a [FontFamilyResource].
 */
class FontFamily internal constructor(
    @Suppress("UNUSED_PARAMETER") // Used only to prevent accidental construction.
    resourceLibrary: ResourceLibrary,
    val name: String
)

class FontFamilyResource(
    val name: String,
    vararg val paths: String
)
