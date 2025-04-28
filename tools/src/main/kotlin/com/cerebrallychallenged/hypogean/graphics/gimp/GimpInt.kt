package com.cerebrallychallenged.hypogean.graphics.gimp

class GimpInt(gimp: Gimp, constant: Int? = null) : GimpValue(gimp, constant?.toString()) {
    operator fun plus(rhs: GimpInt): GimpInt = GimpInt(gimp).also { append("$it = $this + $rhs") }
    operator fun minus(rhs: GimpInt): GimpInt = GimpInt(gimp).also { append("$it = $this - $rhs") }
    operator fun times(rhs: GimpInt): GimpInt = GimpInt(gimp).also { append("$it = $this * $rhs") }
    operator fun div(rhs: GimpInt): GimpInt = GimpInt(gimp).also { append("$it = $this / $rhs") }

    fun isEqual(rhs: GimpInt): GimpBoolean = GimpBoolean(gimp).also { append("$it = $this == $rhs") }
    fun isNotEqual(rhs: GimpInt): GimpBoolean = GimpBoolean(gimp).also { append("$it = $this != $rhs") }
}

operator fun GimpInt.plus(rhs: Int): GimpInt = this + rhs.g
operator fun Int.plus(rhs: GimpInt): GimpInt = with(rhs) { this@plus.g } + rhs

operator fun GimpInt.minus(rhs: Int): GimpInt = this - rhs.g
operator fun Int.minus(rhs: GimpInt): GimpInt = with(rhs) { this@minus.g } - rhs

operator fun GimpInt.times(rhs: Int): GimpInt = this * rhs.g
operator fun Int.times(rhs: GimpInt): GimpInt = with(rhs) { this@times.g } * rhs

operator fun GimpInt.div(rhs: Int): GimpInt = this / rhs.g
operator fun Int.div(rhs: GimpInt): GimpInt = with(rhs) { this@div.g } / rhs

fun GimpInt.isEqual(rhs: Int): GimpBoolean = this.isEqual(rhs.g)
fun Int.isEqual(rhs: GimpInt): GimpBoolean = with(rhs) { this@isEqual.g.isEqual(rhs) }

fun GimpInt.isNotEqual(rhs: Int): GimpBoolean = this.isNotEqual(rhs.g)
fun Int.isNotEqual(rhs: GimpInt): GimpBoolean = with(rhs) { this@isNotEqual.g.isNotEqual(rhs) }
