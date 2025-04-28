package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.maxHealth

/**
 * Subclasses of `Event` are recurring or one-shot events present in the ini bar.
 * One-shot events return [InitiativeCost.Destroy] from [execute].
 */
abstract class Event(initializer: Initializer) : NonWorldEntity(initializer), IniHolder, ItemOrOrEventOrTransient {

    private var _scheduledInTime: Int = MAGIC_UNSCHEDULED

    override val scheduledIniTime: Int
        get() = _scheduledInTime

    init {
        health = 0
        maxHealth = 1
    }

    override fun setIniScheduled(iniTime: Int) {
        _scheduledInTime = iniTime
    }

    context(CascadeContext)
    abstract suspend fun execute(): InitiativeCost

    /**
     * Event becomes active and visible in the [com.cerebrallychallenged.hypogean.view.inibar.IniBarView].
     *
     * @see Entity.isAlive()
     */
    fun activate() {
        health = 1
    }
}
