package com.cerebrallychallenged.hypogean.view.audio

import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.sound.UAudioComponent

abstract class AbstractAudioManager(audioComponent: UAudioComponent) : AudioComponentLike by audioComponent

object AudioManager : AbstractAudioManager(newObject<UAudioComponent>().apply {
    allowSpatialization = false
    registerComponent()
})
