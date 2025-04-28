package com.cerebrallychallenged.jun.util

import com.cerebrallychallenged.jun.JunClassLoader
import java.net.URL

fun getNullableResource(path: String): URL? = JunClassLoader.classLoader.getResource(path)

fun getResource(path: String): URL = getNullableResource(path) ?: error("Resource $path not found")
