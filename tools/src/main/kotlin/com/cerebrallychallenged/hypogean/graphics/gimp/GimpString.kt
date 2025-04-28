package com.cerebrallychallenged.hypogean.graphics.gimp

class GimpString(gimp: Gimp, constant: String? = null) : GimpValue(gimp, constant?.escapePython())

internal fun String.escapePython(): String = buildString {
    append('"')
    for (ch in this@escapePython) {
        when (ch) {
            '"' -> append("""\"""")
            '\\' -> append("""\\""")
            else -> append(ch)
        }
    }
    append('"')
}