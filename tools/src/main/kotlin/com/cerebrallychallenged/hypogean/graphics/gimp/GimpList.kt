package com.cerebrallychallenged.hypogean.graphics.gimp

class GimpList<T : GimpValue>(gimp: Gimp, private val factory: (Gimp) -> T) : GimpValue(gimp) {
    operator fun get(index: GimpInt): T = factory(gimp).also { append("$it = $this[$index]") }

    operator fun get(index: Int): T = get(index.g)
}