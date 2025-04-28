package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.jun.input.InputEvent

data class ViewInputEvent(val inputEvent: InputEvent, val entity: Entity?)
