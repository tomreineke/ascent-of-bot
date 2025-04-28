package com.cerebrallychallenged.hypogean.graphics.gimp

import java.util.*

class GimpBoolean(gimp: Gimp, constant: Boolean? = null) : GimpValue(gimp,
    constant?.toString()
        ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) {
    fun then(block: GimpContext.() -> Unit) {
        append("if $this:")
        ++indentation
        block()
        --indentation
    }
}
