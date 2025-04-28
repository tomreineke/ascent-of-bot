pluginManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
        }
    }
}

include(
    "core",
//    "mod-processor",
    "tools",
)
