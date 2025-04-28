package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.vanilla.attributes.ignoreLocationAndHeading
import com.cerebrallychallenged.jun.math.Angle

abstract class VisActor(mapView: MapView, entity: Actor) : VisEntity<Actor>(mapView, entity) {
    abstract fun updateLocation(location: Cell?)

    abstract fun updateHeading(heading: Angle)

    private val changeListener = object : WorldChange.SimpleSuspendVisitor {
        override suspend fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            change.ifOf(Actor::location) { (_, _, location, _) ->
                if (!entity.ignoreLocationAndHeading) {
                    updateLocation(location)
                }
            }
            change.ifOf(Actor::heading) { (_, _, heading, _) ->
                if (!entity.ignoreLocationAndHeading) {
                    updateHeading(heading)
                }
            }
            change.ifOf(Actor::ignoreLocationAndHeading) { (_, _, ignore, _) ->
                if (!ignore) {
                    updateLocation(entity.location)
                    updateHeading(entity.heading)
                }
            }
            change.ifOf(FactionEntity::relations) {
                updateStencilValue()
            }
        }

        override suspend fun visit(change: WorldChange.FactionMembershipChanged) {
            if (change.factionMember == entity) {
                updateStencilValue()
            }
        }
    }

    override suspend fun onChange(change: WorldChange) {
        super.onChange(change)
        change.accept(changeListener)
    }
}
