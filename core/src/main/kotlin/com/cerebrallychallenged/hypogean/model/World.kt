package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.activestate.ActiveWorldState
import com.cerebrallychallenged.hypogean.activestate.SimulationState
import com.cerebrallychallenged.hypogean.model.action.ActionLibrary
import com.cerebrallychallenged.hypogean.model.attribute.AttributeStore
import com.cerebrallychallenged.hypogean.model.base.DummyEntity
import com.cerebrallychallenged.hypogean.pathfinding.CachingPaths
import com.cerebrallychallenged.hypogean.pathfinding.MovementGraph
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor
import com.cerebrallychallenged.hypogean.rays.CachingRays
import com.cerebrallychallenged.hypogean.rays.RaysQuery
import com.cerebrallychallenged.hypogean.rays.ZeroExtractor
import com.cerebrallychallenged.hypogean.util.kryo.WorldKryo
import com.cerebrallychallenged.jun.math.geo.Vec2i
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import kotlin.random.Random

class World(initializer: Initializer) : AbstractEntity(initializer) {
    override val rulebook: Rulebook

    val random: Random

    val isPrimary: Boolean

    init {
        val worldInitializer = initializer as WorldInitializer
        rulebook = worldInitializer.rulebook
        random = worldInitializer.random
        isPrimary = worldInitializer.isPrimary
    }

    override val world: World
        get() = this

    val kryo = WorldKryo(this)

    var changes: ChangeSchedule = ChangeSchedule(kryo)
        private set

    internal val attributeStore = AttributeStore(rulebook, this)

    private val entityById: Int2ObjectMap<Entity> = Int2ObjectLinkedOpenHashMap(mapOf(id to this))

    val entities: Collection<Entity>
        get() = entityById.values

    internal val cellByPosition: MutableMap<Vec2i, Cell> = Object2ObjectLinkedOpenHashMap()

    private val _cells: MutableSet<Cell> = ObjectLinkedOpenHashSet()

    val cells: Set<Cell>
        get() = _cells

    private val _actors: MutableSet<Actor> = ObjectLinkedOpenHashSet()

    val actors: Set<Actor>
        get() = _actors

    private val _items: MutableSet<Item> = ObjectLinkedOpenHashSet()

    val items: Set<Item>
        get() = _items

    /**
     * Item that acts
     * as a placeholder tool for actions that require no tool,
     * and as a placeholder target for actions that require no target.
     */
    val dummyEntity: Item = DummyEntity(NonWorldInitializer(this, 2)).also {
        entityById[2] = it
        entityUnderConstruction = null
    }

    private val _factions: Map<Faction, FactionEntity>
            = rulebook.factions.withIndex().associateTo(Object2ObjectArrayMap()) { (i, faction) ->
        val id = i + 1000
        faction to FactionEntity(FactionInitializer(this, id, faction)).also {
            entityById[id] = it
            faction.initialize(it)
            entityUnderConstruction = null
        }
    }

    fun factionEntity(faction: Faction): FactionEntity = _factions.getValue(faction)

    private val _events: MutableSet<Event> = ObjectLinkedOpenHashSet()

    val events: Set<Event>
        get() = _events

    private val _statusEffects: MutableSet<StatusEffect> = ObjectLinkedOpenHashSet()

    val allStatusEffects: Set<StatusEffect>
        get() = _statusEffects

    private val _transients: MutableSet<Transient> = ObjectLinkedOpenHashSet()

    val transients: Set<Transient>
        get() = _transients

    private val _graveyard: MutableList<Entity> = ArrayList()

    val graveyard: List<Entity>
        get() = _graveyard

    val iniQueue = IniQueue(this, 0)

    val currentIniTime: Int
        get() = iniQueue.currentIniTime

    val actions = ActionLibrary(this)

    /**
     * Is set by the constructor of [NonWorldEntity] to enable the property [Entity.isUnderConstruction].
     */
    internal var entityUnderConstruction: Entity? = null

    var activeState: ActiveWorldState = SimulationState
        internal set(value) {
            field = value
            value.activate()
            notify(WorldChange.ActiveStateChanged(value))
        }

    private val cachingRays = CachingRays(rulebook.rayStencil, this)

    private val cachingPaths = CachingPaths()

    internal val reconTable = ReconTable(this)

    /**
     * Removes the specified entity. Must only be called from the entity itself.
     * @param entity the entity to remove.
     */
    internal fun removeMe(entity: Entity) {
        val currentState = activeState
        if (currentState is ActiveActorState && currentState.activeActor === entity) {
            activeState = SimulationState
        }
        if (entity is IniHolder) {
            iniQueue.remove(entity)
        }
        notify(WorldChange.Removed(entity))
        when (entity) {
            is Cell -> {
                _cells.remove(entity)
                cellByPosition.remove(entity.position)
            }
            is Actor -> {
                _actors.remove(entity)
            }
            is Item -> {
                _items.remove(entity)
            }
            is Event -> {
                _events.remove(entity)
            }
            is StatusEffect -> {
                _statusEffects.remove(entity)
            }
            is Transient -> {
                _transients.remove(entity)
            }
        }
        _graveyard.add(entity)
    }

