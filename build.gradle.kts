import org.jetbrains.kotlin.parsing.parseBoolean

plugins {
    kotlin("jvm") version "2.0.0-Beta1"
    kotlin("plugin.serialization") version "2.0.0-Beta1"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cc.worldmandia"
version = findProperty("version")!! as String
val productName = findProperty("product-name")!! as String
val development = if (parseBoolean(findProperty("development")!! as String)) {
    "dev"
} else {
    "release"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
    implementation("com.charleskorn.kaml:kaml-jvm:0.55.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.1")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("net.dv8tion:JDA:5.0.0-beta.18") {
        exclude("opus-java")
    }

    testImplementation(kotlin("test"))
}
tasks {
    test {
        useJUnitPlatform()
    }

    shadowJar {
        archiveFileName = "$productName-$version-$development.jar"
    }
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}