package com.cerebrallychallenged.hypogean.view.audio

import com.cerebrallychallenged.jun.unreal.sound.EAudioFaderCurve
import com.cerebrallychallenged.jun.unreal.sound.USoundBase

interface AudioComponentLike {
    fun fadeOut(fadeOutDuration: Float, fadeVolumeLevel: Float, fadeCurve: EAudioFaderCurve)

    var pitchMultiplier: Float

    fun play(startTime: Float = 0.0f)

    var sound: USoundBase?

    fun stop()

    var volumeMultiplier: Float
}
