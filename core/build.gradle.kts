import java.io.FileWriter
import java.io.PrintWriter

plugins {
    kotlin("jvm")
//    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
}

group = "com.cerebrallychallenged.hypogean"
version = "0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

val kotlinVersion = "1.9.25"
val ktorVersion = "2.2.3"
val guavaVersion = "30.0-jre"

dependencies {
    api(kotlin("stdlib", kotlinVersion))
    api(kotlin("stdlib-jdk8", kotlinVersion))
    api(kotlin("reflect", kotlinVersion))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

//    implementation(project(":mod-processor"))
//    ksp(project(":mod-processor"))

    implementation("io.ktor:ktor-network:$ktorVersion")
    api("com.google.guava:guava:$guavaVersion")
    api("it.unimi.dsi:fastutil:8.5.2")
    api("org.jctools:jctools-core:2.1.1")
    api("org.apache.commons:commons-math3:3.6.1")
    api("org.locationtech.jts:jts-core:1.16.1")
    implementation("com.esotericsoftware:kryo:5.3.0")

    testImplementation("junit:junit:4.13")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.6.0")
    testImplementation("com.google.guava:guava-testlib:$guavaVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
}

tasks.register("createJunConfigXml") {
    doLast {
        PrintWriter(FileWriter("Config/jun.xml")).use { writer ->
            val applicationFactoryClass = "com.cerebrallychallenged.HypogeanApplicationFactory"
            val jvmDllPath = projectDir.parentFile.resolve("Plugins/jdk-21/bin/server/jvm.dll").absolutePath
            writer.println("""<jun applicationFactoryClass="$applicationFactoryClass" jvmDllPath="$jvmDllPath">""")
            writer.println("""  <classpath>""")
            fun entry(classpathTime: String, path: File) {
                writer.println("""    <entry time="$classpathTime" path="$path"/>""")
            }
            val projectDirChildren = listOf(
                "build/classes/kotlin/main",
                "build/classes/kotlin/jvm/main",
                "build/resources/main"
            )
            fun projectEntry(classpathTime: String, projectDir: File) {
                for (child in projectDirChildren) {
                    entry(classpathTime, projectDir.resolve(child))
                }
            }
            fun createClasspathEntries(artifact: ResolvedArtifact) {
                val componentIdentifier = artifact.id.componentIdentifier
                val artifactPath = artifact.file//.toPath()
                val isJun = (componentIdentifier as? ModuleComponentIdentifier)?.module?.startsWith("jun-") == true
                if (isJun || componentIdentifier is ProjectComponentIdentifier) {
                    projectEntry("BeginPlay", artifactPath.parentFile.parentFile.parentFile)
                } else {
                    entry("boot", artifactPath)
                }
            }
            fun artifactPriority(artifact: ResolvedArtifact): Int {
                val name = artifact.name
                return when {
                    name == "fastutil" -> 1
                    name.startsWith("kotlin-") -> 2
                    name.startsWith("kotlinx-") -> 3
                    else -> 10
                }
            }
            projectEntry("BeginPlay", project.projectDir)
            entry("BeginPlay", projectDir.parentFile.resolve("Content").absoluteFile)
            entry("BeginPlay", projectDir.parentFile.resolve("NonUEContent").absoluteFile)
            for (artifact in project.configurations["runtimeClasspath"].resolvedConfiguration.resolvedArtifacts.toList().sortedBy(::artifactPriority)) {
                createClasspathEntries(artifact)
            }
            writer.println("""  </classpath>""")
            writer.println("""</jun>""")
        }
    }
}
