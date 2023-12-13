subprojects {
    dependencies {
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.0.0-Beta1")

        // Api
        //api("cc.worldmandia:mcb-api:0.1.0.0")
        api(project(path = ":mcb-api", configuration = "shadow"))
    }
}

tasks {
    shadowJar {
        enabled = false
    }
}