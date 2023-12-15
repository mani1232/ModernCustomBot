val productName = "ModernMusic"

group = "cc.worldmandia"
version = "0.0.0.1"

repositories {
    maven("https://maven.lavalink.dev/releases")
    maven("https://maven.lavalink.dev/snapshots")
}

dependencies {
    implementation("dev.arbjerg:lavalink-client:64f8b44e8164f5873ab107f453b58b1a99a8bc13-SNAPSHOT") {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks {
    shadowJar {
        archiveFileName = "$productName-$version-addon.jar"
    }
}