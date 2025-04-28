package com.cerebrallychallenged.hypogean.vanilla.attributes

import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.view.map.AssetParameterBindings
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.math.geo.Transform3f

/**
 * The description may be some lore about the entity.
 * This can be displayed in an internal wiki or on an info panel.
 */
var Entity.description: String? by attribute(null)

/**
 * The flavor text can be displayed together with the description in a wiki or an info panel to add additional
 * immersion.
 */
var Entity.flavorText: String? by attribute(null)

/**
 * If true, the entity can display an [com.cerebrallychallenged.hypogean.view.info.InfoView] when clicked.
 */
var Entity.isInfoViewAvailable: Boolean by attribute(false)

/**
 * Icon representing that entity. This is used to display the actors in the initiative bar and
 * various views for example. Another example are weapon icons.
 */
var Entity.icon: ImageResource? by attribute(null)

/**
 * 3d asset representing that entity.
 */
var Item.asset: CompositeAsset? by attribute(null)

var StatusEffect.asset: CompositeAsset? by attribute(null)

var Entity.assetParameterBindings: AssetParameterBindings by attribute(AssetParameterBindings.Empty)

/**
 * Controls the position, size, and rotation of the entity visualization.
 * For actors, this applies only while there is an animation, e.g., while moving around.
 */
var Entity.transform : Transform3f? by attribute(null)

/**
 * By default the MapView reacts to changes of location and heading. However,
 * during the animation of a move, the location and heading are not determined
 * by the logical position of the moving actor, but by the EntityMoveEvent.
 * So this flag tells the MapView not to react to changes of location and heading
 * during this time.
 */
var Entity.ignoreLocationAndHeading: Boolean by attribute(false)

/**
 * If `true` and this entity has no recon of [Recon.Visible], it is still shown but desaturated.
 */
var Entity.visibleIfLostSight: Boolean by attribute(false)
