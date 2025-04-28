package com.cerebrallychallenged.hypogean.vanilla.attributes

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.radians

/**
 * Rotation along z axis.
 * Heading 0.0.radians corresponds to the direction (1, 0), i.e., north-west.
 */
var Actor.heading: Angle by attribute(0.0f.radians)

/**
 * Dialog to initiate when talking to that actor.
 */
var Actor.dialog: Dialog? by attribute(null)

var Actor.selectedDialogOptions: Set<Pair<Dialog.Select, Dialog.Node>> by attribute(setOf())

/**
 * Number of slots is needed for layout of slots in character view. Cf. GridUtil#layoutInventorySlots()
 */
var Actor.numberWeaponSlots: Int? by attribute(0)
