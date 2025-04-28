package com.cerebrallychallenged.hypogean.model.maps

private open class MappingIterator<T, R, I: Iterator<T>>(
        protected val base: I,
        private val mapping: (T) -> R
): Iterator<R> {
    override fun hasNext(): Boolean = base.hasNext()

    override fun next(): R = mapping(base.next())
}

fun <T, R> Iterator<T>.map(mapping: (T) -> R): Iterator<R> = MappingIterator(this, mapping)

private class MutableMappingIterator<T, R>(
        base: MutableIterator<T>,
        mapping: (T) -> R
): MappingIterator<T, R, MutableIterator<T>>(base, mapping), MutableIterator<R> {
    override fun remove() {
        base.remove()
    }
}

@JvmName("mapMutable")
fun <T, R> MutableIterator<T>.map(mapping: (T) -> R): MutableIterator<R> = MutableMappingIterator(this, mapping)
