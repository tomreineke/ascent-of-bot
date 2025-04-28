package com.cerebrallychallenged.hypogean.view.tooltip

import com.cerebrallychallenged.hypogean.gui.GuiConfig.DefaultTextStyle
import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.withStyle
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.effect.areaEffect
import com.cerebrallychallenged.hypogean.model.effect.causedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.providedPassiveEffectModifier
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.richtext.RichText
import com.cerebrallychallenged.hypogean.model.richtext.RichText.Style.MediumTitle
import com.cerebrallychallenged.hypogean.model.richtext.RichText.Style.SmallTitle
import com.cerebrallychallenged.hypogean.model.richtext.RichText.Style.TinyTitle
import com.cerebrallychallenged.hypogean.model.richtext.richText
import com.cerebrallychallenged.hypogean.model.richtext.toRichText
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.Energy
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.flavorText
import com.cerebrallychallenged.hypogean.vanilla.attributes.passiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.setupTime
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.hypogean.view.util.process
import toRichText

var Entity.explanatoryTooltip: String? by attribute(null)

private fun createTooltip(viewModel: ViewModel, f: RichText.Builder.() -> Unit): Tooltip = Tooltip {
    withStyle(DefaultTextStyle) {
        val text = richText(f)
        with(viewModel) {
            text.process(viewModel)
        }
    }
}

fun Entity.createTooltip(viewModel: ViewModel, includeFlavor: Boolean): Tooltip = createTooltip(viewModel) {
    title(name, MediumTitle)
    explanatoryTooltip?.let {
        +it
    }
    val flavorText = flavorText
    if (includeFlavor && flavorText != null) {
        newLine()
        quoteItalic {
            +flavorText
        }
    }
    when (this@Entity) {
        is StatusEffect -> {
            newLine()
            val duration = duration
            +"Remaining Time: "
            if (duration != null) {
                val remainingTime = creationTime + duration - world.currentIniTime
                +"$remainingTime⌛ rounds"
            } else {
                +"Indefinite"
            }
        }
        is Item -> {
            val activeEnergyConsumption = activeEnergyConsumption
            if (!activeEnergyConsumption.isZero) {
                newLine()
                +"Energy consumption: "
                when (activeEnergyConsumption) {
                    is ActiveEnergyConsumption.PerAction -> {
                        +Energy.formatValue(activeEnergyConsumption.value)
                        +" / Use"
                    }
                    is ActiveEnergyConsumption.PerDistance -> {
                        +Energy.formatValue(activeEnergyConsumption.perMeter)
                        +" / Cell"
                    }
                }
            }
            val passiveEnergyConsumption = passiveEnergyConsumption
            if (passiveEnergyConsumption > 0) {
                newLine()
                +"Energy consumption: ${Energy.formatValue(passiveEnergyConsumption)} / Round"
            }
            val setupTime = setupTime
            if (setupTime != 0) {
                newLine()
                +"Setup Time: $setupTime⌛"
            }
            val cooldown = cooldown
            if (cooldown != 0) {
                newLine()
                +"Cooldown: $cooldown⌛"
            }
        }
    }
    val directEffect = directEffect
    if (!directEffect.isEmpty() || this@Entity is Weapon) {
        +directEffect.toRichText()
    }
    val areaEffect = areaEffect
    if (areaEffect?.isEmpty() == false) {
        +areaEffect.toRichText()
    }
    val causedStatusEffects = causedStatusEffects
    if (causedStatusEffects.effects.isNotEmpty()) {
        title("Caused Status Effects", SmallTitle)
        for (effect in causedStatusEffects.effects) {
            newLine()
            val effectContainer = effect.effectContainer
            effectContainer.icon?.let {
                +" "
                image(it)
            }
            effectContainer.name?.let {
                +" $it"
            }
            +" for "
            +effect.duration.toRichText()
            effectContainer.directEffect?.let {
                +it.toRichText("   ", TinyTitle, spaceBeforeTitle = false)
            }
            effectContainer.areaEffect?.let {
                +it.toRichText("   ", TinyTitle, spaceBeforeTitle = false)
            }
        }
    }
    val providedEffectReduction = providedPassiveEffectModifier
    if (!providedEffectReduction.isEmpty()) {
        newLine()
        +providedEffectReduction.toRichText(role = "Passive")
    }
}
