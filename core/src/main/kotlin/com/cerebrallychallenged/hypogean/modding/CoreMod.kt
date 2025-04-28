package com.cerebrallychallenged.hypogean.modding

import com.cerebrallychallenged.hypogean.linguistics.pronoun
import com.cerebrallychallenged.hypogean.linguistics.verb
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.EntityTypeAttributeCodec
import com.cerebrallychallenged.hypogean.model.EntityTypes
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.Factions
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.Periodics
import com.cerebrallychallenged.hypogean.model.Slot
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.Transient
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldFactories
import com.cerebrallychallenged.hypogean.model.action.ActionCategories
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionHistoryEntryAttributeCodec
import com.cerebrallychallenged.hypogean.model.action.ActionModes
import com.cerebrallychallenged.hypogean.model.action.Actions
import com.cerebrallychallenged.hypogean.model.action.DefaultMode
import com.cerebrallychallenged.hypogean.model.action.NullAction
import com.cerebrallychallenged.hypogean.model.action.SkipAction
import com.cerebrallychallenged.hypogean.model.action.ViewActionCategory
import com.cerebrallychallenged.hypogean.model.action.actionHistory
import com.cerebrallychallenged.hypogean.model.attribute.AngleAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodecs
import com.cerebrallychallenged.hypogean.model.attribute.Attributes
import com.cerebrallychallenged.hypogean.model.attribute.BooleanAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.ByteArrayAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.DoubleAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.EntityAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.EntityReferringAttributeHandlers
import com.cerebrallychallenged.hypogean.model.attribute.EnumAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.FeatureCodecFactory
import com.cerebrallychallenged.hypogean.model.attribute.FloatAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.ImageResourceAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.IntAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.ListAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.ListReferringAttributeHandler
import com.cerebrallychallenged.hypogean.model.attribute.MapAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.NullableAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.OptionalReferringAttributeHandler
import com.cerebrallychallenged.hypogean.model.attribute.PairAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.QuaternionAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.SetAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttributes
import com.cerebrallychallenged.hypogean.model.attribute.SingleEntityReferringAttributeHandler
import com.cerebrallychallenged.hypogean.model.attribute.StringAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.Transform3fAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.UnrealRefAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.Vec2fAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.Vec2iAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.Vec3fAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.Vec3iAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.Vec4fAttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.Vec4iAttributeCodec
import com.cerebrallychallenged.hypogean.model.base.DropSlot
import com.cerebrallychallenged.hypogean.model.base.DummyEntity
import com.cerebrallychallenged.hypogean.model.base.EquipmentAcceptor
import com.cerebrallychallenged.hypogean.model.base.EquipmentSlot
import com.cerebrallychallenged.hypogean.model.base.HeadedProp
import com.cerebrallychallenged.hypogean.model.base.InventorySlot
import com.cerebrallychallenged.hypogean.model.base.ParticleSystem
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.base.PropPlacementAttributeCodec
import com.cerebrallychallenged.hypogean.model.base.PropSlot
import com.cerebrallychallenged.hypogean.model.base.SpectatorFaction
import com.cerebrallychallenged.hypogean.model.base.ToolAcceptor
import com.cerebrallychallenged.hypogean.model.base.ToolSlot
import com.cerebrallychallenged.hypogean.model.base.heading
import com.cerebrallychallenged.hypogean.model.base.placement
import com.cerebrallychallenged.hypogean.model.base.presentPropsList
import com.cerebrallychallenged.hypogean.model.base.propSize
import com.cerebrallychallenged.hypogean.model.cellFilling
import com.cerebrallychallenged.hypogean.model.containment.AcceptAll
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptors
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.dialog.DialogRoles
import com.cerebrallychallenged.hypogean.model.dialog.Dialogs
import com.cerebrallychallenged.hypogean.model.disguised
import com.cerebrallychallenged.hypogean.model.effect.EffectConsequences
import com.cerebrallychallenged.hypogean.model.effect.areaEffect
import com.cerebrallychallenged.hypogean.model.effect.causedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.destructionEffect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.passiveEffectModifier
import com.cerebrallychallenged.hypogean.model.effect.providedPassiveEffectModifier
import com.cerebrallychallenged.hypogean.model.elevation
import com.cerebrallychallenged.hypogean.model.everythingIsRevealed
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.periodics
import com.cerebrallychallenged.hypogean.model.pickupAble
import com.cerebrallychallenged.hypogean.model.size
import com.cerebrallychallenged.hypogean.model.transformWhenDropped
import com.cerebrallychallenged.hypogean.model.worldFactory
import com.cerebrallychallenged.hypogean.model.zShift
import com.cerebrallychallenged.hypogean.npc.Behaviors
import com.cerebrallychallenged.hypogean.npc.MoveCirclesBehavior
import com.cerebrallychallenged.hypogean.npc.SkipBehavior
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.npc.behaviorBytes
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractors
import com.cerebrallychallenged.hypogean.rays.ZeroExtractor
import com.cerebrallychallenged.hypogean.settings.SettingsModuleKeys
import com.cerebrallychallenged.hypogean.vanilla.attributes.numberWeaponSlots

