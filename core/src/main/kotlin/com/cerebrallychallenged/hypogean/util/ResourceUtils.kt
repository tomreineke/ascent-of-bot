package com.cerebrallychallenged.hypogean.util

import java.net.URL

fun <T> URL.useLines(block: (Sequence<String>) -> T): T {
    return openStream().use { it.reader().useLines(block) }
}