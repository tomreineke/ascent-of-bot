package com.cerebrallychallenged.hypogean.model

class ModelException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

fun modelError(message: String): Nothing = throw ModelException(message)
