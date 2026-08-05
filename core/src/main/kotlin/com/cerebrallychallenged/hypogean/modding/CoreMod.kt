package com.cerebrallychallenged.hypogean.modding

import com.cerebrallychallenged.hypogean.linguistics.pronoun
import com.cerebrallychallenged.hypogean.linguistics.verb
import com.cerebrallychallenged.hypogean.model.*
import com.cerebrallychallenged.hypogean.model.action.*
import com.cerebrallychallenged.hypogean.model.attribute.*
import com.cerebrallychallenged.hypogean.model.base.*
import com.cerebrallychallenged.hypogean.model.containment.AcceptAll
import com.cerebrallychallenged.hypogean.model.containment.ItemAcceptors
import com.cerebrallychallenged.hypogean.model.containment.itemAcceptor
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.dialog.DialogRoles
import com.cerebrallychallenged.hypogean.model.dialog.Dialogs
import com.cerebrallychallenged.hypogean.model.effect.*
import com.cerebrallychallenged.hypogean.npc.*
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
            register(Entity::providedOffensiveModifier)
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
