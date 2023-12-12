import org.jetbrains.kotlin.parsing.parseBoolean

plugins {
    application
}

val productName = findProperty("product-name")!! as String
val development = if (parseBoolean(findProperty("development")!! as String)) {
    "dev"
} else {
    "release"
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0-RC")
    implementation("com.charleskorn.kaml:kaml-jvm:0.56.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
    implementation("net.dv8tion:JDA:5.0.0-beta.18")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation(kotlin("reflect"))

    implementation(project(path = ":mcb-api", configuration = "shadow"))
}

tasks {
    shadowJar {
        archiveFileName = "$productName-$version-$development.jar"
    }
}

application {
    mainClass.set("MainKt")
}