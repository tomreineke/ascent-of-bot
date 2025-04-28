package com.cerebrallychallenged.jun.build

import com.cerebrallychallenged.jun.xml.Xml
import com.cerebrallychallenged.jun.xml.readXml
import com.cerebrallychallenged.jun.xml.writeXml
import java.io.IOException
import java.nio.file.Files.isRegularFile
import java.nio.file.Files.newDirectoryStream
import java.nio.file.Path

fun addPluginProjectFiles(
    unrealProjectName: String,
    unrealProjectPath: Path,
    junProjectPath: Path,
) {
    addPluginProjectFiles(
        unrealProjectName,
        unrealProjectPath,
        junProjectPath,
        "JunPlugin",
        "IJunPlugin.h",
        null to "{b3262ce6-f9d0-4be7-99c3-479293e00089}",
        "Private" to "{bfc05258-362f-414b-9735-42c5e7f6ad8c}",
        "Public" to "{06527797-466b-4bde-b056-d02edf577d65}",
        "Classes" to "{02c120c8-9a27-4f73-90a0-ef7d8e75e104}"
    )
    addPluginProjectFiles(
        unrealProjectName,
        unrealProjectPath,
        junProjectPath,
        "JunPluginRMC",
        "JunPluginRMC.h",
        null to "{56f802be-7fb6-4862-a05f-51f75fa7cb11}",
        "Private" to "{20ac2b39-eccc-4e18-a5c8-9c366e5e82dd}",
        "Public" to "{4ceaa877-e916-4888-825b-445fd9da65dc}"
    )
    addPluginProjectFiles(
        unrealProjectName,
        unrealProjectPath,
        junProjectPath,
        "JunPluginCef",
        "JunPluginCef.h",
        null to "{daedf5ec-83f2-4076-b86e-99000b0378ee}",
        "Private" to "{50986ba5-6ad7-4967-93bc-dc7e0ece0152}",
        "Public" to "{41fb6d46-3bae-4cd7-82d4-b8f2c61a0cd9}"
    )
    addPluginProjectFiles(
        unrealProjectName,
        unrealProjectPath,
        junProjectPath,
        "JunPluginSkiaTree",
        "JunPluginSkiaTree.h",
        null to "{bee2494f-308a-4abd-9ba4-a9ea613a1224}",
        "Private" to "{972198f9-6ac7-4220-afe1-bc3595199147}",
        "Public" to "{b24a6c89-5af7-4162-95e0-fc9031d96c55}"
    )
}

private fun addPluginProjectFiles(
    unrealProjectName: String,
    unrealProjectPath: Path,
    junProjectPath: Path,
    pluginName: String,
    primaryHeaderName: String,
    vararg filters: Pair<String?, String>,
    sourceSubPath: String = "$pluginName/Source"
) {
    val pluginSourcePath = junProjectPath.resolve(sourceSubPath)
    val uProjectName = "$unrealProjectName.uproject"
    val projectFilesPath = unrealProjectPath.resolve("Intermediate/ProjectFiles")
    val vcProjectPath = projectFilesPath.resolve("$unrealProjectName.vcxproj")
    val vcProjectFiltersPath = projectFilesPath.resolve("$unrealProjectName.vcxproj.filters")
    val projectXml = vcProjectPath.readXml()
    val filterXml = vcProjectFiltersPath.readXml()

    var anyChange = false

    fun findOrCreateItemGroup(displayName: String, predicate: (Xml.Tag) -> Boolean): Xml.Tag {
        return filterXml.childTags.find(predicate)?.also {
            println("Found $displayName")
        } ?: run {
            println("Creating $displayName")
            anyChange = true
            Xml.Tag("ItemGroup").also { filterXml.add(it) }
        }
    }

    val dirItemGroup = findOrCreateItemGroup("ItemGroup for $pluginName") { itemGroup ->
        itemGroup.childTags.any {
            it.name == "Filter" && it["Include"] == pluginName
        }
    }
    val compileItemGroup = findOrCreateItemGroup("ItemGroup for source files") { itemGroup ->
        itemGroup.childTags.any {
            it.name == "ClCompile" && (it["Include"]?.endsWith("$pluginName.cpp") ?: false)
        }
    }
    val includeItemGroup = findOrCreateItemGroup("ItemGroup for header files") { itemGroup ->
        itemGroup.childTags.any {
            it.name == "ClInclude" && (it["Include"]?.endsWith(primaryHeaderName) ?: false)
        }
    }
    val projectGroupFilter: (Xml.Tag) -> Boolean = { itemGroup ->
        itemGroup.name == "ItemGroup" && itemGroup.childTags.any {
            it.name == "None" && (it["Include"]?.endsWith(uProjectName) ?: false)
        }
    }
    val projectItemGroup = findOrCreateItemGroup("ItemGroup for build files", projectGroupFilter)
    val projectGroup = projectXml.childTags.firstOrNull(projectGroupFilter)
            ?: throw IOException("$vcProjectPath does not have a project ItemGroup")

    for ((sub, uuid) in filters) {
        val filterPath = sub?.let { """$pluginName\$it""" } ?: pluginName
        if (dirItemGroup.childTags.firstOrNull { it.name == "Filter" && (it["Include"] == filterPath) } != null) {
            println("Found Filter $filterPath")
        } else {
            println("Creating Filter $filterPath")
            anyChange = true
            dirItemGroup.add(Xml.create {
                "Filter"("Include" to filterPath) {
                    "UniqueIdentifier" {
                        -uuid
                    }
                }
            })
        }
        val subPath = sub?.let { pluginSourcePath.resolve(it) } ?: pluginSourcePath
        newDirectoryStream(subPath) { isRegularFile(it) }.use { files ->
            for (file in files) {
                fun addEntry(parentGroup: Xml.Tag, tagName: String) {
                    val relativePath = projectFilesPath.relativize(file).toString()
                    if (parentGroup.childTags.any { it["Include"] == relativePath }) {
                        println("Found $tagName for $relativePath")
                    } else {
                        println("Creating $tagName for $relativePath")
                        anyChange = true
                        parentGroup.add(Xml.create {
                            tagName("Include" to relativePath) {
                                "Filter" {
                                    -filterPath
                                }
                            }
                        })
                        projectGroup.add(Xml.create { tagName("Include" to relativePath) })
                    }
                }

                when (file.toString().substringAfterLast('.')) {
                    "h", "hpp" -> addEntry(includeItemGroup, "ClInclude")
                    "cpp" -> addEntry(compileItemGroup, "ClCompile")
                    "cs" -> addEntry(projectItemGroup, "None")
                }
            }
        }
    }

    if (anyChange) {
        println("Saving changes...")
        vcProjectPath.writeXml(projectXml, indentation = "  ")
        vcProjectFiltersPath.writeXml(filterXml, indentation = "  ")
        println("Done.")
    } else {
        println("No new items found.")
    }
}
