package com.cerebrallychallenged.hypogean.model

const val MAGIC_UNSCHEDULED = -1

interface IniHolder : Entity {
    val isIniScheduled: Boolean
        get() = scheduledIniTime != MAGIC_UNSCHEDULED

    /**
     * The ini time when this is scheduled in the ini queue or {@link #MAGIC_UNSCHEDULED} if it is not.
     */
    val scheduledIniTime: Int

    /**
     * Must only be called from IniQueue!
     */
    fun resetIniScheduled() {
        setIniScheduled(MAGIC_UNSCHEDULED)
    }

    /**
     * Must only be called from IniQueue!
     */
    fun setIniScheduled(iniTime: Int)

    fun World.enqueueRelative(iniDelta: Int) {
        iniQueue.enqueueRelative(iniDelta, this@IniHolder)
    }
}
