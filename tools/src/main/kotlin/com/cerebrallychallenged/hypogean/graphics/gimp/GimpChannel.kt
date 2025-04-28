package com.cerebrallychallenged.hypogean.graphics.gimp

class GimpChannel(gimp: Gimp) : GimpDrawable(gimp)

fun GimpImage.newChannelFromComponent(component: GimpComponent, name: GimpString): GimpChannel =
    GimpChannel(gimp).also { append("$it = pdb.gimp_channel_new_from_component($this, $component, $name)") }

fun GimpImage.newChannelFromComponent(component: GimpComponent, name: String): GimpChannel =
    newChannelFromComponent(component, name.g)