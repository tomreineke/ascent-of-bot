package com.cerebrallychallenged.jun.skiatree

interface Scalable<out T> {
    fun scale(factor: Float): T
}
