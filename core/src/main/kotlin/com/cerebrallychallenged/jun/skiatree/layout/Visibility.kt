package com.cerebrallychallenged.jun.skiatree.layout

enum class Visibility {
    Visible,
    Hidden,
    Collapsed;

    val isVisible: Boolean
        get() = this == Visible

    companion object {
        fun visibleIf(condition: Boolean): Visibility = if (condition) Visible else Hidden
    }
}
