package com.cerebrallychallenged.hypogean.model

class RulebookSpecificationException(message: String) : RuntimeException(message)

fun rulebookSpecificationError(message: String): Nothing = throw RulebookSpecificationException(message)