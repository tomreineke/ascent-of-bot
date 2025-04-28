package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.activestate.ActiveWorldState
import com.cerebrallychallenged.hypogean.model.attribute.Attribute
import com.cerebrallychallenged.hypogean.model.containment.ContainerPosition
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.math.geo.Vec2i
import kotlin.reflect.KProperty1

internal interface WorldChangeDto {
    context(World)
    fun applyChange()
}

sealed class WorldChange {
    internal abstract fun toDto(): WorldChangeDto

    abstract fun <R> accept(visitor: Visitor<R>): R

    abstract suspend fun <R> accept(visitor: SuspendVisitor<R>): R

    open val duration: Float
        get() = 0.0f

    data class Clear(val initialIniTime: Int) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            log.trace { "Clear(initialIniTime=$initialIniTime)" }
            clear(initialIniTime)
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class Removed(val entity: Entity) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            log.trace { "Removed(entity=$entity)" }
            if (entity.isAlive) {
                entity.remove()
            }
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    abstract class EntityCreated : WorldChange() {
        abstract val entity: Entity
    }

    data class CellCreated(val cell: Cell) : EntityCreated() {
        private class CellCreatedDto(val position: Vec2i, val id: Int): WorldChangeDto {
            context(World)
            override fun applyChange() {
                log.trace { "CellCreated(position=$position, id=$id)" }
                dependentCreate(position, id)
            }
        }

        override fun toDto(): WorldChangeDto = CellCreatedDto(cell.position, cell.id)

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)

