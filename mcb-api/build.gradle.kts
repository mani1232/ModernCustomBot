import org.jetbrains.kotlin.parsing.parseBoolean

plugins {
    `maven-publish`
}

val apiVersion = findProperty("api-version")!! as String
val productName = findProperty("product-name")!! as String
val development = parseBoolean(findProperty("development")!! as String)

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.0-RC")
    api("com.charleskorn.kaml:kaml-jvm:0.56.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
    api("net.dv8tion:JDA:5.0.0-beta.18")
    api("ch.qos.logback:logback-classic:1.4.14")

    compileOnly(kotlin("reflect"))
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
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "cc.worldmandia"
            artifactId = "mcb-api"
            version = "${apiVersion}${if (development) "-dev" else ""}"
            from(components["kotlin"])
        }
    }
}