object CoreMod : Mod {
    override fun ModContext.setupFeatureDiscovery() {
        configure<Features> {
            val codecs = AttributeCodecs(this@setupFeatureDiscovery)
            register(codecs)
            val entityReferringAttributeHandlers = EntityReferringAttributeHandlers()
            register(entityReferringAttributeHandlers)
            register(Attributes(codecs, entityReferringAttributeHandlers))
            register(EntityTypes())
            register(EffectConsequences())
            register(Factions())
            register(Periodics())
            register(WorldFactories())
            register(Actions())
            register(ActionCategories())
            register(ActionModes())
            register(ItemAcceptors())
            register(Dialogs())
            register(DialogRoles())
            register(Behaviors())
            register(BlockerValueExtractors())
            register(SettingsModuleKeys())
            register(SimpleIntAttributes())
        }
    }

    override fun ModContext.installCodecsAndStreaming() {
        configure<AttributeCodecs> {
            registerFactory(FeatureCodecFactory(this@installCodecsAndStreaming))

            register<ActionHistoryEntryAttributeCodec>()
            register<AngleAttributeCodec>()
            register<BooleanAttributeCodec>()
            register<ByteArrayAttributeCodec>()
            register<DoubleAttributeCodec>()
            register<EntityAttributeCodec<*>>()
            register<EntityTypeAttributeCodec<*>>()
            register<EnumAttributeCodec<*>>()
            register<FloatAttributeCodec>()
            register<ImageResourceAttributeCodec>()
            register<IntAttributeCodec>()
            register<ListAttributeCodec<*>>()
            register<MapAttributeCodec<*, *>>()
            register<NullableAttributeCodec<*>>()
            register<PairAttributeCodec<*, *>>()
            register<PropPlacementAttributeCodec>()
            register<QuaternionAttributeCodec>()
            register<SetAttributeCodec<*>>()
            register<StringAttributeCodec>()
            register<Transform3fAttributeCodec>()
            register<UnrealRefAttributeCodec<*>>()
            register<Vec2fAttributeCodec>()
            register<Vec2iAttributeCodec>()
            register<Vec3fAttributeCodec>()
            register<Vec3iAttributeCodec>()
            register<Vec4fAttributeCodec>()
            register<Vec4iAttributeCodec>()
        }
    }

    override fun ModContext.install() {
        configure<ActionCategories> {
            register(ActionCategory.Null)
            register(ActionCategory.Skip)
            register(ViewActionCategory)
        }
        configure<ActionModes> {
            register(DefaultMode)
        }
        configure<Actions> {
            register(NullAction)
            register(SkipAction)
        }

        configure<Attributes> {
            register(Actor::actionHistory)
            register(Actor::behavior)
            register(Actor::behaviorBytes)
            register(Actor::location)
            register(Actor::waypoints)
            register(Actor::numberWeaponSlots)
            register(Cell::presentPropsList)
            register(Entity::areaEffect)
            register(Entity::causedStatusEffects)
            register(Entity::destructionEffect)
            register(Entity::directEffect)
            register(Entity::disguised)
            register(Entity::passiveEffectModifier)
            register(Entity::name)
            register(Entity::periodics)
            register(Entity::pronoun)
            register(Entity::providedPassiveEffectModifier)
            register(FactionEntity::defaultRecon)
            register(FactionEntity::relations)
            register(HeadedProp::heading)
            register(Item::cellFilling)
            register(Item::itemAcceptor)
            register(Item::placement)
            register(Item::propSize)
            register(Item::providedBoxes)
            register(Item::verb)
            register(LocatedEntity::elevation)
            register(LocatedEntity::pickupAble)
            register(LocatedEntity::transformWhenDropped)
            register(LocatedEntity::height)
            register(LocatedEntity::size)
            register(LocatedEntity::zShift)
            register(StatusEffect::duration)
            register(StatusEffect::triggerRange)
            register(World::everythingIsRevealed)
            register(World::worldFactory)
        }
        configure<Behaviors> {
            register(MoveCirclesBehavior)
            register(SkipBehavior)
        }
        configure<BlockerValueExtractors> {
            register(ZeroExtractor)
        }
        configure<EntityReferringAttributeHandlers> {
            register<ListReferringAttributeHandler<*>>()
            register<OptionalReferringAttributeHandler<*>>()
            register<SingleEntityReferringAttributeHandler<*>>()
        }
        configure<EntityTypes> {
            register(::Cell)
            register(::DummyEntity)
            register(::EquipmentSlot)
            register(::FactionEntity)
            register(::InventorySlot)
            register(::DropSlot)
            register(::ParticleSystem)
            register(::Prop)
            register(::PropSlot)
            register(::Slot)
            register(::StatusEffect)
            register(::ToolSlot)
            register(::Transient)
            register(::World)
        }
        configure<Factions> {
            register(SpectatorFaction)
        }
        configure<ItemAcceptors> {
            register(AcceptAll)
            register(EquipmentAcceptor)
            register(ToolAcceptor)
        }
    }

    override fun ModContext.postInstall() {
    }
}
