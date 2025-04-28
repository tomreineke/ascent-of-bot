package com.cerebrallychallenged.hypogean.model.containment

sealed class Placeability {
    object Ok : Placeability()
    data class Unplaceable(val obstacles: Collection<String>) : Placeability() {
        constructor(vararg obstacles: String) : this(obstacles.toList())
    }
}
