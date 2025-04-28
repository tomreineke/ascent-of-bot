@file:Suppress("ClassName", "WrapUnaryOperator")

package com.cerebrallychallenged.hypogean.vanilla.props

import com.cerebrallychallenged.hypogean.model.*
import com.cerebrallychallenged.hypogean.model.base.ParticleSystem
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.base.propSize
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.blocks.BlockMaterials
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.movement.groundMovementBlocking
import com.cerebrallychallenged.hypogean.vanilla.rays.visibilityBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.Bunker.SM_pipe_staff_coupling_small
import com.cerebrallychallenged.hypogean.vanilla.refs.CyberpunkIndustries.Mesh_Fence02_FL
import com.cerebrallychallenged.hypogean.vanilla.refs.CyberpunkIndustries.Mesh_Pipe04
import com.cerebrallychallenged.hypogean.vanilla.refs.CyberpunkIndustries.Mesh_TrashBag01
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean.SM_ConveyorBelt
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_Barrel01
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_Box01
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_Box03
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_CartonBox03
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_CartonGarbage03
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_Pallet01
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_Rack01
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_Rack02
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_Tarp01
import com.cerebrallychallenged.hypogean.vanilla.refs.IndustryPropsPack6.SM_Tarp02
import com.cerebrallychallenged.hypogean.vanilla.refs.ResearchCenter.SM_Box_6
import com.cerebrallychallenged.hypogean.vanilla.refs.ResearchCenter.SM_Box_7
import com.cerebrallychallenged.hypogean.vanilla.refs.ResearchCenter.SM_Fan_1
import com.cerebrallychallenged.hypogean.vanilla.refs.ResearchCenter.SM_Light_3
import com.cerebrallychallenged.hypogean.vanilla.refs.SciFiDoor.sci_fi_door
import com.cerebrallychallenged.hypogean.vanilla.refs.StarterContent.P_Sparks
import com.cerebrallychallenged.hypogean.vanilla.triggers.GreatAiApprovalTrigger
import com.cerebrallychallenged.jun.asset.*
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.degrees
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.light.lumen
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

/*
 * Classes in this file can have names that don't follow the standard naming conventions for better
 * readability.
 * Examples and how to interpret them:
 * <ul>
 *     <li>Asset_Rack_3x1x1_20_50_80 : means that there is an rack that stretches 3 tiles in x-, 1 tile in y-
 *     and 1 tile in z-direction. Also the boards of the rack are placed at 20, 50 and 80 centimetres above the
 *     ground.</li>
 *     <li>Asset_UnitRack_20_50_80 : means that the rack stretches 1 tile in each direction (x, y, z).
 *     Also the boards of the rack are placed at 20, 50 and 80 centimetres above the ground.</li>
 * </ul>
 */

object Asset_Light : CompositeAsset({
    pointLightComponent {
        lightIntensity = 100.lumen
    }
})

open class Light(initializer: Initializer) : Prop(initializer) {
    init {
        name = "light"
        asset = Asset_Light
    }
}

object NullAsset : CompositeAsset({
    sceneComponent {
        link(Asset_Light) {
            transform(scale = vec(0.00001f, 0.00001f, 0.00001f), translation = vec(0.0f, 0.0f, 0.0f))
        }
    }
})

object Asset_NeonLight : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Light_3)
        transform(translation = vec(-45.0f, 0.0f, 40.0f)) {
            rotate(Vec3f.UNIT_Z, -90.degrees)
        }
        spotLightComponent {
            lightIntensity = 100.lumen
            transform(translation = vec(0.0f, -20.0f, 50.0f)) {
                rotate(Vec3f.UNIT_Y, 30.degrees)
                rotate(Vec3f.UNIT_Z, 90.degrees)
            }
        }
    }
})

open class NeonLight(initializer: Initializer) : Prop(initializer) {
    init {
        name = "neon light"
        asset = Asset_NeonLight
    }
}

object Asset_Barrel01 : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Barrel01)
    }
})

open class Barrel01(initializer: Initializer) : Prop(initializer) {
    init {
        name = "barrel"
        asset = Asset_Barrel01
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 0.2f }
        height = 0.5f
    }
}

