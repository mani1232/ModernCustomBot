plugins {
    kotlin("jvm") version "2.0.0-Beta1" apply true
    kotlin("plugin.serialization") version "2.0.0-Beta1" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

subprojects {

    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "cc.worldmandia"
    version = findProperty("version")!! as String

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0-RC")
        implementation("com.charleskorn.kaml:kaml-jvm:0.56.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
        implementation("net.dv8tion:JDA:5.0.0-beta.18")

        implementation(kotlin("reflect"))
        testImplementation(kotlin("test"))
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }

    kotlin {
        jvmToolchain(17)
    }
}