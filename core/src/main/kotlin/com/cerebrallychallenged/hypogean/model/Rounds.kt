package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.richtext.RichText
import com.cerebrallychallenged.hypogean.model.richtext.richText

class Rounds(val rounds: Int?) {
    companion object {
        val Infinite = Rounds(null)
    }

    override fun toString(): String = if (rounds == null) "infinite rounds" else "$rounds rounds"

    fun toRichText(): RichText = richText {
        +if (rounds == null) "∞⌛" else "$rounds⌛"
    }
}

val Int.rounds: Rounds
    get() = Rounds(this)