object Asset_CartonBox03 : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_CartonBox03)
    }
})

open class CartonBox03(initializer: Initializer) : Prop(initializer) {
    init {
        name = "box"
        asset = Asset_CartonBox03
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 0.1f }
        height = 0.3f
    }
}

object Asset_Pallet : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Pallet01)
        transform(scale = vec(0.8f, 0.8f, 1.0f))
    }
})

open class Pallet(initializer: Initializer) : Prop(initializer) {
    init {
        name = "pallet"
        asset = Asset_Pallet
        propSize = propSize(2, 2)
        groundMovementBlocking = BlockingValue { 0.1f }
        ballisticBlocking = BlockingValue { 0.1f }
        height = 0.2f
    }
}

object Asset_ChargingPlatform : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Box_6)
        transform(scale = vec(3.8f, 2.0f, 4.8f), translation = vec(-50.0f, 40.0f, 00.0f)) {
            rotate(Vec3f.UNIT_X, 90.degrees)
        }
    }
})

object Asset_UnitPillars : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Rack01)
        transform(scale = vec(0.92f, 0.25f, 0.25f))
    }
})

object Asset_UnitBoard : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Rack02)
        transform(scale = vec(1.0f, 0.25f, 0.25f))
    }
})

object Asset_UnitRack_20_50_80 : CompositeAsset({
    sceneComponent {
        link(Asset_UnitPillars) {
            transform(scale = vec(1.0f, 1.0f, 0.8f))
        }
        link(Asset_UnitBoard) {
            transform(translation = vec(0.0f, 0.0f, 20.0f))
        }
        link(Asset_UnitBoard) {
            transform(translation = vec(0.0f, 0.0f, 50.0f))
        }
        link(Asset_UnitBoard) {
            transform(translation = vec(0.0f, 0.0f, 80.0f))
        }
    }
})

object Asset_Rack_3x1x1_20_50_80 : CompositeAsset({
    sceneComponent {
        link(Asset_UnitRack_20_50_80) {
            transform(scale = vec(0.92f, 2.8f, 1.0f)) {
                rotate(Vec3f.UNIT_Z, -90.degrees)
            }
        }
    }
})

object Asset_UnitBunkBed_20_80 : CompositeAsset({
    sceneComponent {
        link(Asset_UnitPillars) {
            transform(scale = vec(1.0f, 1.0f, 0.8f))
        }
        link(Asset_UnitBoard) {
            transform(translation = vec(0.0f, 0.0f, 20.0f))
        }
        link(Asset_UnitBoard) {
            transform(translation = vec(0.0f, 0.0f, 80.0f))
        }
    }
})

object Asset_UnitBunkBedDropped_20_80 : CompositeAsset({
    sceneComponent {
        link(Asset_UnitBunkBed_20_80) {
            transform(scale = vec(0.92f, 2.0f, 1.0f), translation = vec(0.0f, 0.0f, 50.0f)) {
                rotate(Vec3f.UNIT_Y, -90.degrees)
            }
        }
    }
})

open class RackPillars(initializer: Initializer) : Prop(initializer) {
    init {
        name = "rack pillars"
        asset = Asset_UnitPillars
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 0.3f }
        height = 1.2f
    }
}

open class RackBoard(initializer: Initializer) : Prop(initializer) {
    init {
        name = "rack board"
        asset = Asset_UnitBoard
        groundMovementBlocking = BlockingValue { 0.5f }
        ballisticBlocking = BlockingValue { 0.1f }
        height = 0.1f
    }
}

open class Rack(initializer: Initializer) : Prop(initializer) {
    init {
        name = "base rack"
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 0.5f }
    }
}

open class Rack_3x1x1_20_50_80(initializer: Initializer) : Rack(initializer) {
    init {
        name = "three-storey rack"
        asset = Asset_Rack_3x1x1_20_50_80
        propSize = propSize(3, 1)
        height = 1.0f
    }
}

