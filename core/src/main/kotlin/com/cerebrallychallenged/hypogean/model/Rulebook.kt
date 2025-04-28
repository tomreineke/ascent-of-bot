@file:Suppress("UnstableApiUsage")
package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.modding.Feature
import com.cerebrallychallenged.hypogean.modding.Features
import com.cerebrallychallenged.hypogean.modding.Mod
import com.cerebrallychallenged.hypogean.modding.install
import com.cerebrallychallenged.hypogean.model.action.ActionModes
import com.cerebrallychallenged.hypogean.model.action.Actions
import com.cerebrallychallenged.hypogean.model.attribute.Attributes
import com.cerebrallychallenged.hypogean.rays.RayStencil
import com.cerebrallychallenged.hypogean.rays.readRayStencil
import com.cerebrallychallenged.hypogean.util.useLines
import com.cerebrallychallenged.jun.util.getResource
import java.io.DataInputStream
import java.util.zip.GZIPInputStream
import kotlin.random.Random
import kotlin.reflect.KClass

class Rulebook internal constructor(val features: Features): RulebookContext {
    companion object {
        private fun loadMods(resourceName: String): List<Mod> {
            val modIds = getResource(resourceName).useLines { lines ->
                lines.map { it.trim() }.filterNot { it.isBlank() || it.startsWith("#") }.toList()
            }
            return modIds.map {
                try {
                    Class.forName(it).kotlin.objectInstance as Mod
                } catch (e: Throwable) {
                    rulebookSpecificationError("""Cannot load mod "$it" listed by $resourceName""")
                }
            }
        }

        operator fun invoke(modListResourceName: String = "Rules/Mods.lst"): Rulebook =
                this(loadMods(modListResourceName))

        operator fun invoke(mods: List<Mod>): Rulebook = Rulebook(mods.install())
    }

    override val rulebook: Rulebook
        get() = this

    val rayStencil: RayStencil =
            getResource("Rules/rays.rays").openStream().use { baseStream ->
                val inputStream = if (RayStencil.COMPRESSED) GZIPInputStream(baseStream) else baseStream
                DataInputStream(inputStream).use { it.readRayStencil() }
            }

    fun createWorld(isPrimary: Boolean): World = World(WorldInitializer(this, Random.Default, isPrimary))

    val attributes = feature<Attributes>()

    val entityTypes = feature<EntityTypes>()

    val actions = feature<Actions>()

    val actionModes = feature<ActionModes>()

    val factions = feature<Factions>()
}

interface RulebookContext {
    val rulebook: Rulebook
}

inline fun <reified T : Entity> RulebookContext.entityTypeOf(): EntityType<T> = rulebook.entityTypes.get()

fun <T : Entity> RulebookContext.entityTypeOf(clazz: KClass<T>): EntityType<T> = rulebook.entityTypes[clazz]

inline fun <reified T : Feature> RulebookContext.feature(): T = rulebook.features.itemByClass()
