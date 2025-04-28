package com.cerebrallychallenged.hypogean.model

/**
 * This union type is needed because of the limited overload resolution capabilities of the JVM.
 * Implementing classes have in common, that they are initialized without further parameters,
 * in contrast to, e.g. [Cell], which is initialized with a position.
 * Hence, they share the same [World.create] method.
 */
interface ItemOrOrEventOrTransient : Entity