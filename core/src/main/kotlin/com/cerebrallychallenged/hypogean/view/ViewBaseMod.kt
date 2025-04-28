package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.hypogean.gui.GuiAssetLoaders
import com.cerebrallychallenged.hypogean.gui.StandardButton
import com.cerebrallychallenged.hypogean.modding.CoreMod
import com.cerebrallychallenged.hypogean.modding.Features
import com.cerebrallychallenged.hypogean.modding.Mod
import com.cerebrallychallenged.hypogean.modding.ModContext
import com.cerebrallychallenged.hypogean.modding.ModDependencies
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.action.Actions
import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodecs
import com.cerebrallychallenged.hypogean.model.attribute.Attributes
import com.cerebrallychallenged.hypogean.settings.SettingsModuleKeys
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonDefinitionsRegistry
import com.cerebrallychallenged.hypogean.view.conf.ViewsDefinitions
import com.cerebrallychallenged.hypogean.view.experimental.ExperimentalCommand
import com.cerebrallychallenged.hypogean.view.input.InputCommands
import com.cerebrallychallenged.hypogean.view.input.KeyBindings
import com.cerebrallychallenged.hypogean.view.map.ActorAssets
import com.cerebrallychallenged.hypogean.view.map.AssetParameterBindingsAttributeCodec
import com.cerebrallychallenged.hypogean.view.map.Asset_GroundCircleIndicator
import com.cerebrallychallenged.hypogean.view.map.PanDownCommand
import com.cerebrallychallenged.hypogean.view.map.PanDownCommandSecondary
import com.cerebrallychallenged.hypogean.view.map.PanLeftCommand
import com.cerebrallychallenged.hypogean.view.map.PanLeftCommandSecondary
import com.cerebrallychallenged.hypogean.view.map.PanRightCommand
import com.cerebrallychallenged.hypogean.view.map.PanRightCommandSecondary
import com.cerebrallychallenged.hypogean.view.map.PanUpCommand
import com.cerebrallychallenged.hypogean.view.map.PanUpCommandSecondary
import com.cerebrallychallenged.hypogean.view.map.RotateLeftCommand
import com.cerebrallychallenged.hypogean.view.map.RotateRightCommand
import com.cerebrallychallenged.hypogean.view.map.ZoomInCommand
import com.cerebrallychallenged.hypogean.view.map.ZoomOutCommand
import com.cerebrallychallenged.hypogean.view.map.asset
import com.cerebrallychallenged.hypogean.view.map.visualizers.Asset_BoxFrame
import com.cerebrallychallenged.hypogean.view.map.visualizers.Asset_Path
import com.cerebrallychallenged.hypogean.view.map.visualizers.Asset_WaypointCone
import com.cerebrallychallenged.hypogean.view.map.voxel.BlockAssets
import com.cerebrallychallenged.hypogean.view.map.voxel.blockAsset
import com.cerebrallychallenged.hypogean.view.menu.ToggleMenuView
import com.cerebrallychallenged.hypogean.view.modular.actions.CharacterViewAction
import com.cerebrallychallenged.hypogean.view.modular.actions.InfoViewAction
import com.cerebrallychallenged.hypogean.view.modular.views.ToggleCharacterView
import com.cerebrallychallenged.hypogean.view.modular.views.ToggleInventoryView
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip

@ModDependencies(CoreMod::class)
object ViewBaseMod : Mod {
    override fun ModContext.setupFeatureDiscovery() {
        configure<Features> {
            register(ActionButtonDefinitionsRegistry())
            register(ActorAssets())
            register(BlockAssets())
            register(CompositeAssets())
            register(CompositeParameters())
            register(GuiAssetLoaders())
            register(InputCommands())
            register(ViewsDefinitions())
        }
    }

    override fun ModContext.installCodecsAndStreaming() {
        configure<AttributeCodecs> {
            register<AssetParameterBindingsAttributeCodec>()
        }
    }

    override fun ModContext.install() {
        configure<Actions> {
            register(CharacterViewAction)
            register(InfoViewAction)
        }
        configure<Attributes> {
            register(Actor::asset)
            register(Entity::explanatoryTooltip)
//            register(Item::energyRegulationPossible)
            register(Item::blockAsset)
            register(LocatedEntity::materialAsset)
            register(World::globalDirectionalBrightness)
        }
        configure<CompositeAssets> {
            register(Asset_GroundCircleIndicator)
            register(Asset_BoxFrame)
            register(Asset_CellFloor)
            register(Asset_Path)
            register(Asset_WaypointCone)
        }
        configure<CompositeParameters> {
            register(Asset_Path.PathParameter)
            register(FactionMaterial)
            register(Material)
            register(MaterialAsset)
            register(Time)
        }
        configure<GuiAssetLoaders> {
            register(StandardButton.AssetLoader)
        }
        configure<InputCommands> {
            register(ExperimentalCommand)
            register(PanDownCommand)
            register(PanLeftCommand)
            register(PanRightCommand)
            register(PanUpCommand)
            register(PanDownCommandSecondary)
            register(PanLeftCommandSecondary)
            register(PanRightCommandSecondary)
            register(PanUpCommandSecondary)
            register(RotateLeftCommand)
            register(RotateRightCommand)
            register(ShiftDebugMode)
            register(ShiftPartialMode)
            register(ToggleCharacterView)
            register(ToggleInventoryView)
            register(ToggleMenuView)
            register(ZoomInCommand)
            register(ZoomOutCommand)
        }
        configure<SettingsModuleKeys> {
            register(KeyBindings.Key)
        }
    }
}
