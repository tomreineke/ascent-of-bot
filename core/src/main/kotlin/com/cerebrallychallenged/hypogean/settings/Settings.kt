package com.cerebrallychallenged.hypogean.settings

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Rulebook
import com.cerebrallychallenged.hypogean.model.feature
import com.cerebrallychallenged.jun.unreal.FPaths
import com.cerebrallychallenged.jun.xml.Xml
import com.cerebrallychallenged.jun.xml.findOrAddChild
import com.cerebrallychallenged.jun.xml.readXml
import java.nio.file.Files
import java.nio.file.Paths

suspend fun Rulebook.loadSettings(): Settings {
    val settingsPath = Paths.get(FPaths.projectConfigDir, "settings.xml")
    val xml = if (Files.exists(settingsPath)) {
        settingsPath.readXml()
    } else {
        Xml.create { "settings"() }
    }
    val settingsModuleKeys = feature<SettingsModuleKeys>()
    val modules = settingsModuleKeys.associateWith { key ->
        val xmlKey = settingsModuleKeys.idForItem(key)
        key.create(
                this,
                xml.findOrAddChild({ it["module"] == xmlKey }) { "setting"("module" to xmlKey) }
        )
    }
    return Settings(modules)
}

class Settings(private val modules: Map<SettingsModuleKey<*>, SettingsModule>) {
    @Suppress("UNCHECKED_CAST") // Safe as association given by T of SettingsModuleKey<T>.
    operator fun <T : SettingsModule> get(key: SettingsModuleKey<T>): T = modules.getValue(key) as T
}

abstract class SettingsModule

abstract class SettingsModuleKey<T : SettingsModule> {
    abstract fun create(rulebook: Rulebook, xml: Xml.Tag): T
}

class SettingsModuleKeys : SimpleObjectRegistry<SettingsModuleKey<*>>()