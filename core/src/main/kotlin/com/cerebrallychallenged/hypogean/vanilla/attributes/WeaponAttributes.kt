package com.cerebrallychallenged.hypogean.vanilla.attributes

import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon

/**
 * If `true`, the range of that weapon can be intentionally limited.
 * For example, a rocket missing its target should not fly indefinitely, but explode at least next to it.
 * In contrast, the flight of a bullet cannot be limited, i.e., it cannot stop flying mid-air.
 */
var Weapon.hasAdjustableRange: Boolean by attribute(false)
