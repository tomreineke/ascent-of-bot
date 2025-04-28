package com.cerebrallychallenged.hypogean.vanilla

import com.cerebrallychallenged.hypogean.gui.hBox
import com.cerebrallychallenged.hypogean.gui.node
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.vBox
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.View
import com.cerebrallychallenged.hypogean.model.action.singleAction
import com.cerebrallychallenged.hypogean.model.action.singleCategory
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttributes
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.actions.Hacking
import com.cerebrallychallenged.hypogean.vanilla.actions.Move
import com.cerebrallychallenged.hypogean.vanilla.actions.MoveAction
import com.cerebrallychallenged.hypogean.vanilla.actions.OpenInventoryAction
import com.cerebrallychallenged.hypogean.vanilla.actions.PickUp
import com.cerebrallychallenged.hypogean.vanilla.actions.Talk
import com.cerebrallychallenged.hypogean.vanilla.actions.Use
import com.cerebrallychallenged.hypogean.vanilla.actions.Utility
import com.cerebrallychallenged.hypogean.vanilla.refs.DystopianDrones
import com.cerebrallychallenged.hypogean.view.actionbar.ActionBarView
import com.cerebrallychallenged.hypogean.view.conf.ViewsDefinition
import com.cerebrallychallenged.hypogean.view.dialog.DialogView
import com.cerebrallychallenged.hypogean.view.effectinfo.EffectInfoView
import com.cerebrallychallenged.hypogean.view.enemyturn.EnemyTurnView
import com.cerebrallychallenged.hypogean.view.gameover.GameOverView
import com.cerebrallychallenged.hypogean.view.hint.HintView
import com.cerebrallychallenged.hypogean.view.inibar.IniBarView
import com.cerebrallychallenged.hypogean.view.loading.LoadingView
import com.cerebrallychallenged.hypogean.view.map.MapView
import com.cerebrallychallenged.hypogean.view.map.visualizers.GenericVisualizer
import com.cerebrallychallenged.hypogean.view.map.visualizers.MoveVisualizer
import com.cerebrallychallenged.hypogean.view.menu.MenuView
import com.cerebrallychallenged.hypogean.view.modular.actions.CharacterViewAction
import com.cerebrallychallenged.hypogean.view.modular.actions.InfoViewAction
import com.cerebrallychallenged.hypogean.view.modular.modules.CollectAllInventoryModule
import com.cerebrallychallenged.hypogean.view.modular.modules.DescriptionModule
import com.cerebrallychallenged.hypogean.view.modular.modules.FlavorTextModule
import com.cerebrallychallenged.hypogean.view.modular.modules.GageModule
import com.cerebrallychallenged.hypogean.view.modular.modules.InventoryModule
import com.cerebrallychallenged.hypogean.view.modular.modules.InventoryModule.Style.DefaultLargeBoxSize
import com.cerebrallychallenged.hypogean.view.modular.modules.InventoryModule.Style.DefaultSmallBoxSize
import com.cerebrallychallenged.hypogean.view.modular.modules.InventoryModule.Style.boxGap
import com.cerebrallychallenged.hypogean.view.modular.modules.NameModule
import com.cerebrallychallenged.hypogean.view.modular.modules.PortraitModule
import com.cerebrallychallenged.hypogean.view.modular.modules.StatusEffectsModule
import com.cerebrallychallenged.hypogean.view.modular.views.CharacterView
import com.cerebrallychallenged.hypogean.view.modular.views.ContainerInventoryView
import com.cerebrallychallenged.hypogean.view.modular.views.InfoView
import com.cerebrallychallenged.hypogean.view.mouse.MouseView
import com.cerebrallychallenged.hypogean.view.radialmenu.RadialMenuView
import com.cerebrallychallenged.hypogean.view.report.ReportView
import com.cerebrallychallenged.hypogean.view.transititem.TransitItemView
import com.cerebrallychallenged.hypogean.view.util.layoutGrid
import com.cerebrallychallenged.hypogean.view.util.layoutInventorySlots
import com.cerebrallychallenged.hypogean.view.util.mouse.Info
import com.cerebrallychallenged.hypogean.view.util.mouse.Inventory
import com.cerebrallychallenged.hypogean.view.util.mouse.MouseCursor
import com.cerebrallychallenged.hypogean.view.util.mouse.Move
import com.cerebrallychallenged.hypogean.view.util.mouse.PickUp
import com.cerebrallychallenged.hypogean.view.util.mouse.Shoot
import com.cerebrallychallenged.hypogean.view.util.mouse.Talk
import com.cerebrallychallenged.hypogean.view.util.mouse.Use
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.util.getResource