    override fun collectInitialChanges(collector: (WorldChange) -> Unit) {
        collector(WorldChange.Clear(iniQueue.currentIniTime))
        for (cell in cells) {
            collector(WorldChange.CellCreated(cell))
        }
        for (actor in actors) {
            collector(WorldChange.ActorCreated(actor, true))
        }
        for (item in items) {
            collector(WorldChange.ItemCreated(item, true))
        }
        for (event in events) {
            collector(WorldChange.EventCreated(event, true))
        }
        for (statusEffect in allStatusEffects) {
            collector(WorldChange.StatusEffectCreated(statusEffect, true))
        }
        for (transient in transients) {
            collector(WorldChange.TransientCreated(transient, true))
        }
        for (entity in graveyard) {
            when (entity) {
                is Actor -> collector(WorldChange.ActorCreated(entity, false))
                is Item -> collector(WorldChange.ItemCreated(entity, false))
                is Event -> collector(WorldChange.EventCreated(entity, false))
                is Transient -> collector(WorldChange.TransientCreated(entity, false))
            }
        }
        reconTable.collectInitialChanges(collector)
        val iter = entities.iterator()
        val skippedEntity = iter.next() // Skip self, rather call the super method.
        assert(skippedEntity === this)
        super.collectInitialChanges(collector)
        for (entity in iter) {
            entity.collectInitialChanges(collector)
        }
        iniQueue.collectInitialChanges(collector)
        collector(WorldChange.ActiveStateChanged(activeState))
    }

    fun createInitialWorldChanges(): ChangeScheduleDto =
        ChangeSchedule(kryo).also { collectInitialChanges(it::addChange) }.toDto()

    internal fun notify(change: WorldChange) {
        cachingRays.invalidateCache()
        cachingPaths.invalidateCache()
        reconTable.invalidate()
        changes.addChange(change)
    }

    fun notifyViewEvent(viewEvent: ViewEvent) {
        notify(WorldChange.ViewEventHappened(viewEvent))
    }

    fun flush(): ChangeScheduleDto = changes.also { changes = ChangeSchedule(kryo) }.toDto()

    fun cellAt(position: Vec2i): Cell? = cellByPosition[position]

    fun queryRays(
            sourcePosition: Vec2i,
            blockerValueExtractor: BlockerValueExtractor = ZeroExtractor,
            actingSubject: Any? = null
    ): RaysQuery = cachingRays.query(sourcePosition, blockerValueExtractor, actingSubject)

    fun shortestPath(movementGraph: MovementGraph): CachingPaths.ShortestPaths = cachingPaths.query(movementGraph)

    fun clear(initialIniTime: Int) {
        entityById.clear()
        entityById[id] = this
        entityById[dummyEntity.id] = dummyEntity
        for (faction in _factions.values) {
            faction.clear()
            entityById[faction.id] = faction
        }
        cellByPosition.clear()
        _cells.clear()
        _actors.clear()
        _items.clear()
        _events.clear()
        _statusEffects.clear()
        _transients.clear()
        attributeStore.clear()
        iniQueue.clear(initialIniTime)
        notify(WorldChange.Clear(initialIniTime))
        for (faction in _factions.values) {
            faction.faction.initialize(faction)
        }
        activeState = SimulationState
    }

    fun entityById(id: Int): Entity = entityById[id] ?: modelError("No entity found with id $id")

    inline fun <reified T : Entity> byId(id: Int): T = entityById(id).checkedType()

    fun <T : Entity> byId(id: Int, entityClass: Class<T>): T = entityById(id).checkedType(entityClass)

    fun nullableEntityById(id: Int): Entity? = entityById[id]

    inline fun <reified T : Entity> nullableById(id: Int): T? = nullableEntityById(id)?.checkedType()

    fun <T : Entity> nullableById(id: Int, entityClass: Class<T>): T? = nullableEntityById(id)?.checkedType(entityClass)

    /**
     * Creates a random free id for a new entity. It is a negative integer.
     * Note that positive ids are given to entities with a predetermined id such as this world itself (id=1), the
     * dummy entity (id=2), and factions.
     */
    private fun createEntityId(): Int {
        var id: Int
        do {
            // Sample a random negative integer: Filter out the unsigned bits, then flip the sign.
            id = (random.nextInt() and 0x7fffffff) xor 0x80000000.toInt()
        } while (id in entityById)
        return id
    }

    /**
     * Creates a cell using the specified factory.
     * This is the preferred method if the desired subclass of [Cell] is statically known.
     * Example:
     *     world.create(::CellSandstoneFloor, vec(5, 2))
     */
    fun create(position: Vec2i): Cell = internalCreate(position, createEntityId(), true)

    /**
     * Creates a [Cell] for this dependent world.
     * Must only be called from subclasses of [WorldChangeApplicator.readAndApply].
     */
    internal fun dependentCreate(position: Vec2i, id: Int): Cell = internalCreate(position, id, false)