open class UnitBunkBedDropped_20_80(initializer: Initializer) : Rack(initializer) {
    init {
        name = "double bunk bed dropped"
        asset = Asset_UnitBunkBedDropped_20_80
        height = 1.0f
    }
}

object Asset_UnitConveyorBelt : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_ConveyorBelt)
        transform(scale = vec(1.05f, 0.7f, 1.0f))
    }
})

open class ConveyorBelt(initializer: Initializer) : Prop(initializer) {
    init {
        name = "conveyor belt"
        asset = Asset_UnitConveyorBelt
    }
}

object Asset_UnitCoveredBox : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Box03)
        transform(scale = vec(1.0f, 1.0f, 0.5f), translation = vec(-50.0f, 0.0f, 0.0f))
    }
})

open class CoveredBox(initializer: Initializer) : Prop(initializer) {
    init {
        name = "covered box"
        asset = Asset_UnitCoveredBox
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 0.5f }
        height = 0.5f
    }
}

object Asset_UnitTerminal : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Box_7)
        transform(scale = vec(2.0f, 1.0f, 1.0f), translation = vec(-50.0f, -40.0f, 15.0f))
    }
})

open class Terminal(initializer: Initializer) : Prop(initializer) {
    init {
        name = "terminal"
        asset = Asset_UnitTerminal
        height = 1.0f
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 1.0f }
        height = 1.0f
    }
}

class FirstBossTerminal(initializer: Initializer) : Terminal(initializer)

object Asset_UnitCartonage : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_CartonGarbage03)
    }
})

open class Cartonage3(initializer: Initializer) : Prop(initializer) {
    init {
        name = "cartonage"
        asset = Asset_UnitCartonage
    }
}

object Asset_UnitBlanket1 : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Tarp01)
        transform(scale = vec(0.3f, 0.3f, 1.0f))
    }
})

open class Blanket1(initializer: Initializer) : Prop(initializer) {
    init {
        name = "blanket"
        asset = Asset_UnitBlanket1
    }
}

object Asset_UnitBlanket2 : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Tarp02)
        transform(scale = vec(0.5f, 0.5f, 1.0f))
    }
})

open class Blanket2(initializer: Initializer) : Prop(initializer) {
    init {
        name = "blanket"
        asset = Asset_UnitBlanket2
    }
}

object Asset_UnitWoodenBox : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Box01)
        transform(scale = vec(0.5f, 0.4f, 0.5f))
    }
})

open class WoodenBox(initializer: Initializer) : Prop(initializer) {
    init {
        name = "wooden box"
        asset = Asset_UnitWoodenBox
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 0.5f }
        height = 1.0f
    }
}

object Asset_BigDoor : CompositeAsset({
    staticMeshComponent {
        transform(translation = vec(-5.0f, 0.0f, 10.0f), scale = vec(0.4f, 1.4f, 0.25f)) {
            rotate(Vec3f.UNIT_Z, -90.degrees)
        }
        staticMesh = load(sci_fi_door)
    }
})

object Asset_HiddenBigDoor : CompositeAsset({
    staticMeshComponent {
        transform(translation = vec(0.0f, 0.0f, 10.0f), scale = vec(0.4f, 0.5f, 0.25f)) {
            rotate(Vec3f.UNIT_Z, -90.degrees)
        }
        staticMesh = load(sci_fi_door)
    }
})

open class Asset_Security_Door_DirectionX(
        materialRef: UnrealRef<UMaterialInterface>,
        freeBlocks: Int,
        topBlocks: Int
) : CompositeAsset({
    staticMeshComponent {
        link(Asset_BigDoor)
        if (topBlocks > 0) {
            link(Asset_BaseBlocks(materialRef, topBlocks)) {
                transform(translation = vec(0.0f, 0.0f, 100.0f * (freeBlocks + 1)))
            }
        }
    }
})

open class Asset_Hidden_Door_DirectionX(
    materialRef: UnrealRef<UMaterialInterface>,
    freeBlocks: Int,
    topBlocks: Int
) : CompositeAsset({
    staticMeshComponent {
        link(Asset_HiddenBigDoor)
        if (topBlocks > 0) {
            link(Asset_BaseBlocks(materialRef, topBlocks)) {
                transform(translation = vec(0.0f, 0.0f, 100.0f * (freeBlocks + 1)))
            }
        }
    }
})

