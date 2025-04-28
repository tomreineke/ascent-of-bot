package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.npc.behaviorBytes
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.points
import com.cerebrallychallenged.jun.math.geo.vec

abstract class Actor(initializer: Initializer) : SlotBearer(initializer), IniHolder, FactionMember, MovingEntity {
    private var _scheduledInTime: Int = MAGIC_UNSCHEDULED

    override val scheduledIniTime: Int
        get() = _scheduledInTime

    val maxIniTime: Int = Int.MAX_VALUE

    override fun setIniScheduled(iniTime: Int) {
        _scheduledInTime = iniTime
    }

    /**
     * Cell with the lowest position occupied by this actor.
     * For example, an actor with `diameter == 2` and `location == world.cellAt(vec(7, 4))`
     * occupies the cells at `(7, 4)`, `(8, 4)`, `(7, 5)`, and `(8, 5)`.
     */
    var location: Cell? by attribute(null)

    /**
     * List of waypoints an NPC actor is patrolling when not engaged in some other interaction like combat.
     */
    var waypoints: List<Cell>? by attribute(null)

    val checkedLocation: Cell
        get() = location ?: modelError("Actor $this must be located in a cell")

    /**
     * The positions this actor occupies if it was placed at the specified location.
     */
    internal fun occupiedPositions(location: Cell?): Bounds<Vec2i> =
            if (location != null) Bounds.byMinSize(location.position, Vec2i.ONE * diameter) else Bounds.empty2i()

    val occupiedPositions: Bounds<Vec2i>
        get() = occupiedPositions(location)

    private fun occupiedLocations(location: Cell?): Sequence<Cell> =
            occupiedPositions(location).points.map { world.cell[it] }

    val occupiedLocations: Sequence<Cell>
        get() = occupiedLocations(location)

    override fun remove() {
        for (cell in occupiedLocations) {
            cell.presentActor = null
        }
        factionEntity?.internalRemoveActor(this)
        super.remove()
    }

    override fun onAttributeChanged(change: WorldChange.AttributeChanged<*>) {
        super.onAttributeChanged(change)
        change.ifOf(Actor::location) { (_, _, newLocation, oldLocation) ->
            if (newLocation != oldLocation) {
                for (cell in occupiedLocations(oldLocation)) {
                    cell.presentActor = null
                }
                for (statusEffect in transitivelyCarriedStatusEffects) {
                    statusEffect.unregisterAtCells(positionOverride = oldLocation?.position2f)
                }

                for (cell in occupiedLocations(newLocation)) {
                    cell.presentActor?.let {
                        modelError("Cannot move actor $this to $newLocation as $it is already present")
                    }
                    cell.presentActor = this
                }
                for (statusEffect in transitivelyCarriedStatusEffects) {
                    statusEffect.registerAtCells(positionOverride = newLocation?.position2f)
                }
            }
        }
        change.ifOf(Actor::behavior) {
            behaviorBytes = null
        }
    }

    override var factionEntity: FactionEntity? = null
        set(newFactionEntity) {
            field?.internalRemoveActor(this)
            newFactionEntity?.internalAddActor(this)
            field = newFactionEntity
            world.notify(WorldChange.FactionMembershipChanged(this, newFactionEntity?.faction))
        }

    override val isLocated: Boolean
        get() = location != null

    override val position: Vec2i
        get() = checkedLocation.position

    override val basePoint: Vec3f
        get() {
            val cellBasePoint = checkedLocation.basePoint + vec(0.5f, 0.5f, 0.0f) * (diameter - 1)
            val zShift = zShift
            return if (zShift != 0.0f) {
                cellBasePoint + Vec3f.UNIT_Z * zShift
            } else {
                cellBasePoint
            }
        }
}
