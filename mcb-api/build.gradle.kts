plugins {
    `maven-publish`
}

val productName = findProperty("product-name")!! as String

dependencies {

}

tasks {
    shadowJar {
        archiveFileName = "$productName-$version-api.jar"
    }
}

publishing {
    repositories {
        maven {
            name = "WorldMandiaRepository"
            url = uri("https://repo.worldmandia.cc/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "cc.worldmandia"
            artifactId = "mcb-api"
            version = project.version.toString()
            from(components["java"])
        }
    }
}