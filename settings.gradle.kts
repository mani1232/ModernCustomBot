pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "ModernCustomBot"
include("mcb-core")
include("mcb-api")
include("mcb-addons")
include("mcb-addons:mcb-music")
findProject(":mcb-addons:mcb-music")?.name = "mcb-music"
