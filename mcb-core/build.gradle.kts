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
    implementation(project(path = ":mcb-api", configuration = "shadow"))

    implementation("ch.qos.logback:logback-classic:1.4.14")
}

tasks {
    shadowJar {
        archiveFileName = "$productName-$version-$development.jar"
    }
}

application {
    mainClass.set("MainKt")
}