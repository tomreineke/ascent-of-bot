package com.cerebrallychallenged.hypogean.model

/**
 * Checks if this entity has the expected id.
 */
fun Entity.checkIdMatch(expectedId: Int) {
    if (this.id != expectedId) {
        modelError("Id mismatch: got ${this.id}, expected $expectedId")
    }
}