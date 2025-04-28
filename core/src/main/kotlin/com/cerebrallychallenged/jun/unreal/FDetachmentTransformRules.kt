package com.cerebrallychallenged.jun.unreal

class FDetachmentTransformRules(
        private val locationRule: EDetachmentRule,
        private val rotationRule: EDetachmentRule,
        private val scaleRule: EDetachmentRule,
        private val callModify: Boolean
) {
    constructor(rule: EDetachmentRule, callModify: Boolean) : this(rule, rule, rule, callModify)

    constructor(attachmentRules: FAttachmentTransformRules, callModify: Boolean) : this(
            if (attachmentRules.locationRule == EAttachmentRule.KeepRelative)
                EDetachmentRule.KeepRelative
            else
                EDetachmentRule.KeepWorld,
            if (attachmentRules.rotationRule == EAttachmentRule.KeepRelative)
                EDetachmentRule.KeepRelative
            else
                EDetachmentRule.KeepWorld,
            if (attachmentRules.scaleRule == EAttachmentRule.KeepRelative)
                EDetachmentRule.KeepRelative
            else
                EDetachmentRule.KeepWorld,
            callModify
    )

    companion object {
        val KeepRelativeTransform = FDetachmentTransformRules(EDetachmentRule.KeepRelative, true)
        val KeepWorldTransform = FDetachmentTransformRules(EDetachmentRule.KeepWorld, true)
    }

    fun toJunMagic(): Int {
        return (locationRule.ordinal
                or (rotationRule.ordinal shl 2)
                or (scaleRule.ordinal shl 4)
                or if (callModify) 64 else 0)
    }
}
