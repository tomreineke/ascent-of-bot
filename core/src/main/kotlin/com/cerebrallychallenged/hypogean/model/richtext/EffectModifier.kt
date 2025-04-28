import com.cerebrallychallenged.hypogean.model.effect.EffectKindSet
import com.cerebrallychallenged.hypogean.model.effect.EffectModifier
import com.cerebrallychallenged.hypogean.model.richtext.RichText
import com.cerebrallychallenged.hypogean.model.richtext.RichText.Style.SmallTitle
import com.cerebrallychallenged.hypogean.model.richtext.richText

fun EffectModifier.toRichText(indentation: String = "", role: String): RichText = richText {
    title("${indentation}Provided $role Effect Modifiers:", SmallTitle)
    for (effectValueModifier in modifiers) {
        newLine()
        +"${indentation}• $effectValueModifier of "
        +when (val kinds = effectValueModifier.kinds) {
            is EffectKindSet.Empty -> "Nothing" // Should not happen
            is EffectKindSet.Singleton -> kinds.kind.toString()
            is EffectKindSet.Complex -> {
                val list = (kinds.subclasses.asSequence().map { "any $it" } + kinds.kinds.map { "$it" }).toList()
                when (list.size) {
                    0 -> "Nothing" // Should not happen
                    1 -> list[0]
                    2 -> "${list[0]} and ${list[1]}"
                    else -> {
                        "${list.subList(0, list.size - 1).joinToString(", ")} and ${list.last()}"
                    }
                }
            }
        }
    }
}
