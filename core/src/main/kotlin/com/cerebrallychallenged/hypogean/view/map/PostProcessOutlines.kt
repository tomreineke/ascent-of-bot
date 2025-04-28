package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean.M_Outlines
import com.cerebrallychallenged.jun.asset.AssetLibrary
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceDynamic
import com.cerebrallychallenged.jun.unreal.postprocess.APostProcessVolume
import com.cerebrallychallenged.jun.unreal.spawnActor

internal class PostProcessOutlines(
        private val material: UMaterialInstanceDynamic,
        private val factionRelationColors: Map<Faction.Relation?, FLinearColor>
) {
    companion object {
        val HintStencilValue = Faction.Relation.values().size + 1

        private val Faction.Relation.stencilValue: Int
            get() = ordinal + 1

        suspend operator fun invoke(
                assetLibrary: AssetLibrary,
                factionRelationColors: Map<Faction.Relation?, FLinearColor>,
                hintColor: FLinearColor
        ): PostProcessOutlines {
            val parentMaterial = assetLibrary.load(M_Outlines)
            val material = UMaterialInstanceDynamic.create(parentMaterial)
            for ((relation, color) in factionRelationColors) {
                if (relation != null) {
                    val stencilValue = relation.stencilValue
                    material.parameters["Outline$stencilValue"] = color
                    material.parameters["Occluded$stencilValue"] = color.withA(0.5f)
                }
            }
            material.parameters["Outline$HintStencilValue"] = hintColor
            material.parameters["Occluded$HintStencilValue"] = hintColor.withA(0.5f)
            return PostProcessOutlines(material, factionRelationColors)
        }
    }

    private val postProcessVolume = spawnActor<APostProcessVolume>().apply {
        unbound = true
        addOrUpdateBlendable(material, 1.0f)
    }

    fun stencilValueFor(relation: Faction.Relation, hovered: Boolean): Int {
        return if (hovered) {
            val outlineColor = factionRelationColors[relation] ?: error("Missing color for $relation")
            val hoveredColorLo = outlineColor.withA(0.5f)
            val hoveredColorHi = hoveredColorLo.interpolate(0.5f, FLinearColor.White)
            material.parameters["DynamicOutLineLo"] = outlineColor
            material.parameters["DynamicOutLineHi"] = outlineColor
            material.parameters["DynamicOccludedLo"] = hoveredColorLo
            material.parameters["DynamicOccludedHi"] = hoveredColorHi
            material.parameters["DynamicInteriorLo"] = hoveredColorLo
            material.parameters["DynamicInteriorHi"] = hoveredColorHi
            15
        } else {
            relation.stencilValue
        }
    }
}
