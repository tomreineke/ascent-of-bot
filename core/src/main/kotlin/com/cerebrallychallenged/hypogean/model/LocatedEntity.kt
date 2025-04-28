package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.presentProps
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import kotlin.math.max

/**
 * {@code LocatedEntity} is implemented by entities located in the world at integer coordinates.
 * Some entities can be temporarily without location, e.g., {@link Actor}s.
 * Hence, {@link #getPosition()} must only be called if {@link #isLocated()} returns {@code true}.
 */
interface LocatedEntity : Entity {
    val position: Vec2i

    val isLocated: Boolean

    val position2f: Vec2f
        get() = position.toFloat()

    val basePoint: Vec3f

    val centerPoint: Vec3f
        get() = basePoint + Vec3f.UNIT_Z * height * 0.5f

    val topPoint: Vec3f
        get() = basePoint + Vec3f.UNIT_Z * height


}

var LocatedEntity.size: Vec2i by attribute(Vec2i.ONE)

var LocatedEntity.zShift: Float by attribute(0.0f)

/**
 * This attribute affects calculations for visibility and targetibility as well as some visual effects
 * like where the overhead texts (see [com.cerebrallychallenged.hypogean.view.map.events.OverheadTextEvent])
 * appear over the specific entity.
 * However, it does not affect the visible height of the entity. The visible height is determined by the asset
 * used for the entity.
 */
var LocatedEntity.height: Float by attribute(0.0f)

var LocatedEntity.elevation: Float by attribute(0.0f)

/**
 * If an entity can be picked up by an actor. For these entities a
 * [com.cerebrallychallenged.hypogean.vanilla.actions.PickupActionInstance] can be created.
 */
var LocatedEntity.pickupAble: Boolean by attribute(false)

/**
 * Transform for an entity when lying on the ground / when it is dropped to the ground,
 * depending on x and y coordinates.
 */
var LocatedEntity.transformWhenDropped: (x: Int, y: Int) -> Transform3f by attribute { _, _ ->
    Transform3f(
        Quaternion.IDENTITY,
        Vec3f.ZERO,
        Vec3f.ONE
    )
}

var Item.cellFilling: Boolean by attribute(false)

val LocatedEntity.presentCellFilling: Boolean
    get() = when (this) {
        is Item -> cellFilling
        is Cell -> presentProps.any { it.cellFilling }
        else -> false
    }

val LocatedEntity.presentHeight: Float
    get() = when (this) {
        is Cell -> max(height, presentProps.maxOfOrNull { it.height } ?: 0.0f)
        else -> height
    }
