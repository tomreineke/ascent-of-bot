package com.cerebrallychallenged.jun.clipper

enum class InitOptions {
    ReverseSolution,
    StrictlySimple,
    PreserveCollinear
}

internal fun Set<InitOptions>.toMagic(): Int = sumOf { 1 shl it.ordinal }
