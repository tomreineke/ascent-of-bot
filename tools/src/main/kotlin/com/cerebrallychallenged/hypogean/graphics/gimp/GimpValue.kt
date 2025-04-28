package com.cerebrallychallenged.hypogean.graphics.gimp

import java.util.*

open class GimpValue(gimp: Gimp, private val constantString: String? = null) : GimpContext by gimp {
    private val varIndex: Int = if (constantString == null) gimp.obtainVarIndex() else -1

    override fun toString(): String =
        constantString ?: "v_${this::class.simpleName!!.removePrefix("Gimp").lowercase(Locale.getDefault())}_${varIndex}"
}
