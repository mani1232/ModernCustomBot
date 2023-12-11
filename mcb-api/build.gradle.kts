val productName = findProperty("product-name")!! as String

dependencies {

}

tasks {
    shadowJar {
        archiveFileName = "$productName-$version-api.jar"
    }
}