package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.IniHolder
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock

/**
 * If `true`, this entity needs a positive [com.cerebrallychallenged.hypogean.vanilla.attributes.energy]
 * to be active. That especially applies to robots and other machines.
 * Is `false` for most biological actors. For example, human soldiers without energy cannot shoot their laser weapon,
 * but they can still walk.
 */
var IniHolder.needsEnergyForInitiative: Boolean by attribute(true)

context(CascadeBlock)
fun performEnergyBlackout(entity: Entity){
    if (isReal && entity is IniHolder && entity.needsEnergyForInitiative && entity.isIniScheduled) {
        world.iniQueue.remove(entity)
    }
}
