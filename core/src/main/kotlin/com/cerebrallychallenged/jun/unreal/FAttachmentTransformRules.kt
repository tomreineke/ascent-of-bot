package com.cerebrallychallenged.jun.unreal

class FAttachmentTransformRules(
        val locationRule: EAttachmentRule,
        val rotationRule: EAttachmentRule,
        val scaleRule: EAttachmentRule,
        private val weldSimulatedBodies: Boolean
) {
    constructor(rule: EAttachmentRule, weldSimulatedBodies: Boolean) : this(rule, rule, rule, weldSimulatedBodies)

    companion object {
        val KeepRelativeTransform = FAttachmentTransformRules(EAttachmentRule.KeepRelative, false)
        val KeepWorldTransform = FAttachmentTransformRules(EAttachmentRule.KeepWorld, false)
        val SnapToTargetNotIncludingScale = FAttachmentTransformRules(
                EAttachmentRule.SnapToTarget,
                EAttachmentRule.SnapToTarget,
                EAttachmentRule.KeepWorld,
                false
        )
        val SnapToTargetIncludingScale = FAttachmentTransformRules(EAttachmentRule.SnapToTarget, false)
    }

    fun toJunMagic(): Int {
        return (locationRule.ordinal
                or (rotationRule.ordinal shl 2)
                or (scaleRule.ordinal shl 4)
                or if (weldSimulatedBodies) 64 else 0)
    }
}
