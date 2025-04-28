package com.cerebrallychallenged.jun.util.buffers

interface Indexed<T> : Iterable<T> {
    val size: Long

    operator fun get(index: Long): T

    operator fun set(index: Long, value: T)

    override fun iterator(): Iterator<T> = (0L until size).asSequence().map { this[it] }.iterator()
}

operator fun <T> Indexed<T>.get(index: Int): T = this[index.toLong()]

operator fun <T> Indexed<T>.set(index: Int, value: T) {
    this[index.toLong()] = value
}
