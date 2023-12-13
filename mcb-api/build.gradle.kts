import org.jetbrains.kotlin.parsing.parseBoolean

plugins {
    `maven-publish`
}

val productName = findProperty("product-name")!! as String
val development = parseBoolean(findProperty("development")!! as String)

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0-RC")
    implementation("com.charleskorn.kaml:kaml-jvm:0.56.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
    implementation("net.dv8tion:JDA:5.0.0-beta.18")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation(kotlin("reflect"))
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