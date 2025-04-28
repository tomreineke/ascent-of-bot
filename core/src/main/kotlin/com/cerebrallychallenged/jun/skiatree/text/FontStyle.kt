package com.cerebrallychallenged.jun.skiatree.text

@JvmInline
value class FontStyle private constructor(internal val value: Int) {
    companion object {
        operator fun invoke(weight: Weight, width: Width, slant: Slant): FontStyle = FontStyle(
            weight.value + (width.value shl 16) + (slant.ordinal shl 24)
        )

        val Normal: FontStyle = FontStyle(Weight.Normal, Width.Normal, Slant.Upright)
        val Bold: FontStyle = FontStyle(Weight.Bold, Width.Normal, Slant.Upright)
        val Italic: FontStyle = FontStyle(Weight.Normal, Width.Normal, Slant.Italic)
        val BoldItalic: FontStyle = FontStyle(Weight.Bold, Width.Normal, Slant.Italic)
    }

    enum class Weight(internal val value: Int) {
        Invisible(0),
        Thin(100),
        ExtraLight(200),
        Light(300),
        Normal(400),
        Medium(500),
        SemiBold(600),
        Bold(700),
        ExtraBold(800),
        Black(900),
        ExtraBlack(1000)
    }

    enum class Width(internal val value: Int) {
        UltraCondensed(1),
        ExtraCondensed(2),
        Condensed(3),
        SemiCondensed(4),
        Normal(5),
        SemiExpanded(6),
        Expanded(7),
        ExtraExpanded(8),
        UltraExpanded(9)
    }

    enum class Slant {
        Upright,
        Italic,
        Oblique
    }
}
