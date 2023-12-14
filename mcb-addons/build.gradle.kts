subprojects {
    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.0.0-Beta1")

        // Api
        compileOnly(project(path = ":mcb-api"))
    }
}

tasks {
    shadowJar {
        enabled = false
    }
    jar {
        enabled = false
        subprojects.forEach {
            dependsOn(it.tasks.shadowJar)
        }
    }
}