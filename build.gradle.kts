import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25" apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            incremental = true
            jvmTarget = "21"
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.contracts.ExperimentalContracts",
                "-Xopt-in=SuspiciousCollectionReassignment",
                "-Xinline-classes",
                "-Xcontext-receivers"
            )
        }
    }
}
