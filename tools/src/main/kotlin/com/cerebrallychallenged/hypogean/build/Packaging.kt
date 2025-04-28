package com.cerebrallychallenged.hypogean.build

import com.cerebrallychallenged.jun.xml.Xml
import com.cerebrallychallenged.jun.xml.readXml
import com.cerebrallychallenged.jun.xml.writeXml
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.copyTo
import kotlin.io.path.copyToRecursively
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.system.exitProcess

private const val UE_Version = "5.1"

private const val JVM = "jdk-21"

private const val ToolPath = """C:\Program Files\Epic Games\UE_$UE_Version\Engine\Build\BatchFiles\RunUAT.bat"""

private val ProjectPath = Path("Hypogean.uproject").toAbsolutePath()

private val StagedPath = Path("Saved", "StagedBuilds", "Windows", "Hypogean")

private val JvmPath = Path("Plugins", JVM)

fun main() {
    packaging(true)
    copyJars()
    createDistributableJVM()
    processSkia()
    copyNonUEContent()
}

private fun packaging(isIncremental: Boolean) {
    val args = listOfNotNull(
        ToolPath,
        "BuildCookRun",
        "-project=$ProjectPath",
        "-cook",
        "-iterativecooking".takeIf { isIncremental },
        "-build",
        "-stage",
        "-package",
        "-targetplatform=Win64",
        "-clientconfig=Shipping",
        "-logwindow",
    )
    val exitCode = ProcessBuilder(*args.toTypedArray()).inheritIO().start().waitFor()
    if (exitCode != 0) {
        exitProcess(exitCode)
    }
}

private fun copyJars() {
    val root = Path("Config", "jun.xml").readXml()
    val jarsPath = StagedPath.resolve("Jars")
    jarsPath.createDirectories()
    val output = Xml.create {
        "jun"(
            "applicationFactoryClass" to root["applicationFactoryClass"]!!,
            "jvmDllPath" to """JVM\bin\server\jvm.dll"""
        ) {
            "classpath" {
                val classpath = root.childTags.first { it.name == "classpath" }
                "entry"("time" to "BeginPlay", "path" to "Content")
                "entry"("time" to "BeginPlay", "path" to "NonUEContent")
                fun process(jarPath: Path, time: String) {
                    val name = jarPath.name
                    jarPath.copyTo(jarsPath.resolve(name), overwrite = true)
                    "entry"("time" to time, "path" to "Jars/$name")
                }
                process(Path("core", "build", "libs", "core-0.1.jar"), "BeginPlay")
                for (tag in classpath.childTags) {
                    val path = tag["path"]!!
                    if (path.endsWith(".jar")) {
                        process(Path(path), "boot")
                    }
                }
            }
        }
    }
    StagedPath.resolve("Config/jun.xml").writeXml(output)
}

@OptIn(ExperimentalPathApi::class)
private fun createDistributableJVM() {
    val jlinkPath = JvmPath.resolve("bin/jlink")
    val outputPath = StagedPath.resolve("JVM")
    if (outputPath.exists()) {
        outputPath.deleteRecursively()
    }
    val modules = listOf(
        "java.base",
        "java.compiler",
        "java.datatransfer",
        "java.desktop",
        "java.instrument",
        "java.logging",
        "java.prefs",
        "java.xml",
        "jdk.compiler",
//            "jdk.incubator.vector",
        "jdk.jdi",
        "jdk.jstatd",
        "jdk.net",
        "jdk.unsupported",
        "jdk.unsupported.desktop",
        "jdk.zipfs",
        "jdk.jdwp.agent"
    )
    val exitCode = ProcessBuilder(
        jlinkPath.absolutePathString(),
        "--no-man-pages",
        "--strip-native-commands",
        "--module-path",
        JvmPath.resolve("jmods").absolutePathString(),
        "--add-modules",
        modules.joinToString(separator = ","),
        "--output",
        outputPath.absolutePathString()
    ).inheritIO().start().waitFor()
    if (exitCode != 0) {
        exitProcess(exitCode)
    }
}

private fun processSkia() {
    val skiaTree = Path("Plugins", "JunPluginSkiaTree", "skia-tree")
    val exitCode = ProcessBuilder(
        "cargo",
        "build",
        "--release"
    ).directory(skiaTree.toFile()).inheritIO().start().waitFor()
    if (exitCode != 0) {
        exitProcess(exitCode)
    }
    val dllName = "skiatree.dll"
    skiaTree.resolve("target/release/$dllName")
        .copyTo(StagedPath.resolve("Binaries/Win64/$dllName"), overwrite = true)
}

@OptIn(ExperimentalPathApi::class)
private fun copyNonUEContent() {
    Path("NonUEContent")
        .copyToRecursively(StagedPath.resolve("NonUEContent"), followLinks = false, overwrite = true)
}
