val productName = "ModernMusic"

group = "cc.worldmandia"
version = "0.0.0.1"

dependencies {

}

tasks {
    shadowJar {
        archiveFileName = "$productName-$version-addon.jar"
    }
}