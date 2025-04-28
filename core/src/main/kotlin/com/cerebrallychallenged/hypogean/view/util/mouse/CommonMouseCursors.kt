package com.cerebrallychallenged.hypogean.view.util.mouse

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.vec

private val InfoCursor = MouseCursor("fantasy_rpg_cursors_silver/eye001", Vec2f.ZERO)

val MouseCursor.Companion.Info: MouseCursor
    get() = InfoCursor

private val InventoryCursor = MouseCursor("fantasy_rpg_cursors_silver/chest002", Vec2f.ZERO)

val MouseCursor.Companion.Inventory: MouseCursor
    get() = InventoryCursor

private val TalkCursor = MouseCursor("fantasy_rpg_cursors_silver/talk002_small", vec(0.8125f, 0.84375f))

val MouseCursor.Companion.Talk: MouseCursor
    get() = TalkCursor

private val MoveCursor = MouseCursor("fantasy_rpg_cursors_silver/walk001_small", Vec2f.ONE_HALF)

val MouseCursor.Companion.Move: MouseCursor
    get() = MoveCursor

private val PickUpCursor = MouseCursor("fantasy_rpg_cursors_silver/hand001_small", Vec2f.ONE_HALF)

val MouseCursor.Companion.PickUp: MouseCursor
    get() = PickUpCursor

private val ShootCursor = MouseCursor("fantasy_rpg_cursors_silver/pointer013_small", Vec2f.ONE_HALF)

val MouseCursor.Companion.Shoot: MouseCursor
    get() = ShootCursor

private val UseCursor = MouseCursor("fantasy_rpg_cursors_silver/gears002_small", Vec2f.ONE_HALF)

val MouseCursor.Companion.Use: MouseCursor
    get() = UseCursor

private val DigCursor = MouseCursor("fantasy_rpg_cursors_silver/pickaxe002_small", Vec2f.ONE_HALF)

val MouseCursor.Companion.Dig: MouseCursor
    get() = DigCursor

private val MeleeCursor = MouseCursor("fantasy_rpg_cursors_silver/swords002_small", Vec2f.ONE_HALF)

val MouseCursor.Companion.Melee: MouseCursor
    get() = MeleeCursor

private val PushCursor = MouseCursor("fantasy_rpg_cursors_silver/kick002_small", Vec2f.ONE_HALF)

val MouseCursor.Companion.Push: MouseCursor
    get() = PushCursor
