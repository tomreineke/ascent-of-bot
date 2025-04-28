package com.cerebrallychallenged.hypogean.view.input

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry

abstract class InputCommand(val defaultKeyName: String? = null)

class InputCommands : SimpleObjectRegistry<InputCommand>()