object Asset_Hidden_Door_Cave_DirectionX : Asset_Hidden_Door_DirectionX(BlockMaterials.MI_CaveWall, 0, 1)

object Asset_Hidden_Door_Cave_DirectionY : Asset_Rotated90(Asset_Hidden_Door_Cave_DirectionX)

object Asset_Security_Door_Cave_DirectionX : Asset_Security_Door_DirectionX(BlockMaterials.MI_CaveWall, 0, 1)

object Asset_Security_Door_Cave_DirectionY : Asset_Rotated90(Asset_Security_Door_Cave_DirectionX)

open class HiddenDoor(initializer: Initializer) : Prop(initializer) {
    init {
        name = "hidden door"
        asset = Asset_Hidden_Door_Cave_DirectionX
        cellFilling = true
        height = 1.0f
        groundMovementBlocking = BlockingValue { 2.0f }
        ballisticBlocking = BlockingValue { 2.0f }
        visibilityBlocking = BlockingValue { 1.0f }
    }
}

open class BigDoor(initializer: Initializer) : Prop(initializer) {
    init {
        name = "security door"
        asset = Asset_Security_Door_Cave_DirectionX
        cellFilling = true
        height = 1.0f
        groundMovementBlocking = BlockingValue { 2.0f }
        ballisticBlocking = BlockingValue { 2.0f }
        visibilityBlocking = BlockingValue { 1.0f }
    }
}

object Asset_RectangularVent : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_Fan_1)
        transform(scale = vec(0.5f, 0.5f, 0.5f), translation = vec(-50.0f, 30.0f, 50.0f)) {
            rotate(Vec3f.UNIT_X, 90.degrees)
            rotate(Vec3f.UNIT_Z, 90.degrees)
        }
    }
})

open class RectangularVent(initializer: Initializer) : Prop(initializer) {
    init {
        name = "vent"
        asset = Asset_RectangularVent
        groundMovementBlocking = BlockingValue { 0.5f }
        ballisticBlocking = BlockingValue { 0.2f }
        height = 0.5f
    }
}

object Asset_Sparks : CompositeAsset({
    particleSystemComponent {
        template = load(P_Sparks)
    }
})

open class Sparks(initializer: Initializer) : ParticleSystem(initializer) {
    init {
        name = "sparks"
        asset = Asset_Sparks
    }
}

object Asset_LandMine : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_pipe_staff_coupling_small)
        transform(scale = vec(3.0f, 3.0f, 3.0f)) {
            rotate(Vec3f.UNIT_X, -90.degrees)
        }
    }
})

object Asset_Bag : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Mesh_TrashBag01)
        transform(scale = vec(0.8f, 0.8f, 1.0f))
    }
})

object Asset_Cyberpunk_Fence : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Mesh_Fence02_FL)
        transform(scale = vec(0.8f, 0.8f, 1.0f))
    }
})

object Asset_Cyberpunk_Pipe : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(Mesh_Pipe04)
        transform(scale = vec(0.2f, 0.2f, 0.2f))
    }
})

open class Bag(initializer: Initializer) : Prop(initializer) {
    init {
        name = "bag"
        asset = Asset_Bag
        propSize = propSize(1, 1)
        health = 20
        groundMovementBlocking = BlockingValue { 0.1f }
        ballisticBlocking = BlockingValue { 0.2f }
        height = 0.5f
        pickupAble = true
        icon = Images.Bag
        initializer.addStatusEffect<GreatAiApprovalTrigger>()
    }
}

open class Pipe(initializer: Initializer) : Prop(initializer) {
    init {
        name = "pipe"
        asset = Asset_Cyberpunk_Pipe
        propSize = propSize(1, 1)
        health = 100
        groundMovementBlocking = BlockingValue { 1.0f }
        ballisticBlocking = BlockingValue { 1.0f }
        height = 0.5f
        icon = Images.Bag
    }
}


