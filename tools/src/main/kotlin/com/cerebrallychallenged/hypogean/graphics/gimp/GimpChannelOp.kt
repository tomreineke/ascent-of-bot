package com.cerebrallychallenged.hypogean.graphics.gimp

enum class GimpChannelOp(private val code: String) {
    Add("CHANNEL_OP_ADD"),
    Subtract("CHANNEL_OP_SUBTRACT"),
    Replace("CHANNEL_OP_REPLACE"),
    Intersect("CHANNEL_OP_INTERSECT");

    override fun toString(): String = code
}