plugins {
    kotlin("jvm")
}

group = "com.cerebrallychallenged.hypogean"
version = "0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    api(project(":core"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.4.3")

    api("com.sksamuel.scrimage:scrimage-core:4.0.12")
    api("com.sksamuel.scrimage:scrimage-filters:4.0.12")

    val jfxVersion = "14.0.1"
    api("org.openjfx:javafx-base:$jfxVersion")
    api("org.openjfx:javafx-base:$jfxVersion:linux")
    api("org.openjfx:javafx-base:$jfxVersion:mac")
    api("org.openjfx:javafx-base:$jfxVersion:win")
    api("org.openjfx:javafx-controls:$jfxVersion")
    api("org.openjfx:javafx-controls:$jfxVersion:linux")
    api("org.openjfx:javafx-controls:$jfxVersion:mac")
    api("org.openjfx:javafx-controls:$jfxVersion:win")
    api("org.openjfx:javafx-graphics:$jfxVersion")
    api("org.openjfx:javafx-graphics:$jfxVersion:linux")
    api("org.openjfx:javafx-graphics:$jfxVersion:mac")
    api("org.openjfx:javafx-graphics:$jfxVersion:win")
    api("org.openjfx:javafx-media:$jfxVersion")
    api("org.openjfx:javafx-media:$jfxVersion:linux")
    api("org.openjfx:javafx-media:$jfxVersion:mac")
    api("org.openjfx:javafx-media:$jfxVersion:win")
    api("org.openjfx:javafx-swing:$jfxVersion")
    api("org.openjfx:javafx-swing:$jfxVersion:linux")
    api("org.openjfx:javafx-swing:$jfxVersion:mac")
    api("org.openjfx:javafx-swing:$jfxVersion:win")
    api("org.openjfx:javafx-web:$jfxVersion")
    api("org.openjfx:javafx-web:$jfxVersion:linux")
    api("org.openjfx:javafx-web:$jfxVersion:mac")
    api("org.openjfx:javafx-web:$jfxVersion:win")
}
