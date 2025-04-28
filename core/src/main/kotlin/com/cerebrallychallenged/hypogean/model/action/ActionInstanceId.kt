package com.cerebrallychallenged.hypogean.model.action

internal data class ActionInstanceId internal constructor(val prefixIndices: List<Int>, val index: Int) {
    fun toIndexList(): List<Int> = prefixIndices + index
}
