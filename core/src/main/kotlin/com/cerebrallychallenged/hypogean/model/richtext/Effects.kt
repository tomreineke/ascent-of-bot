package com.cerebrallychallenged.hypogean.model.richtext

import com.cerebrallychallenged.hypogean.linguistics.signedString
import com.cerebrallychallenged.hypogean.model.effect.AreaEffect
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.EffectValue
import com.cerebrallychallenged.hypogean.model.richtext.RichText.Style.SmallTitle
import com.cerebrallychallenged.hypogean.vanilla.cascade.LinearFalloff

fun Effect.toRichText(
    indentation: String = "",
    titleStyle: RichText.Style = SmallTitle,
    spaceBeforeTitle: Boolean = true
): RichText = richText {
    val effects = effects
    title("${indentation}Direct Effects", titleStyle, spaceBeforeTitle)
    +effectsToRichText(effects, indentation)
}

fun AreaEffect.toRichText(
    indentation: String = "",
    titleStyle: RichText.Style = SmallTitle,
    spaceBeforeTitle: Boolean = true
): RichText = richText {
    title(
        "${indentation}Area Effect within ${radius}m${if (falloff == LinearFalloff) " with linear falloff" else ""}",
        titleStyle,
        spaceBeforeTitle
    )
    +effectsToRichText(effect.effects, indentation)
}


private fun effectsToRichText(effects: List<EffectValue>, indentation: String): RichText = richText {
    if (effects.isEmpty()) {
        +" None"
    } else {
        for (effectValue in effects) {
            newLine()
            +"$indentation• "
            +effectValue.toRichText()
            +" of ${effectValue.kind}"
        }
    }
}

fun EffectValue.toRichText(): RichText = richText {
    when (this@toRichText) {
        is EffectValue.Absolute -> {
            val first = range.first
            val last = range.last
            +if (first == last) {
                "$first"
            } else {
                "$first to $last"
            }
        }
        is EffectValue.Relative -> {
            val qualifier = when (this@toRichText) {
                is EffectValue.RelativeOfCurrent -> "Current"
                is EffectValue.RelativeOfMax -> "Max"
            }
            +"${percent.signedString()}% · $qualifier "
            withTooltip(attribute.symbol.toRichText(), attribute.name.toRichText())
        }
    }
}
