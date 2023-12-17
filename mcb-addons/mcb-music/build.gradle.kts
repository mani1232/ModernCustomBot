val productName = "ModernMusic"

group = "cc.worldmandia"
version = "0.0.0.1"

repositories {
    maven("https://maven.lavalink.dev/releases")
    maven("https://maven.lavalink.dev/snapshots")
}

dependencies {
    implementation("dev.arbjerg:lavalink-client:2.0.0") {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks {
    shadowJar {
        archiveFileName = "$productName-$version-addon.jar"
    }
}