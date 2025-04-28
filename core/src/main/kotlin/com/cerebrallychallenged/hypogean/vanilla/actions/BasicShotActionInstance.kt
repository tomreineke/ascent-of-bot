package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.cascade.cascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.executeCascade
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.rays.HitResult
import com.cerebrallychallenged.hypogean.rays.sight
import com.cerebrallychallenged.hypogean.util.collections.WorldStatistic
import com.cerebrallychallenged.hypogean.util.collections.WorldStatisticRecorder
import com.cerebrallychallenged.hypogean.util.math.probability.AngleDistribution
import com.cerebrallychallenged.hypogean.util.math.probability.FloatDistribution
import com.cerebrallychallenged.hypogean.util.math.probability.constantDistribution
import com.cerebrallychallenged.hypogean.vanilla.ActionWithAccuracy
import com.cerebrallychallenged.hypogean.vanilla.angleDistribution
import com.cerebrallychallenged.hypogean.vanilla.attackfx.ActorAim
import com.cerebrallychallenged.hypogean.vanilla.attackfx.attackFx
import com.cerebrallychallenged.hypogean.vanilla.attributes.hasAdjustableRange
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealWeaponEffects
import com.cerebrallychallenged.hypogean.vanilla.cascade.handleEquipment
import com.cerebrallychallenged.hypogean.vanilla.computeAccuracy
import com.cerebrallychallenged.hypogean.vanilla.distanceDistribution
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon
import com.cerebrallychallenged.hypogean.vanilla.rays.BallisticExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.VisibilityExtractor
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.GroundMovement
import com.cerebrallychallenged.hypogean.view.map.events.PreloadAssetsEvent
import com.cerebrallychallenged.hypogean.view.report.reportHit
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.unreal.UObject
import kotlin.random.Random

/**
 * This class is generic so that we can use attributes of a tool that are specific for a special weapon.
 */
abstract class BasicShotActionInstance<W : Weapon>(
    activeActor: Actor,
    protected val assumedActiveActorLocation: Cell,
    final override val equipment: W,
    final override val target: LocatedEntity,
    action: ActionWithAccuracy
) : ActionInstance(action, activeActor) {
    override val initiativeCost: InitiativeCost = InitiativeCost.Delta(equipment.initiativeCost)

    companion object {
        fun computeAttackSituation(
            activeActor: Actor,
            activeActorLocation: Cell,
            target: LocatedEntity,
            action: ActionWithAccuracy,
            weapon: Weapon
        ): AttackSituation {
            // Compute from where the actor is shooting and where it is aiming at.
            val sight = activeActor.sight(
                VisibilityExtractor,
                if (activeActor.canSideStep) { GroundMovement(activeActor) } else null,
                activeActorLocation
            ).of(target)
                ?: modelError("Actor $activeActor can't see target $target, although enumerated as potential target")
            val aimedPosition = sight.targetCentroid
            val shootingPosition = sight.viewPosition
            val shootingPosition2f = shootingPosition.toFloat()
            val delta = aimedPosition - shootingPosition2f

            // Compute the actual shooting angle and adjusted distance considering limited accuracy.
            // (accuracy of Double.POSITIVE_INFINITY is valid for perfect aim)
            val accuracy = action.computeAccuracy(activeActor, weapon)
            return AttackSituation(
                activeActor,
                shootingPosition,
                shootingPosition2f,
                aimedPosition,
                angleDistribution(delta.angle(), accuracy),
                if (weapon.hasAdjustableRange) {
                    distanceDistribution(delta.length, accuracy)
                } else {
                    constantDistribution(Float.POSITIVE_INFINITY)
                }
            )
        }
    }

    override fun estimateConsequences(): WorldStatistic<IntProperty>? {
        require(target.position !in activeActor.occupiedPositions(assumedActiveActorLocation)) {
            "Cannot attack a position occupied by the active actor"
        }
        val attackSituation = computeAttackSituation(
            activeActor,
            assumedActiveActorLocation,
            target,
            (action as ActionWithAccuracy),
            equipment
        )
        val random = world.random
        val recorder = WorldStatisticRecorder<IntProperty>(world)
        repeat(100) {
            recorder.incCount()
            val hitResult = attackSituation.sampleHit(random)
            if (hitResult is HitResult.NoHit) return@repeat
            if (hitResult is HitResult.Hit) {
                for (hitEntity in hitResult.hitEntities) {
                    recorder.recordHit(hitEntity)
                }
            }
            executeCascade(recorder) {
                cascadeBlock {
                    dealWeaponEffects(equipment, hitResult)
                    handleEquipment(equipment)
                }
            }
        }
        return recorder.toWorldStatistic()
    }

    context(CascadeContext)
    override suspend fun execute() = cascadeBlock {
        require(target.position !in activeActor.occupiedPositions(assumedActiveActorLocation)) {
            "Cannot attack a position occupied by the active actor"
        }
        val attackSituation = computeAttackSituation(
            activeActor,
            assumedActiveActorLocation,
            target,
            (action as ActionWithAccuracy),
            equipment
        )
        val hitResult = attackSituation.sampleHit(world.random)
        // TODO inflict damage on entities in between, e.g., when shooting through a glass window

        val hitPosition3f =
            (hitResult as? HitResult.Hit)?.position3f ?: hitResult.position2f.append(target.centerPoint.z)
        ActorAim.executeFx(attackSituation, equipment, hitPosition3f)
        equipment.attackFx?.let { attackFx ->
            val assets = mutableListOf<UnrealRef<UObject>>()
            attackFx.collectAssetRefs(assets)
            if (assets.isNotEmpty()) {
                world.notifyViewEvent(PreloadAssetsEvent(assets))
            }
            attackFx.executeFx(attackSituation, equipment, hitPosition3f)
            delay(attackFx.estimateDuration(attackSituation, equipment, hitPosition3f))
        }
        dealWeaponEffects(equipment, hitResult)
        reportHit(activeActor, equipment, target, hitResult)
        handleEquipment(equipment)
    }
}

class AttackSituation(
    val activeActor: Actor,
    private val shootingPosition: Vec2i,
    val shootingPosition2f: Vec2f,
    val aimedPosition: Vec2f,
    private val angleDistribution: AngleDistribution,
    private val distanceDistribution: FloatDistribution
) {
    fun sampleHit(random: Random): HitResult {
        val angle = angleDistribution(random)
        val distance = distanceDistribution(random)
        return activeActor.world.queryRays(
            shootingPosition,
            BallisticExtractor,
            activeActor
        ).computeHit(angle, Float.POSITIVE_INFINITY, 0.5f, distance)
    }
}
