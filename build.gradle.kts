plugins {
    kotlin("jvm") version "2.0.0-Beta2" apply true
    kotlin("plugin.serialization") version "2.0.0-Beta2" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.worldmandia.cc/releases/")
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    kotlin {
        jvmToolchain(17)
    }
}

subprojects {

    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    group = "cc.worldmandia"
    version = findProperty("version")!! as String

    dependencies {
        testImplementation(kotlin("test"))
    }

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}