package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.base.PROP_SLOT_NAME
import com.cerebrallychallenged.hypogean.model.base.PropSlot
import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.model.maps.EntitySet
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec

class Cell(initializer: Initializer) : SlotBearer(initializer), LocatedEntity {

    init {
        initializer.defineSlot<PropSlot>(PROP_SLOT_NAME) {
            providedBoxes = vec(8, 8)
            name = "Props"
        }
        zShift = 0.0f
    }

    override val position: Vec2i = (initializer as CellInitializer).position

    var presentActor: Actor? = null
        /**
         * Must only be called from Actor.
         */
        internal set


    override val isLocated: Boolean = true

    override fun remove() {
        if (presentActor != null) {
            modelError("Cannot remove cell $this with present slotBearer")
        }
        super.remove()
    }

    override fun toString(): String = "${javaClass.simpleName} [$id] @ $position"

    override val position2f: Vec2f
        get() = position.toFloat()

    override val basePoint: Vec3f
        get() = position2f.append(zShift)

    internal val _nearStatusEffects: MutableSet<StatusEffect> = EntitySet(world, StatusEffect::class.java)

    val nearStatusEffects: Set<StatusEffect> = _nearStatusEffects
}
