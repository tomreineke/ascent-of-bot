package com.cerebrallychallenged.hypogean.build

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.Path
import kotlin.io.path.writeLines

private val ProjectPaths = listOf(Path("core"))
private val UnrealRefPattern = """UnrealRef<\w+>\("[\w\./]+'([^']+)'"\)""".toRegex()

fun main() {
    val assetRefs = mutableListOf<String>()
    for (projectPath in ProjectPaths) {
        Files.walkFileTree(projectPath.resolve("src/main/kotlin"), object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                if (Files.isRegularFile(file) && file.toString().endsWith(".kt")) {
                    for (line in file.toFile().readLines()) {
                        val trimmed = line.trim()
                        if (!trimmed.startsWith("//")) {
                            for (match in UnrealRefPattern.findAll(trimmed)) {
                                assetRefs.add(match.groupValues[1])
                            }
                        }
                    }
                }
                return FileVisitResult.CONTINUE
            }
        })
    }
    for (assetRef in assetRefs) {
        println(assetRef)
    }
    Path("Content/Hypogean/AssetInclusion/KotlinAssets.lst").writeLines(assetRefs)
}
