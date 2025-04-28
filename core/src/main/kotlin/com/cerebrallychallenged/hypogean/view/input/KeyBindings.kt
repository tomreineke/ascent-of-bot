package com.cerebrallychallenged.hypogean.view.input

import com.cerebrallychallenged.hypogean.model.Rulebook
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.hypogean.settings.SettingsModule
import com.cerebrallychallenged.hypogean.settings.SettingsModuleKey
import com.cerebrallychallenged.jun.xml.Xml
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import java.io.IOException
import com.cerebrallychallenged.jun.input.Key as InputKey

class KeyBindings(rulebook: Rulebook, private val xml: Xml.Tag) : SettingsModule() {
    object Key : SettingsModuleKey<KeyBindings>() {
        override fun create(rulebook: Rulebook, xml: Xml.Tag): KeyBindings = KeyBindings(rulebook, xml)
    }

    private val inputCommands = rulebook.feature<InputCommands>()

    private val bindings: Multimap<InputKey, InputCommand> = HashMultimap.create()

    private val reverseBindings: Multimap<InputCommand, InputKey> = HashMultimap.create()

    private fun addBinding(key: InputKey, inputCommand: InputCommand) {
        bindings.put(key, inputCommand)
        reverseBindings.put(inputCommand, key)
    }

    init {
        for (child in xml.childTags.filter { it.name == "binding" }) {
            val key = InputKey[child["key"] ?: throw IOException("Key binding misses key attribute")] ?: continue
            val inputCommand = inputCommands.itemForId(
                    child["command"] ?: throw IOException("Key binding misses command attribute")
            )
            addBinding(key, inputCommand)
        }
        for (inputCommand in inputCommands) {
            if (reverseBindings.get(inputCommand).isEmpty()) {
                val key = InputKey[inputCommand.defaultKeyName ?: continue] ?: continue
                addBinding(key, inputCommand)
            }
        }
    }

    operator fun get(key: InputKey): Collection<InputCommand> = bindings.get(key)
}