object DefaultViews : ViewsDefinition({
    view(MapView::Factory) {
        actionVisualizers[{ singleAction == MoveAction }] = MoveVisualizer.Factory
        actionVisualizers[{ singleCategory == ActionCategory.Attack && singleAction != null }] = GenericVisualizer.Factory(true)
        actionVisualizers[{ singleCategory != ActionCategory.View }] = GenericVisualizer.Factory(false)
    }
    view(MouseView::Factory) {
        cursors[{ singleCategory == ActionCategory.Move }] = MouseCursor.Move
        cursors[{ singleCategory == ActionCategory.PickUp }] = MouseCursor.PickUp
        cursors[{ singleAction == InfoViewAction }] = MouseCursor.Info
        cursors[{ singleAction == CharacterViewAction }] = MouseCursor.Info
        cursors[{ singleAction == OpenInventoryAction }] = MouseCursor.Inventory
        cursors[{ singleCategory == ActionCategory.Attack && singleAction != null }] = MouseCursor.Shoot
        val useCategories = listOf(
            ActionCategory.Hacking,
            ActionCategory.Use,
            ActionCategory.Utility
        )
        cursors[{ singleCategory in useCategories }] = MouseCursor.Use
        cursors[{ singleCategory == ActionCategory.Talk }] = MouseCursor.Talk
    }

    view(ActionBarView::Factory) {
        defineController(ActionCategory.Move, mergeActions = true, mergeTools = true, mergeModes = true)
        defineController(ActionCategory.PickUp, mergeActions = true, mergeTools = true, mergeModes = true)
        defineController(ActionCategory.Use, mergeActions = true, mergeTools = true, mergeModes = true)
        defineController(ActionCategory.Talk, mergeActions = true, mergeTools = true, mergeModes = true)
        defineController(ActionCategory.Hacking, mergeActions = true, mergeTools = true, mergeModes = true)
        defineController(ActionCategory.Attack, mergeActions = true, mergeTools = false, mergeModes = true)
        defineController(ActionCategory.Utility, mergeActions = true, mergeTools = true, mergeModes = true)
        defineController(ActionCategory.Skip, mergeActions = true, mergeTools = true, mergeModes = true)
    }
    view(ReportView::Factory)
    view(MenuView::Factory)
    view(EnemyTurnView::Factory)
    view(GameOverView::Factory)
    view(DialogView::Factory)
    view(IniBarView::Factory)
    val gap = 20
    view(ContainerInventoryView::Factory) {
        structure = { (leftEntity, rightEntity) ->
            hBox {
                hgap = gap
                vBox {
                    vgap = gap
                    hBox {
                        hgap = gap
                        horizontalAlign = Align.Center
                        include(PortraitModule(leftEntity))
                        include(NameModule(leftEntity))
                    }
                    leftEntity.factionEntity?.let {
                        include(InventoryModule(it, "inventory", DefaultSmallBoxSize))
                    }
                    include(InventoryModule(leftEntity, "weaponSlot", DefaultLargeBoxSize))
                }
                vBox {
                    vgap = gap
                    hBox {
                        hgap = gap
                        horizontalAlign = Align.Center
                        include(PortraitModule(rightEntity))
                        include(NameModule(rightEntity))
                    }
                    include(InventoryModule(rightEntity, DefaultSmallBoxSize))
                    include(CollectAllInventoryModule(rightEntity))
                }
            }
        }
    }
    view(InfoView::Factory) {
        structure = { entity ->
            vgap = gap.scaled
            include(NameModule(entity))
            include(DescriptionModule(entity))
            include(FlavorTextModule(entity))
            hBox {
                hgap = gap.scaled
                include(PortraitModule(entity))
                vBox {
                    for (attribute in entity.rulebook.feature<SimpleIntAttributes>()) {
                        attribute.asAttributeFor(entity)?.let {
                            include(GageModule(entity, it))
                        }
                    }
                }
            }
            include(StatusEffectsModule(entity))
        }
    }
    view(LoadingView::Factory) {
        getResource("tipsOfDay.txt").readText().lineSequence().filterTo(tipsOfDay) { it.isNotEmpty() }
        sounds.add(DystopianDrones.Evolving_Drones_1)
    }
    view(CharacterView::Factory) {
        structure = { entity ->
            vgap = gap.scaled
            include(NameModule(entity))
            include(DescriptionModule(entity))
            include(FlavorTextModule(entity))
            hBox {
                hgap = gap.scaled
                include(PortraitModule(entity))
                vBox {
                    for (attribute in entity.rulebook.feature<SimpleIntAttributes>()) {
                        attribute.asAttributeFor(entity)?.let {
                            include(GageModule(entity, it))
                        }
                    }
                }
            }
            include(StatusEffectsModule(entity))

            hBox(isWrapping = true) {
                hgap = gap.scaled
                node {
                    layoutGrid(
                        3,
                        3,
                        550,
                        DefaultLargeBoxSize + gap,
                        layoutInventorySlots(entity)
                    ) { position, slotName ->
                        include(InventoryModule(entity, slotName, DefaultLargeBoxSize, position))
                    }
                }
                entity.factionEntity?.let {
                    vBox {
                        vgap = gap.scaled
                        val inv = InventoryModule(it, "inventory", DefaultSmallBoxSize)
                        include(inv)
                        val width = inv?.container?.let {
                            val providedBoxes = it.providedBoxes.x
                            inv.boxSize * providedBoxes + boxGap * (providedBoxes - 1)
                        }
                        // put the drop area centrally beneath the inventory
                        val posX = width?.div(2)?.minus(DefaultLargeBoxSize / 2) ?: 0
                        include(InventoryModule(it, "dropSlot", DefaultLargeBoxSize, vec(posX, 0)))
                    }
                }
            }
        }
    }
//
    view(TransitItemView::Factory)
    view(HintView::Factory)
    view(EffectInfoView::Factory)
    view(RadialMenuView::Factory)
//    view(ExperimentalView::Factory)
})
