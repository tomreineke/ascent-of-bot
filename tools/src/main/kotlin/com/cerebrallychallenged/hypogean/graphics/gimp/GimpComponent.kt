package com.cerebrallychallenged.hypogean.graphics.gimp

enum class GimpComponent(private val code: String) {
    Red("CHANNEL_RED"),
    Green("CHANNEL_GREEN"),
    Blue("CHANNEL_BLUE"),
    Gray("CHANNEL_GRAY"),
    Indexed("CHANNEL_INDEXED"),
    Alpha("CHANNEL_ALPHA");

    override fun toString(): String = code
}