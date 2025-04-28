package com.cerebrallychallenged.hypogean.util

import com.cerebrallychallenged.hypogean.model.ChangeScheduleDto
import com.cerebrallychallenged.hypogean.model.ModelException
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldChangeDto
import com.cerebrallychallenged.hypogean.model.WorldFactory
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.stream.readByteArray
import com.cerebrallychallenged.jun.stream.writeByteArray
import com.cerebrallychallenged.jun.unreal.FPaths
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries

private val SaveGameNameRegex = """[\w-]+""".toRegex()

val SaveGameDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")

fun gameSavePath(saveGameName: String): Path {
    require(SaveGameNameRegex.matches(saveGameName))
    Files.createDirectories(Path(FPaths.projectSavedDir, "SaveGames"))
    return Path(FPaths.projectSavedDir, "SaveGames", "$saveGameName.aobsave")
}

fun saveGamesList(): List<Path> = Path(FPaths.projectSavedDir, "SaveGames").listDirectoryEntries("*.aobsave")

internal fun saveWorld(world: World, gameSavePath: Path) {
    val bytes = world.kryo.serializeToByteArray(world.createInitialWorldChanges())
    // No coroutine for saving but a synchronous operation, because
    // if the user clicks "Exit" during the save operation, the save file is corrupt.
    FileOutputStream(gameSavePath.toFile()).use { fileStream ->
        DataOutputStream(fileStream).writeByteArray(bytes)
    }
}

internal fun loadWorld(world: World, gameSavePath: Path): ChangeScheduleDto {
    val bytes = FileInputStream(gameSavePath.toFile()).use { fileStream ->
        DataInputStream(fileStream).readByteArray()
    }
    return world.kryo.deserializeFromByteArray(bytes)
}

class GameLoaderFactory(val gameSavePath: String) : WorldFactory {
    override fun World.setup() {
        val changeScheduleDto = loadWorld(this, Path(gameSavePath))
        for (change in changeScheduleDto.lists.asSequence().flatMap { it.second }) {
            try {
                val worldChange = kryo.deserializeFromByteArray<WorldChangeDto>(change)
                worldChange.applyChange()
            } catch (e: Exception) {
                // World changes like WorldChange$ReconChanged and WorldChange$AttributeChanged
                // seem to not be saved correctly. When loading a save game, we'll just ignore
                // these issues in order to avoid a deep dive.
                log.warn { "Loading issue: Applying a change in Word.setup() fails with message: ${e.message}" }
            }
        }
    }
}
