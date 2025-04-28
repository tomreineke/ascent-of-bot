package com.cerebrallychallenged.hypogean.graphics.gimp

enum class BevelingStyle(private val code: String) {
    OuterBevel("0"),
    InnerBevel("1"),
    Emboss("2"),
    PillowEmboss("3");

    override fun toString(): String = code
}

enum class EmbossingDirection(private val code: String) {
    Up("0"),
    Down("1");

    override fun toString(): String = code
}

enum class CombinationMode(private val code: String) {
    Normal("NORMAL_MODE"),
    Dissolve("DISSOLVE_MODE"),
    Multiply("MULTIPLY_MODE"),
    Screen("SCREEN_MODE"),
    Overlay("OVERLAY_MODE"),
    Difference("DIFFERENCE_MODE"),
    Addition("ADDITION_MODE"),
    Subtract("SUBTRACT_MODE"),
    DarkenOnly("DARKEN_ONLY_MODE"),
    LightenOnly("LIGHTEN_ONLY_MODE"),
    Hue("HUE_MODE"),
    Saturation("SATURATION_MODE"),
    Color("COLOR_MODE"),
    Value("VALUE_MODE"),
    Divide("DIVIDE_MODE"),
    Dodge("DODGE_MODE"),
    Burn("BURN_MODE"),
    HardLight("HARDLIGHT_MODE"),
    SoftLight("SOFTLIGHT_MODE"),
    GrainExtract("GRAIN_EXTRACT_MODE"),
    GrainMerge("GRAIN_MERGE_MODE"),
    ColorErase("COLOR_ERASE_MODE"),
    Erase("ERASE_MODE"),
    Replace("REPLACE_MODE"),
    AntiErase("ANTI_ERASE_MODE");

    override fun toString(): String = code
}

fun GimpContext.bevelEmboss(
    image: GimpImage,
    drawable: GimpDrawable,
    style: BevelingStyle,
    depth: GimpInt,
    direction: EmbossingDirection,
    size: GimpInt,
    soften: GimpInt,
    angle: GimpFloat,
    altitude: GimpFloat,
    glossContour: GimpInt,
    highlightColor: GimpColor,
    highlightMode: CombinationMode,
    highlightOpacity: GimpFloat,
    shadowColor: GimpColor,
    shadowMode: CombinationMode,
    shadowOpacity: GimpFloat,
    surfaceContour: GimpInt,
    useTexture: GimpBoolean,
    pattern: GimpString,
    scale: GimpFloat,
    texDepth: GimpFloat,
    invert: GimpBoolean,
    merge: GimpBoolean
) {
    append("pdb.python_layerfx_bevel_emboss($image, $drawable, $style, $depth, $direction, $size, $soften, $angle, $altitude, $glossContour, $highlightColor, $highlightMode, $highlightOpacity, $shadowColor, $shadowMode, $shadowOpacity, $surfaceContour, $useTexture, $pattern, $scale, $texDepth, $invert, $merge)")
}

fun GimpContext.bevelEmboss(
    image: GimpImage,
    drawable: GimpDrawable,
    style: BevelingStyle,
    depth: Int,
    direction: EmbossingDirection,
    size: Int,
    soften: Int,
    angle: Double,
    altitude: Double,
    glossContour: Int,
    highlightColor: GimpColor,
    highlightMode: CombinationMode,
    highlightOpacity: Double,
    shadowColor: GimpColor,
    shadowMode: CombinationMode,
    shadowOpacity: Double,
    surfaceContour: Int,
    useTexture: Boolean,
    pattern: String,
    scale: Double,
    texDepth: Double,
    invert: Boolean,
    merge: Boolean
) {
    bevelEmboss(
        image,
        drawable,
        style,
        depth.g,
        direction,
        size.g,
        soften.g,
        angle.g,
        altitude.g,
        glossContour.g,
        highlightColor,
        highlightMode,
        highlightOpacity.g,
        shadowColor,
        shadowMode,
        shadowOpacity.g,
        surfaceContour.g,
        useTexture.g,
        pattern.g,
        scale.g,
        texDepth.g,
        invert.g,
        merge.g
    )
}

fun GimpContext.neonLogoAlpha(
    image: GimpImage,
    drawable: GimpDrawable,
    value: GimpFloat,
    backgroundColor: GimpColor,
    glowColor: GimpColor,
    createShadow: GimpBoolean
) {
    append("pdb.script_fu_neon_logo_alpha($image, $drawable, $value, $backgroundColor, $glowColor, $createShadow)")
}

fun GimpContext.neonLogoAlpha(
    image: GimpImage,
    drawable: GimpDrawable,
    value: Double,
    backgroundColor: GimpColor,
    glowColor: GimpColor,
    createShadow: Boolean
) {
    neonLogoAlpha(image, drawable, value.g, backgroundColor, glowColor, createShadow.g)
}

fun GimpContext.softGlow(
    image: GimpImage,
    drawable: GimpDrawable,
    glowRadius: GimpFloat,
    brightness: GimpFloat,
    sharpness: GimpFloat
) {
    append("pdb.plug_in_softglow($image, $drawable, $glowRadius, $brightness, $sharpness)")
}

fun GimpContext.softGlow(
    image: GimpImage,
    drawable: GimpDrawable,
    glowRadius: Double,
    brightness: Double,
    sharpness: Double
) {
    softGlow(image, drawable, glowRadius.g, brightness.g, sharpness.g)
}
