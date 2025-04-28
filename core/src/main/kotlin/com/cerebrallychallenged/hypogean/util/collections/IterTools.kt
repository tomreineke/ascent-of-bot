package com.cerebrallychallenged.hypogean.util.collections

inline fun <T: Any, K: Comparable<K>> Iterator<T>.mergeJoinBy(
    rhs: Iterator<T>,
    extract: (T) -> K,
    merge: (T?, T?) -> Unit
) {
    var leftElement: T? = null
    var rightElement: T? = null
    while (true) {
        if (leftElement == null) {
            if (this.hasNext()) {
                leftElement = this.next()
            } else {
                if (rightElement != null) {
                    merge(null, rightElement)
                }
                for (element in rhs) {
                    merge(null, element)
                }
                return
            }
        }
        if (rightElement == null) {
            if (rhs.hasNext()) {
                rightElement = rhs.next()
            } else {
                merge(leftElement, null)
                for (element in this) {
                    merge(element, null)
                }
                return
            }
        }
        val cmp = extract(leftElement).compareTo(extract(rightElement))
        when {
            cmp < 0 -> {
                merge(leftElement, null)
                leftElement = null
            }
            cmp > 0 -> {
                merge(null, rightElement)
                rightElement = null
            }
            else -> {
                merge(leftElement, rightElement)
                leftElement = null
                rightElement = null
            }
        }
    }
}

//inline fun <T: Any, K: Comparable<K>, R: Any> Sequence<T>.mergeJoinBy(
//    rhs: Sequence<T>,
//    crossinline extract: (T) -> K,
//    crossinline merge: (T?, T?) -> R?
//): Sequence<R> = sequence {
//    this@mergeJoinBy.iterator().mergeJoinBy(rhs.iterator(), extract) { left, right ->
//        merge(left, right)?.let { yield(it) }
//    }
//}
