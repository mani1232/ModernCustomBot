import org.jetbrains.kotlin.parsing.parseBoolean

plugins {
    `maven-publish`
}

val productName = findProperty("product-name")!! as String
val development = parseBoolean(findProperty("development")!! as String)

dependencies {

}

tasks {
    shadowJar {
        archiveFileName = "$productName-$version-api.jar"
    }
}

publishing {
    if (!development) {
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
    } else {
        repositories {
            maven {
                name = "WorldMandiaRepository"
                url = uri("https://repo.worldmandia.cc/snapshots")
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
                version = "${project.version}-dev"
                from(components["java"])
            }
        }
    }
}