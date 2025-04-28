package com.cerebrallychallenged.hypogean.graphics.buttons

import com.cerebrallychallenged.hypogean.vanilla.actions.GrapplingAttackAction
import com.cerebrallychallenged.hypogean.vanilla.items.melee.GrapplingAttackDirectionBadge
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.view.actionbar.CrosshairBadge
import com.cerebrallychallenged.hypogean.view.actionbar.RankBadge
import com.cerebrallychallenged.hypogean.view.actionbar.TimeBadge
import com.cerebrallychallenged.jun.math.geo.vec

// The strange order of ranks is due to the naming of the badge icons by https://game-icons.net/badges.html
private val RankBadges = BadgeGroupStyle(RankBadge,1 to "private", 2 to "corporal", 3 to "sergeant")

private val TimeBadges = BadgeGroupStyle(TimeBadge, "time")

private val GrapplingAttackDirectionBadges = BadgeGroupStyle(
    GrapplingAttackDirectionBadge,
    GrapplingAttackAction.Direction.PULL_TO_ACTOR to "arrow-down",
    GrapplingAttackAction.Direction.PULL_TO_TARGET to "arrow-up"
)

private val CrosshairBadges = BadgeGroupStyle(CrosshairBadge, "crosshair")

/**
 * Before you run this method, make sure you started and closed the shipped Gimp installation under
 * [tools/GIMP 2/bin/gimp-2.10.exe] at least once.
 * Warnings from executing the python script can be ignored.
 */
fun main() {
    createButtons("action_buttons", 539, listOf(
        Button(ActionIcons.ArmorPunch, 300, badges = listOf(TimeBadges to vec(339, -5))),
        Button(ActionIcons.Autogun, 300, badges = listOf(RankBadges to vec(339, -5))),
        Button(ActionIcons.AutoRepair, 300),
        Button(ActionIcons.BolterGun, 300, badges = listOf(RankBadges to vec(340, 340))),
        Button(ActionIcons.CannonShot, 300, badges = listOf(RankBadges to vec(339, -5))),
        Button(ActionIcons.CircleClaws, 350),
        Button(ActionIcons.Circuitry, 400, shift = vec(-10, 10)),
        Button(ActionIcons.ClockwiseRotation, 350),
        Button(ActionIcons.Crosshair, 380),
        Button(ActionIcons.DoubleDragon, 280),
        Button(ActionIcons.EnergyShield, 330),
        Button(ActionIcons.FlameClaws, 300, shift = vec(20, 0)),
        Button(ActionIcons.Grapple, 300, shift = vec(20, 0)),
        Button(ActionIcons.HarpoonChain, 280, badges = listOf(
            GrapplingAttackDirectionBadges to vec(330, -15)
        )),
        Button(ActionIcons.Joystick, 300),
        Button(ActionIcons.LaserBlast, 260, badges = listOf(RankBadges to vec(340, -5))),
        Button(ActionIcons.MachineGun, 280, badges = listOf(RankBadges to vec(340, -5))),
        Button(ActionIcons.Minigun, 280, badges = listOf(RankBadges to vec(340, -5))),
        Button(ActionIcons.NextButton, 280),
        Button(ActionIcons.PickUp, 300),
        Button(ActionIcons.RayGun, 300, badges = listOf(RankBadges to vec(339, 339))),
        Button(ActionIcons.Rocket, 280, badges = listOf(
            RankBadges to vec(340, -5),
            CrosshairBadges to vec(5, 335)
        )),
        Button(ActionIcons.SinusoidalBeam, 300, badges = listOf(RankBadges to vec(340, -5))),
        Button(ActionIcons.Spanner, 280, shift = vec(25, 0)),
        Button(ActionIcons.Talk, 280),
        Button(ActionIcons.TankTread, 350, shift = vec(-60, 0)),
        Button(ActionIcons.Walk, 380),
        Button(ActionIcons.WolverineClaws, 300)
    )) { it == "hand" } // Comment in to run Gimp only for the specified condition.
}
