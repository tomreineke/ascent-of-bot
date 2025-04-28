package com.cerebrallychallenged.hypogean.util

import com.cerebrallychallenged.hypogean.model.Entity
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList

inline fun <T> Iterable<T>.mapToInt(transform: (T) -> Int): IntList = IntArrayList().apply {
    for (element in this@mapToInt) {
        add(transform(element))
    }
}

fun <T : Entity> Iterable<T>.ids(): IntList = mapToInt { it.id }

inline fun <reified T : Any> T.toSimpleClassString(): String {
    val stringArray = (this::class.simpleName ?: "").split(Regex("(?<!^)(?=[A-Z])"))
    return stringArray.joinToString(" ") {
        if (it == "Over") { "over" } else it
    }
}