    private fun internalCreate(position: Vec2i, id: Int, isPrimary: Boolean): Cell {
        val initializer = CellInitializer(this, id, position)
        val cell = Cell(initializer)
        entityUnderConstruction = null
        entityById[id] = cell
        _cells.add(cell)
        val oldCell = cellByPosition.put(position, cell)
        oldCell?.let { modelError("Cannot create another cell at $position, already occupied by $it") }
        notify(WorldChange.CellCreated(cell))
        if (isPrimary) {
            initializer.applySlotDefinitionsTo(cell)
            initializer.applyStatusEffectInitializations(cell)
        }
        return cell
    }

    /**
     * Creates an [Actor] using the specified factory.
     * This is the preferred method if the desired subclass of [Actor] is statically known.
     * Example:
     *     val location: Cell = ...
     *     world.create(::Robot, location)
     */
    fun <T: Actor> create(factory: (Initializer) -> T, location: Cell?): T {
        val actor = internalCreate(factory, createEntityId(), isPrimary = true, isAlive = true)
        if (location != null) {
            actor.location = location
        }
        return actor
    }

    /**
     * Creates an [Actor] for this dependent world.
     * Must only be called from subclasses of [WorldChangeApplicator.readAndApply].
     */
    fun <T: Actor> dependentCreate(actorType: EntityType<T>, id: Int, isAlive: Boolean): T
        = internalCreate(actorType.factory, id, false, isAlive)

    private fun <T: Actor> internalCreate(factory: (Initializer) -> T, id: Int, isPrimary: Boolean, isAlive: Boolean): T {
        val initializer = ActorInitializer(this, id)
        val actor = factory(initializer)
        entityUnderConstruction = null
        entityById[id] = actor
        if (isAlive) {
            _actors.add(actor)
        } else {
            _graveyard.add(actor)
        }
        notify(WorldChange.ActorCreated(actor, isAlive))
        if (isPrimary) {
            initializer.applySlotDefinitionsTo(actor)
            initializer.applyStatusEffectInitializations(actor)
        }
        return actor
    }

    /**
     * Creates an [Item], [Event], or [Transient] using the specified factory.
     * This is the preferred method if the desired subclass is statically known.
     * Example:
     *     world.create(::RocketLauncher)
     */
    fun <T: ItemOrOrEventOrTransient> create(factory: (Initializer) -> T): T
            = internalCreate(factory, createEntityId(), true)

    /**
     * Creates an [Item], [FactionEntity], [Event], or [Transient] for this dependent world.
     * Must only be called from subclasses of [WorldChangeApplicator.readAndApply].
     */
    internal fun <T: ItemOrOrEventOrTransient> dependentCreate(entityType: EntityType<T>, id: Int, isAlive: Boolean): T
            = internalCreate(entityType.factory, id, isAlive)

    private fun <T: ItemOrOrEventOrTransient> internalCreate(factory: (Initializer) -> T, id: Int, isAlive: Boolean): T {
        val initializer = NonWorldInitializer(this, id)
        val entity = factory(initializer)
        entityUnderConstruction = null
        entityById[id] = entity
        // We cannot declare setAllInternal in Entity, as then it would have to be public.
        when (entity) {
            is Item -> {
                if (isAlive) {
                    _items.add(entity)
                } else {
                    _graveyard.add(entity)
                }
                notify(WorldChange.ItemCreated(entity, isAlive))
            }
            is Event -> {
                if (isAlive) {
                    _events.add(entity)
                } else {
                    _graveyard.add(entity)
                }
                notify(WorldChange.EventCreated(entity, isAlive))
            }
            is Transient -> {
                if (isAlive) {
                    _transients.add(entity)
                } else {
                    _graveyard.add(entity)
                }
                notify(WorldChange.TransientCreated(entity, isAlive))
            }
        }
        if (isPrimary) {
            initializer.applyStatusEffectInitializations(entity)
        }
        return entity
    }

    /**
     * Creates a [StatusEffect] for this dependent world.
     * Must only be called from subclasses of [WorldChangeApplicator.readAndApply].
     */
    internal fun <T: StatusEffect> dependentCreate(
        statusEffectType: EntityType<T>,
        bearer: Entity,
        id: Int,
        isAlive: Boolean
    ): T = internalCreate(statusEffectType.factory, bearer, id, isAlive)

    internal fun <T: StatusEffect> internalCreate(
        factory: (Initializer) -> T,
        bearer: Entity,
        id: Int = createEntityId(),
        isAlive: Boolean
    ): T {
        val initializer = StatusEffectInitializer(this, id, bearer)
        val statusEffect = factory(initializer)
        entityUnderConstruction = null
        entityById[id] = statusEffect
        if (isAlive) {
            (bearer as AbstractEntity).internalAddStatusEffect(statusEffect)
            _statusEffects.add(statusEffect)
            statusEffect.registerAtCells()
        } else {
            _graveyard.add(statusEffect)
        }
        notify(WorldChange.StatusEffectCreated(statusEffect, isAlive))
        if (isPrimary) {
            initializer.applyStatusEffectInitializations(statusEffect)
        }
        return statusEffect
    }

    fun updateRecon() {
        reconTable.recompute()
    }
}