        override val entity: Entity
            get() = cell
    }

    data class ActorCreated(val actor: Actor, val isAlive: Boolean) : EntityCreated() {
        private class ActorCreatedDto(val id: Int, val actorType: EntityType<Actor>): WorldChangeDto {
            context(World)
            override fun applyChange() {
                log.trace { "ActorCreated(id=$id, actorType=$actorType)" }
                dependentCreate(actorType, id, isAlive)
            }
        }

        override fun toDto(): WorldChangeDto = ActorCreatedDto(actor.id, actor.type)

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)

        override val entity: Entity
            get() = actor
    }

    data class ItemCreated(val item: Item, val isAlive: Boolean) : EntityCreated() {
        private class ItemCreatedDto(val id: Int, val itemType: EntityType<Item>): WorldChangeDto {
            context(World)
            override fun applyChange() {
                log.trace { "ItemCreated(id=$id, itemType=$itemType)" }
                dependentCreate(itemType, id, isAlive)
            }
        }

        override fun toDto(): WorldChangeDto = ItemCreatedDto(item.id, item.type)

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)

        override val entity: Entity
            get() = item
    }

    data class EventCreated(val event: Event, val isAlive: Boolean) : EntityCreated() {
        private class EventCreatedDto(val id: Int, val eventType: EntityType<Event>): WorldChangeDto {
            context(World)
            override fun applyChange() {
                log.trace { "EventCreated(id=$id, eventType=$eventType)" }
                dependentCreate(eventType, id, isAlive)
            }
        }

        override fun toDto(): WorldChangeDto = EventCreatedDto(event.id, event.type)

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)

        override val entity: Entity
            get() = event
    }

    data class StatusEffectCreated(val statusEffect: StatusEffect, val isAlive: Boolean) : EntityCreated() {
        class StatusEffectCreatedDto(
            val bearerId: Int,
            val id: Int,
            val statusEffectType: EntityType<StatusEffect>,
            val isAlive: Boolean
        ): WorldChangeDto {
            context(World)
            override fun applyChange() {
                val bearer = entityById(bearerId)
                log.trace { "StatusEffectCreated(bearer=$bearer, id=$id, statusEffectType=$statusEffectType)" }
                dependentCreate(statusEffectType, bearer, id, isAlive)
            }
        }

        override fun toDto(): WorldChangeDto =
            StatusEffectCreatedDto(statusEffect.bearer.id, statusEffect.id, statusEffect.type, isAlive)

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)

        override val entity: Entity
            get() = statusEffect
    }

    data class TransientCreated(val transientEntity: Transient, val isAlive: Boolean) : EntityCreated() {
        private class TransientCreatedDto(val id: Int, val transientType: EntityType<Transient>): WorldChangeDto {
            context(World)
            override fun applyChange() {
                log.trace { "TransientCreated(id=$id, transientType=$transientType)" }
                dependentCreate(transientType, id, isAlive)
            }
        }

        override fun toDto(): WorldChangeDto = TransientCreatedDto(transientEntity.id, transientEntity.type)

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)

        override val entity: Entity
            get() = transientEntity
    }

    data class AttributeChanged<T>(
            val entity: Entity,
            val attribute: Attribute<T>,
            val value: T,
            val prevValue: T
    ) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            log.trace { "AttributeChanged(entity=$entity, attribute=$attribute, value=$value)" }
            attribute.setValue(entity, value)
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)

        fun <U> asOf(property: KProperty1<*, U>): AttributeChanged<U>? {
            return if (this.attribute.property == property) {
                @Suppress("UNCHECKED_CAST")
                this as AttributeChanged<U>
            } else {
                null
            }
        }

        inline fun <U> ifOf(property: KProperty1<*, U>, body: (AttributeChanged<U>) -> Unit) {
            asOf(property)?.apply(body)
        }

        fun <U> asOfAttribute(attribute: Attribute<U>): AttributeChanged<U>? {
            return if (this.attribute === attribute) {
                @Suppress("UNCHECKED_CAST")
                this as AttributeChanged<U>
            } else {
                null
            }
        }

        inline fun <U> ifOfAttribute(attribute: Attribute<U>, body: (AttributeChanged<U>) -> Unit) {
            asOfAttribute(attribute)?.apply(body)
        }
    }

    data class ReconChanged(val entity: Entity, val reconByFaction: Map<FactionEntity, Recon>) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            world.reconTable.process(this)
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class SlotAdded(val slotBearer: SlotBearer, val slotId: String, val slot: Slot) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            log.trace { "SlotAdded(slotBearer=$slotBearer, slotId=$slotId, slot=$slot)" }
            slotBearer.addSlot(slotId, slot)
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class ItemMove(
        val movedItem: Item,
        val oldContainerPosition: ContainerPosition?,
        val newContainerPosition: ContainerPosition?
    ) : WorldChange(), WorldChangeDto {
        constructor(movedItem: Item, newContainerPosition: ContainerPosition?) :
                this(movedItem, movedItem.containerPosition, newContainerPosition)

        context(World)
        override fun applyChange() {
            log.trace {
                "ItemInsertion(movedItem=$movedItem, oldContainerPosition=$oldContainerPosition, newContainerPosition=$newContainerPosition)"
            }
            if (movedItem.containerPosition != oldContainerPosition) {
                modelError("Item insertion of $movedItem failed")
            }
            movedItem.containerPosition = newContainerPosition
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class FactionMembershipChanged(
            val factionMember: FactionMember,
            val faction: Faction?
    ) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            log.trace {
                "FactionMembershipChanged(factionMember=$factionMember, faction=${faction})"
            }
            factionMember.faction = faction
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class IniEnqueue(val iniTime: Int, val iniHolder: IniHolder) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            log.trace { "IniEnqueue(iniTime=$iniTime, iniHolder=$iniHolder)" }
            iniQueue.enqueueAbsolute(iniTime, iniHolder)
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class IniDequeue(val iniHolder: IniHolder) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            log.trace { "IniEnqueue(iniHolder=$iniHolder)" }
            val actualIniHolder = iniQueue.dequeue()
            if (iniHolder != actualIniHolder) {
                modelError("Expected to dequeue ini holder $iniHolder, but actually got $actualIniHolder")
            }
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class IniRemove(val iniHolder: IniHolder) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            log.trace { "IniRemove(iniHolder=$iniHolder)" }
            iniQueue.remove(iniHolder)
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class IniIncTime(val newIniTime: Int) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            log.trace { "IniIncTime(newIniTime=$newIniTime)" }
            val actualNewIniTime = iniQueue.incTime()
            if (actualNewIniTime != newIniTime) {
                modelError("Time mismatch. primary: $newIniTime dependent: $actualNewIniTime")
            }
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class ActiveStateChanged(val activeState: ActiveWorldState) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            world.activeState = activeState
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)
    }

    data class ViewEventHappened(val viewEvent: ViewEvent) : WorldChange(), WorldChangeDto {
        context(World)
        override fun applyChange() {
            notify(ViewEventHappened(viewEvent))
        }

        override fun toDto(): WorldChangeDto = this

        override fun <R> accept(visitor: Visitor<R>): R = visitor.visit(this)

        override suspend fun <R> accept(visitor: SuspendVisitor<R>): R = visitor.visit(this)

        override val duration: Float
            get() = viewEvent.duration
    }

    interface Visitor<R> {
        fun visit(change: Clear): R = visitDefault(change)
        fun visit(change: Removed): R = visitDefault(change)
        fun visit(change: CellCreated): R = visitDefault(change)
        fun visit(change: ActorCreated): R = visitDefault(change)
        fun visit(change: ItemCreated): R = visitDefault(change)
        fun visit(change: EventCreated): R = visitDefault(change)
        fun visit(change: StatusEffectCreated): R = visitDefault(change)
        fun visit(change: TransientCreated): R = visitDefault(change)
        fun <T> visit(change: AttributeChanged<T>): R = visitDefault(change)
        fun visit(change: ReconChanged): R = visitDefault(change)
        fun visit(change: SlotAdded): R = visitDefault(change)
        fun visit(change: ItemMove): R = visitDefault(change)
        fun visit(change: FactionMembershipChanged): R = visitDefault(change)
        fun visit(change: IniEnqueue): R = visitDefault(change)
        fun visit(change: IniDequeue): R = visitDefault(change)
        fun visit(change: IniRemove): R = visitDefault(change)
        fun visit(change: IniIncTime): R = visitDefault(change)
        fun visit(change: ActiveStateChanged): R = visitDefault(change)
        fun visit(change: ViewEventHappened): R = visitDefault(change)

        fun visitDefault(change: WorldChange): R
    }

    interface SimpleVisitor : Visitor<Unit> {
        override fun visitDefault(change: WorldChange) {}
    }

    interface SuspendVisitor<R> {
        suspend fun visit(change: Clear): R = visitDefault(change)
        suspend fun visit(change: Removed): R = visitDefault(change)
        suspend fun visit(change: CellCreated): R = visitDefault(change)
        suspend fun visit(change: ActorCreated): R = visitDefault(change)
        suspend fun visit(change: ItemCreated): R = visitDefault(change)
        suspend fun visit(change: EventCreated): R = visitDefault(change)
        suspend fun visit(change: StatusEffectCreated): R = visitDefault(change)
        suspend fun visit(change: TransientCreated): R = visitDefault(change)
        suspend fun <T> visit(change: AttributeChanged<T>): R = visitDefault(change)
        suspend fun visit(change: ReconChanged): R = visitDefault(change)
        suspend fun visit(change: SlotAdded): R = visitDefault(change)
        suspend fun visit(change: ItemMove): R = visitDefault(change)
        suspend fun visit(change: FactionMembershipChanged): R = visitDefault(change)
        suspend fun visit(change: IniEnqueue): R = visitDefault(change)
        suspend fun visit(change: IniDequeue): R = visitDefault(change)
        suspend fun visit(change: IniRemove): R = visitDefault(change)
        suspend fun visit(change: IniIncTime): R = visitDefault(change)
        suspend fun visit(change: ActiveStateChanged): R = visitDefault(change)
        suspend fun visit(change: ViewEventHappened): R = visitDefault(change)

        suspend fun visitDefault(change: WorldChange): R
    }

    interface SimpleSuspendVisitor : SuspendVisitor<Unit> {
        override suspend fun visitDefault(change: WorldChange) {}
    }
}


class WorldChangeBuffer : (WorldChange) -> Unit, Iterable<WorldChange> {
    private var changes = mutableListOf<WorldChange>()

    override fun invoke(change: WorldChange) {
        changes.add(change)
    }

    fun clear() {
        changes.clear()
    }

    override fun iterator(): Iterator<WorldChange> = changes.iterator()
}
