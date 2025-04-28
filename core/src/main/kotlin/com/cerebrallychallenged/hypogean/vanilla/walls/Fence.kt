@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.walls

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.activeActor
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.base.propSize
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.description
import com.cerebrallychallenged.hypogean.vanilla.attributes.dialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.cascade.showHealthChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.dialogs.GreatAIDialog3
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.visibilityBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.Images.PortraitFence
import com.cerebrallychallenged.hypogean.vanilla.refs.ResearchCenter
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec

object Asset_ResearchCenter_Fence5 : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ResearchCenter.SM_Fence_5)
        transform(translation = vec(148.0f, 0.0f, 0.0f), scale = Vec3f.ONE * 0.88f) {
            rotate(Vec3f.UNIT_Z, Angle.DEGREE_90)
        }
    }
})

open class FenceResearchCenter5Prop(initializer: Initializer) : Prop(initializer) {
    init {
        name = "Fence"
        description = "A sturdy iron fence blocking the way."
        asset = Asset_ResearchCenter_Fence5
        propSize = propSize(3)
        height = 2.3f
        health = 5
        showHealthChangesInDamageReport = true
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 1.0f }
        visibilityBlocking = BlockingValue { 0.2f }
    }
}

object Asset_ResearchCenter_Fence6 : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(ResearchCenter.SM_Fence_Door_1)
        transform(
            translation = vec(-50.0f, 0.0f, 0.0f),
            scale = (Vec3f.ONE * 0.75f).minus(vec(0.0f, 0.0f, 0.15f))
        )
    }
})

open class FenceResearchCenter6Prop(initializer: Initializer) : Prop(initializer) {
    init {
        name = "Fence"
        description = "A sturdy iron fence blocking the way."
        asset = Asset_ResearchCenter_Fence6
        icon = PortraitFence
        health = 5
        height = 2.0f
        showHealthChangesInDamageReport = true
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 0.8f }
        visibilityBlocking = BlockingValue { 0.2f }
    }

    override fun remove() {
        // GreatAI is not amused and we have a different dialogue
        world.activeActor?.dialog = GreatAIDialog3
        super.remove()
    }
}
