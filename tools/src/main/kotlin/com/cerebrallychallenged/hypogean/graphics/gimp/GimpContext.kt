package com.cerebrallychallenged.hypogean.graphics.gimp

import java.nio.file.Path

interface GimpContext {
    val gimp: Gimp

    fun append(line: String) {
        gimp.append(line)
    }

    var indentation: Int
        get() = gimp.indentation
        set(value) {
            gimp.indentation = value
        }

    val Int.g: GimpInt
        get() = GimpInt(gimp, this)

    val Double.g: GimpFloat
        get() = GimpFloat(gimp, this)

    val String.g: GimpString
        get() = GimpString(gimp, this)

    val Boolean.g: GimpBoolean
        get() = GimpBoolean(gimp, this)

    val Path.g: GimpString
        get() = GimpString(gimp, toAbsolutePath().toString().replace('\\', '/'))

    @Suppress("PropertyName")
    val GimpColor.Companion.White: GimpColor
        get() = GimpColor(gimp, """"White"""")

    @Suppress("PropertyName")
    val GimpColor.Companion.Black: GimpColor
        get() = GimpColor(gimp, """"Black"""")
}

fun GimpContext.message(text: GimpString) {
    append("pdb.gimp_message($text)")
}

fun GimpContext.message(text: String) {
    message(text.g)
}