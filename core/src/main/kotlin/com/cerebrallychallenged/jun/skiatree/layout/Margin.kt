package com.cerebrallychallenged.jun.skiatree.layout

data class Margin(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    companion object {
        fun all(value: Int): Margin = Margin(value, value, value, value)
    }
}
