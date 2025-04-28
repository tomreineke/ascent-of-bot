package com.cerebrallychallenged.jun.math.geo

interface VectorSpace<V : Vec<V, *, *, *>> {
    val dimension: Int

    val zero: V

    val one: V

    fun basis(index: Int): V

    /**
     * Returns the volume of a single point in this vector space.
     * Equals [.one] in discrete and [.zero] in continuous spaces, respectively.
     * @return the volume of a single point in this vector space.
     */
    val pointSize: V

    val emptyBounds: Bounds<V>
}