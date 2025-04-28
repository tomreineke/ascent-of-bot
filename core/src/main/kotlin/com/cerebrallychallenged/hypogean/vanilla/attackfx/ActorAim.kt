package com.cerebrallychallenged.hypogean.vanilla.attackfx

import com.cerebrallychallenged.hypogean.vanilla.attributes.heading
import com.cerebrallychallenged.hypogean.view.map.events.EntityMoveEvent

object ActorAim : AttackFx {
    context(AttackFx.Context)
    override suspend fun executeFx() {
        val initialPosition = attackingActor.basePoint
        val initialHeading = attackingActor.heading
        val shootingPosition = situation.shootingPosition2f
        val sideStepDelta = shootingPosition - initialPosition.xy
        val (shootingPosition3f, preAimHeading) = if (!sideStepDelta.isZero) {
            val sideStepHeading = sideStepDelta.angle()
            EntityMoveEvent
                .zRotation(attackingActor, initialPosition, initialHeading, sideStepHeading)?.notifyWorldAndDelay()
            // Sidestep movement
            val shootingPosition3f = shootingPosition.append(initialPosition.z)
            EntityMoveEvent
                .linearMove(attackingActor, initialPosition, shootingPosition3f, sideStepHeading)?.notifyWorldAndDelay()
            Pair(shootingPosition3f, sideStepHeading)
        } else {
            Pair(initialPosition, attackingActor.heading)
        }
        val aimHeading = (hitPosition.xy  - shootingPosition).angle()
        EntityMoveEvent.zRotation(attackingActor, shootingPosition3f, preAimHeading, aimHeading)?.notifyWorldAndDelay()
        if (!sideStepDelta.isZero) {
            schedule {
                delay(1.0f) // FIXME: depends on shooting
                EntityMoveEvent
                    .zRotation(attackingActor, shootingPosition3f, aimHeading, preAimHeading)?.notifyWorldAndDelay()
                EntityMoveEvent.linearMove(
                    attackingActor,
                    shootingPosition3f,
                    initialPosition,
                    preAimHeading
                )?.notifyWorldAndDelay()
                attackingActor.heading = preAimHeading
            }
        } else {
            attackingActor.heading = aimHeading
        }
    }
}
