package com.cerebrallychallenged.jun

object JunClassLoader {
    @Suppress("ObjectPropertyName")
    private var _classLoader: ClassLoader? = null

    var classLoader: ClassLoader
        get() = _classLoader ?: ClassLoader.getSystemClassLoader()
        set(value) {
            _classLoader = value
        }
}
