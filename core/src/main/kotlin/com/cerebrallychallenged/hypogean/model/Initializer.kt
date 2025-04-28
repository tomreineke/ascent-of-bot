package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.jun.math.geo.Vec2i
import kotlin.random.Random

/**
 * This is passed down the constructors for all subclasses of [Entity].
 */
sealed class Initializer {
    abstract val id: Int
}

internal class WorldInitializer(val rulebook: Rulebook, val random: Random, val isPrimary: Boolean) : Initializer() {
    override val id: Int = 1
}

open class NonWorldInitializer(val world: World, override val id: Int) : Initializer() {
    private class StatusEffectInitialization<T : StatusEffect>(
        val statusEffectType: EntityType<T>,
        val statusEffectInitializer: ((T) -> Unit)?,
        val createsNew: Boolean
    ) {
        fun execute(bearer: Entity) {
            val statusEffect = if (createsNew) {
                bearer.createStatusEffect(statusEffectType)
            } else {
                bearer.statusEffects.firstNotNullOfOrNull(statusEffectType::asInstance)
                    ?: modelError("Cannot modify status effect of type $statusEffectType for $bearer as it does not exist")
            }
            statusEffectInitializer?.let { it(statusEffect) }
        }
    }

    private val statusEffectInitializations = mutableListOf<StatusEffectInitialization<out StatusEffect>>()

    @PublishedApi
    internal fun <T : StatusEffect> addStatusEffectInitialization(
        statusEffectType: EntityType<T>,
        statusEffectInitializer: ((T) -> Unit)?,
        createsNew: Boolean
    ) {
        statusEffectInitializations.add(
            StatusEffectInitialization(statusEffectType, statusEffectInitializer, createsNew)
        )
    }

    internal fun applyStatusEffectInitializations(bearer: Entity) {
        for (statusEffectInitialization in statusEffectInitializations) {
            statusEffectInitialization.execute(bearer)
        }
    }
}

open class SlotBearerInitializer(world: World, id: Int) : NonWorldInitializer(world, id) {
    private class SlotDefinition<T : Slot>(
        val slotType: EntityType<T>,
        val slotId: String,
        val slotInitializer: (T) -> Unit
    )

    private val slotDefinitions = mutableListOf<SlotDefinition<out Slot>>()

    @PublishedApi
    internal fun <T : Slot> addSlotDefinition(slotType: EntityType<T>, slotId: String, slotInitializer: (T) -> Unit) {
        slotDefinitions.add(SlotDefinition(slotType, slotId, slotInitializer))
    }

    internal fun applySlotDefinitionsTo(slotBearer: SlotBearer) {
        val world = slotBearer.world
        for ((slotId, definitions) in slotDefinitions.groupBy { it.slotId }) {
            // If there is for example an "EquipmentSlot" for "Robot" but a "FancyEquipmentSlot" for "FancyRobot"
            // we want the most specialized slot type, in this case "FancyEquipmentSlot".
            val slotType = definitions.asSequence().map { it.slotType }.reduce(::min)
            val slot = world.create(slotType)
            for (definition in definitions) {
                applySlotDefinition(definition, slot)
            }
            slotBearer.addSlot(slotId, slot)
        }
    }

    private fun <T : Slot> applySlotDefinition(definition: SlotDefinition<T>, slot: Slot) {
        // Safe as the type of the slot was determined by finding the common subtype of all definitions.
        @Suppress("UNCHECKED_CAST")
        definition.slotInitializer(slot as T)
    }
}

internal class CellInitializer(world: World, id: Int, val position: Vec2i) : SlotBearerInitializer(world, id)

internal class WallInitializer(world: World, id: Int, val source: Cell, val target: Cell) : SlotBearerInitializer(world, id)

internal class ActorInitializer(world: World, id: Int) : SlotBearerInitializer(world, id)

internal class FactionInitializer(world: World, id: Int, val faction: Faction) : SlotBearerInitializer(world, id)

internal class StatusEffectInitializer(world: World, id: Int, val bearer: Entity) : NonWorldInitializer(world, id)
