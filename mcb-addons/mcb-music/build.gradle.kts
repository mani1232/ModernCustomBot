val productName = "ModernMusic"

group = "cc.worldmandia"
version = "0.0.0.1"

repositories {
    maven("https://maven.lavalink.dev/releases")
    maven("https://maven.lavalink.dev/snapshots")
}

dependencies {
    implementation("dev.arbjerg:lavalink-client:425e575403ce612d54f2ab5ef379c09a1129ee9c-SNAPSHOT") {
        exclude(group = "org.jetbrains.kotlin")
    }
}

tasks {
    shadowJar {
        archiveFileName = "$productName-$version-addon.jar"
    }
}